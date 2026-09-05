package com.pclogix.lanteacraft.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.pclogix.lanteacraft.LanteaCraft;
import com.pclogix.lanteacraft.block.StargateBaseBlock;
import com.pclogix.lanteacraft.block.entity.StargateBaseBlockEntity;
import com.pclogix.lanteacraft.gate.IrisType;
import com.pclogix.lanteacraft.gate.StargateVariant;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/**
 * Renders the complete assembled Stargate as generated geometry.
 *
 * <p>The multiblock itself is made of blocks, but the visible ring, spinning
 * glyph track, chevrons, event horizon, kawoosh, and iris are all emitted here
 * as quads. Coordinates below are in a local gate space centered on the middle
 * of the ring: X/Y form the face of the gate and Z is the depth coming out of
 * the gate face.</p>
 */
public class StargateBaseRenderer implements BlockEntityRenderer<StargateBaseBlockEntity> {
    private static final ResourceLocation EVENT_HORIZON_TEXTURE = ResourceLocation.fromNamespaceAndPath(LanteaCraft.MODID, "textures/fx/eventhorizon.png");
    private static final ResourceLocation MECHANICAL_IRIS_TEXTURE = ResourceLocation.fromNamespaceAndPath(LanteaCraft.MODID, "textures/fx/iris.png");
    private static final ResourceLocation ENERGY_IRIS_TEXTURE = ResourceLocation.fromNamespaceAndPath(LanteaCraft.MODID, "textures/fx/energy_iris.png");

    // The frame texture is treated as a 16x16 tile atlas. The tile constants
    // below are packed as 0xYX, matching the nibble math in tileU/tileV.
    private static final double FRAME_SHEET_SIZE = 1024.0D;
    private static final double FRAME_TILE_SIZE = FRAME_SHEET_SIZE / 16.0D;
    private static final int OUTER_TEXTURE = 0x04;
    private static final int FACE_TEXTURE = 0x14;
    private static final int INNER_TEXTURE = 0x17;
    private static final int CHEVRON_TEXTURE = 0x05;
    private static final int CHEVRON_LIT_TEXTURE = 0x16;
    private static final String LEGACY_GLYPHS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890-+";

    // The classic gate has 38 addressable glyph positions and 9 chevrons.
    // Keeping the generated mesh segmented the same way makes the rotating
    // glyph strip line up with address symbols.
    private static final int RING_SEGMENTS = 38;
    private static final double RING_SYMBOL_ANGLE = 360.0D / RING_SEGMENTS;
    private static final double RING_SYMBOL_CENTER_OFFSET = RING_SYMBOL_ANGLE / 2.0D;
    private static final int CHEVRONS = 9;

    // Ring radii. The "moving" radius is the inner edge of the spinning glyph
    // ring, while INNER_RADIUS is the beveled inner mouth of the fixed frame.
    private static final double INNER_RADIUS = 2.75D;
    private static final double INNER_MOVING_RADIUS = INNER_RADIUS + 0.15D;
    private static final double OUTER_RADIUS = 3.5D;
    private static final double MID_RADIUS = INNER_MOVING_RADIUS + (OUTER_RADIUS - INNER_MOVING_RADIUS) / 2.0D;
    private static final double RING_DEPTH = 0.15D;

    // Chevron geometry is built once and then rotated around the ring. The
    // width is angular-ish rather than literal degrees: renderChevron starts
    // from a local radial axis and uses this value as the +/- Y spread.
    private static final double CHEVRON_OUTER_RADIUS = OUTER_RADIUS + 1.0D / 16.0D;
    private static final double CHEVRON_WIDTH = 0.5D;
    private static final double CHEVRON_DEPTH = 0.0625D;
    private static final double CHEVRON_ANGLE = 360.0D / CHEVRONS;
    private static final double CHEVRON_ANGLE_OFFSET = -90.0D + CHEVRON_WIDTH;

    private static final int TOP_CHEVRON = 0;
    // Dialed address symbols do not lock chevrons in visual clockwise order.
    // Reserve the top chevron for the final symbol so 7, 8, and 9-chevron
    // dials all finish by locking the top middle chevron.
    private static final int[] PRE_TOP_CHEVRON_LOCK_ORDER = { 8, 7, 6, 3, 2, 1, 5, 4 };
    private static final double SPIN_TICKS = 55.0D;
    private static final double CHEVRON_TICKS = 10.0D;

    // Event horizon and iris are drawn as translucent discs slightly in front
    // of the ring center so they do not z-fight with frame geometry.
    private static final int HORIZON_BANDS = 10;
    private static final double HORIZON_RADIUS = INNER_MOVING_RADIUS - 1.0D / 32.0D;
    private static final double HORIZON_Z = 0.01D;
    private static final float HORIZON_ALPHA = 1F;
    private static final double IRIS_Z = HORIZON_Z + 0.05D;
    private static final double KAWOOSH_TICKS = 18.0D;
    private static final double KAWOOSH_ALPHA = 1D;
    private static final int IRIS_BLADES = 32;
    private static final int CHEVRON_MIN_BLOCK_LIGHT = 6;

    // Trig lookup tables for the ring mesh. The +1 lets callers use i + 1 on
    // the final segment without branching back to zero.
    private static final double[] SIN = new double[RING_SEGMENTS + 1];
    private static final double[] COS = new double[RING_SEGMENTS + 1];

    // The client level clock is periodically corrected from the server. Those
    // corrections are fine for world simulation, but can make a purely visual
    // rotation step backwards or forwards. Keep a presentation clock per gate
    // that is seeded from the synchronized dial state and then runs locally.
    private final Map<DialClockKey, ClientDialClock> dialClocks = new HashMap<>();

    static {
        for (int i = 0; i <= RING_SEGMENTS; i++) {
            double angle = 2.0D * Math.PI * i / RING_SEGMENTS;
            SIN[i] = Math.sin(angle);
            COS[i] = Math.cos(angle);
        }
    }

    public StargateBaseRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(StargateBaseBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        BlockState state = blockEntity.getBlockState();
        if (!state.hasProperty(StargateBaseBlock.ASSEMBLED) || !state.getValue(StargateBaseBlock.ASSEMBLED)) {
            return;
        }

        // Move from block-local origin to gate-local origin. The renderer uses
        // a vertical 7-block-ish ring centered above the base block, then faces
        // it according to the base block direction.
        poseStack.pushPose();
        poseStack.translate(0.5D, 3.5D, 0.5D);
        poseStack.mulPose(Axis.YP.rotationDegrees(-state.getValue(StargateBaseBlock.FACING).toYRot()));

        int frameLight = packedLight;
        int chevronLight = boostBlockLight(packedLight, CHEVRON_MIN_BLOCK_LIGHT);
        int effectLight = LightTexture.FULL_BRIGHT;
        DialingRenderState dialingState = dialingState(blockEntity, partialTick);
        StargateVariant variant = variant(state);

        // The bottom row belongs visually to the multiblock footprint rather
        // than the generated ring. Render any camouflage first so the custom
        // geometry can sit cleanly on top.
        renderBottomCamouflage(blockEntity, poseStack, bufferSource, packedLight, packedOverlay);

        // Static gate frame and chevrons share the frame atlas.
        VertexConsumer frame = bufferSource.getBuffer(RenderType.entitySolid(frameTexture(variant)));
        renderShell(poseStack, frame, frameLight);
        renderChevrons(poseStack, frame, chevronLight, dialingState);

        // The glyph strip is a separate cutout texture so it can rotate as one
        // band while the rest of the frame stays still.
        VertexConsumer glyphs = bufferSource.getBuffer(RenderType.entityCutout(glyphTexture(variant)));
        poseStack.pushPose();
        poseStack.mulPose(Axis.ZP.rotationDegrees((float)dialingState.ringRotation()));
        renderGlyphRing(poseStack, glyphs, frameLight);
        poseStack.popPose();

        // Translucent effects are rendered last in front of the frame.
        VertexConsumer horizon = bufferSource.getBuffer(RenderType.entityTranslucent(EVENT_HORIZON_TEXTURE));
        renderEventHorizon(blockEntity, partialTick, poseStack, horizon, effectLight);
        renderIris(blockEntity, partialTick, poseStack, bufferSource, variant, effectLight);

        poseStack.popPose();
    }

    @Override
    public boolean shouldRenderOffScreen(StargateBaseBlockEntity blockEntity) {
        return true;
    }

    @Override
    public int getViewDistance() {
        return 128;
    }

    @Override
    public boolean shouldRender(StargateBaseBlockEntity blockEntity, Vec3 cameraPos) {
        return gateBounds(blockEntity).distanceToSqr(cameraPos) < getViewDistance() * getViewDistance();
    }

    @Override
    public AABB getRenderBoundingBox(StargateBaseBlockEntity blockEntity) {
        return gateBounds(blockEntity);
    }

    private AABB gateBounds(StargateBaseBlockEntity blockEntity) {
        // The base block is only one piece of the multiblock, so inflate upward
        // and outward enough to keep the generated ring/effects from culling.
        return new AABB(blockEntity.getBlockPos()).inflate(5.0D, 5.0D, 5.0D).expandTowards(0.0D, 6.0D, 0.0D);
    }

    private StargateVariant variant(BlockState state) {
        return state.getBlock() instanceof StargateBaseBlock baseBlock ? baseBlock.variant() : StargateVariant.MILKY_WAY;
    }

    private ResourceLocation frameTexture(StargateVariant variant) {
        return ResourceLocation.fromNamespaceAndPath(LanteaCraft.MODID, "textures/tileentity/stargate" + variant.textureSuffix() + ".png");
    }

    private ResourceLocation glyphTexture(StargateVariant variant) {
        return ResourceLocation.fromNamespaceAndPath(LanteaCraft.MODID, "textures/tileentity/stargate_glyphs" + variant.textureSuffix() + ".png");
    }

    private void renderBottomCamouflage(StargateBaseBlockEntity blockEntity, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        BlockState camouflage = blockEntity.bottomCamouflage();
        if (camouflage == null) {
            return;
        }

        // Draw seven camouflage blocks across the lower span of the gate. The
        // slight scale bump avoids cracks/z-fighting against neighboring gate
        // blocks when the block model and generated renderer overlap exactly.
        for (int x = -3; x <= 3; x++) {
            poseStack.pushPose();
            poseStack.translate(x, -3.0D, 0.0D);
            BlockState gateState = blockEntity.getBlockState();
            if (gateState.hasProperty(StargateBaseBlock.FACING)) {
                poseStack.mulPose(Axis.YP.rotationDegrees(gateState.getValue(StargateBaseBlock.FACING).toYRot()));
            }
            poseStack.scale(1.004F, 1.004F, 1.004F);
            poseStack.translate(-0.5D, -0.5D, -0.5D);
            renderCamouflageBlock(blockEntity, camouflage, camouflagePos(blockEntity, x), poseStack, bufferSource, packedLight, packedOverlay);
            poseStack.popPose();
        }
    }

    private BlockPos camouflagePos(StargateBaseBlockEntity blockEntity, int x) {
        BlockState state = blockEntity.getBlockState();
        if (state.hasProperty(StargateBaseBlock.FACING)) {
            Direction right = state.getValue(StargateBaseBlock.FACING).getClockWise();
            return blockEntity.getBlockPos().relative(right, x);
        }

        return blockEntity.getBlockPos().offset(x, 0, 0);
    }

    private void renderCamouflageBlock(StargateBaseBlockEntity blockEntity, BlockState camouflage, BlockPos pos, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        Level level = blockEntity.getLevel();
        BlockRenderDispatcher blockRenderer = Minecraft.getInstance().getBlockRenderer();
        if (level == null) {
            blockRenderer.renderSingleBlock(camouflage, poseStack, bufferSource, packedLight, packedOverlay);
            return;
        }

        RenderType renderType = ItemBlockRenderTypes.getChunkRenderType(camouflage);
        VertexConsumer consumer = bufferSource.getBuffer(renderType);
        // The real block at this position is a Stargate component, not the
        // camouflage state. Vanilla ambient occlusion would therefore sample
        // the hidden gate and paint dark seams/corners onto otherwise ordinary
        // full-cube models (sandstone makes the artifact especially obvious).
        // Keep the world-aware model/tint path, but use flat face lighting so
        // the disguised block is not shaded by geometry it is replacing.
        blockRenderer.getModelRenderer().tesselateWithoutAO(
                level,
                blockRenderer.getBlockModel(camouflage),
                camouflage,
                pos,
                poseStack,
                consumer,
                false,
                RandomSource.create(camouflage.getSeed(pos)),
                camouflage.getSeed(pos),
                packedOverlay
        );
    }

    private void renderShell(PoseStack poseStack, VertexConsumer consumer, int packedLight) {
        PoseStack.Pose pose = poseStack.last();
        double bevelDepth = RING_DEPTH - 1.0D / 16.0D;

        // Build the frame as eight quad strips per segment:
        // outer wall, inner wall, front/back radial faces, and bevels between
        // the fixed frame and the inset spinning glyph ring. Different shades
        // fake basic lighting because normals are intentionally simple.
        for (int i = 0; i < RING_SEGMENTS; i++) {
            // Outer cylindrical rim.
            quad(pose, consumer, packedLight,
                    OUTER_RADIUS * COS[i], OUTER_RADIUS * SIN[i], RING_DEPTH, 0, 0,
                    OUTER_RADIUS * COS[i], OUTER_RADIUS * SIN[i], -RING_DEPTH, 0, 1,
                    OUTER_RADIUS * COS[i + 1], OUTER_RADIUS * SIN[i + 1], -RING_DEPTH, 1, 1,
                    OUTER_RADIUS * COS[i + 1], OUTER_RADIUS * SIN[i + 1], RING_DEPTH, 1, 0,
                    OUTER_TEXTURE,
                    0.85F, 0.85F, 0.85F);

            // Inner cylindrical mouth around the event horizon.
            quad(pose, consumer, packedLight,
                    INNER_RADIUS * COS[i], INNER_RADIUS * SIN[i], bevelDepth, 0, 0,
                    INNER_RADIUS * COS[i + 1], INNER_RADIUS * SIN[i + 1], bevelDepth, 1, 0,
                    INNER_RADIUS * COS[i + 1], INNER_RADIUS * SIN[i + 1], -bevelDepth, 1, 1,
                    INNER_RADIUS * COS[i], INNER_RADIUS * SIN[i], -bevelDepth, 0, 1,
                    INNER_TEXTURE,
                    0.75F, 0.75F, 0.75F);

            // Middle vertical wall that visually separates outer frame and
            // spinning glyph track.
            quad(pose, consumer, packedLight,
                    MID_RADIUS * COS[i], MID_RADIUS * SIN[i], RING_DEPTH, 0, 0,
                    MID_RADIUS * COS[i + 1], MID_RADIUS * SIN[i + 1], RING_DEPTH, 16, 0,
                    MID_RADIUS * COS[i + 1], MID_RADIUS * SIN[i + 1], -RING_DEPTH, 16, 16,
                    MID_RADIUS * COS[i], MID_RADIUS * SIN[i], -RING_DEPTH, 0, 16,
                    FACE_TEXTURE,
                    0.85F, 0.85F, 0.85F);

            // Inner edge of the spinning glyph track.
            quad(pose, consumer, packedLight,
                    INNER_MOVING_RADIUS * COS[i], INNER_MOVING_RADIUS * SIN[i], RING_DEPTH, 0, 0,
                    INNER_MOVING_RADIUS * COS[i], INNER_MOVING_RADIUS * SIN[i], -RING_DEPTH, 0, 16,
                    INNER_MOVING_RADIUS * COS[i + 1], INNER_MOVING_RADIUS * SIN[i + 1], -RING_DEPTH, 16, 16,
                    INNER_MOVING_RADIUS * COS[i + 1], INNER_MOVING_RADIUS * SIN[i + 1], RING_DEPTH, 16, 0,
                    FACE_TEXTURE,
                    0.85F, 0.85F, 0.85F);

            // Front face from middle wall out to the outside rim.
            quad(pose, consumer, packedLight,
                    MID_RADIUS * COS[i], MID_RADIUS * SIN[i], RING_DEPTH, 16, 16,
                    OUTER_RADIUS * COS[i], OUTER_RADIUS * SIN[i], RING_DEPTH, 16, 0,
                    OUTER_RADIUS * COS[i + 1], OUTER_RADIUS * SIN[i + 1], RING_DEPTH, 0, 0,
                    MID_RADIUS * COS[i + 1], MID_RADIUS * SIN[i + 1], RING_DEPTH, 0, 16,
                    FACE_TEXTURE,
                    1.0F, 1.0F, 1.0F);

            // Front bevel down into the inner mouth.
            quad(pose, consumer, packedLight,
                    INNER_RADIUS * COS[i], INNER_RADIUS * SIN[i], bevelDepth, 16, 16,
                    INNER_MOVING_RADIUS * COS[i], INNER_MOVING_RADIUS * SIN[i], RING_DEPTH, 16, 0,
                    INNER_MOVING_RADIUS * COS[i + 1], INNER_MOVING_RADIUS * SIN[i + 1], RING_DEPTH, 0, 0,
                    INNER_RADIUS * COS[i + 1], INNER_RADIUS * SIN[i + 1], bevelDepth, 0, 16,
                    FACE_TEXTURE,
                    1.0F, 1.0F, 1.0F);

            // Back bevel down into the inner mouth.
            quad(pose, consumer, packedLight,
                    INNER_RADIUS * COS[i], INNER_RADIUS * SIN[i], -bevelDepth, 0, 16,
                    INNER_RADIUS * COS[i + 1], INNER_RADIUS * SIN[i + 1], -bevelDepth, 16, 16,
                    INNER_MOVING_RADIUS * COS[i + 1], INNER_MOVING_RADIUS * SIN[i + 1], -RING_DEPTH, 16, 0,
                    INNER_MOVING_RADIUS * COS[i], INNER_MOVING_RADIUS * SIN[i], -RING_DEPTH, 0, 0,
                    FACE_TEXTURE,
                    0.55F, 0.55F, 0.55F);

            // Back face across the ring body.
            quad(pose, consumer, packedLight,
                    INNER_MOVING_RADIUS * COS[i], INNER_MOVING_RADIUS * SIN[i], -RING_DEPTH, 0, 16,
                    INNER_MOVING_RADIUS * COS[i + 1], INNER_MOVING_RADIUS * SIN[i + 1], -RING_DEPTH, 16, 16,
                    OUTER_RADIUS * COS[i + 1], OUTER_RADIUS * SIN[i + 1], -RING_DEPTH, 16, 0,
                    OUTER_RADIUS * COS[i], OUTER_RADIUS * SIN[i], -RING_DEPTH, 0, 0,
                    FACE_TEXTURE,
                    0.55F, 0.55F, 0.55F);
        }
    }

    private void renderGlyphRing(PoseStack poseStack, VertexConsumer consumer, int packedLight) {
        PoseStack.Pose pose = poseStack.last();
        // Nudge this strip toward the camera and slightly over the neighboring
        // radii so the glyph texture covers joins in the procedural shell.
        double inner = INNER_MOVING_RADIUS - 1.0D / 128.0D;
        double outer = MID_RADIUS + 1.0D / 128.0D;
        double z = RING_DEPTH - 1.0D / 64.0D;

        for (int i = 0; i < RING_SEGMENTS; i++) {
            // U runs around the circle; V samples only the upper part of the
            // glyph texture where the strip artwork lives.
            float u0 = (float)i / RING_SEGMENTS;
            float u1 = (float)(i + 1) / RING_SEGMENTS;
            quad(pose, consumer, packedLight,
                    inner * COS[i], inner * SIN[i], z, u1, 0.66,
                    outer * COS[i], outer * SIN[i], z, u1, 0,
                    outer * COS[i + 1], outer * SIN[i + 1], z, u0, 0,
                    inner * COS[i + 1], inner * SIN[i + 1], z, u0, 0.66,
                    -1,
                    1.0F, 1.0F, 1.0F);
        }
    }

    private void renderChevrons(PoseStack poseStack, VertexConsumer consumer, int packedLight, DialingRenderState dialingState) {
        for (int i = 0; i < CHEVRONS; i++) {
            poseStack.pushPose();
            // renderChevron builds a single chevron centered along local +X;
            // rotate that local geometry into each chevron's ring position.
            poseStack.mulPose(Axis.ZP.rotationDegrees((float)(i * CHEVRON_ANGLE - CHEVRON_ANGLE_OFFSET)));
            double lock = dialingState.chevronLock(i);
            // Locking pulls the chevron slightly inward to give the address
            // sequence a visible mechanical "clunk".
            poseStack.translate(-lock / 8.0D, 0.0D, 0.0D);
            renderChevron(poseStack.last(), consumer, packedLight, lock);
            poseStack.popPose();
        }
    }

    private void renderIris(StargateBaseBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, StargateVariant variant, int packedLight) {
        double progress = blockEntity.irisProgress(partialTick);
        if (progress <= 0.0D) {
            return;
        }

        // Energy iris is a simple translucent disc that scales closed/open.
        // Mechanical iris uses overlapping blades below.
        if (blockEntity.irisType() == IrisType.ENERGY) {
            VertexConsumer energy = bufferSource.getBuffer(RenderType.entityTranslucent(ENERGY_IRIS_TEXTURE));
            renderTexturedIrisDisc(poseStack.last(), energy, packedLight, progress, HORIZON_RADIUS, IRIS_Z, 0.88F);
            return;
        }

        VertexConsumer mechanical = bufferSource.getBuffer(RenderType.entityTranslucent(MECHANICAL_IRIS_TEXTURE));
        renderMechanicalIris(poseStack.last(), mechanical, packedLight, progress);
    }

    private void renderMechanicalIris(PoseStack.Pose pose, VertexConsumer consumer, int packedLight, double closedProgress) {
        // closedProgress is 1 when shut, but the blade math is easier to read
        // as "how open are we?" because blades get longer/flatter while closing.
        double bladeOpenProgress = 1.0D - closedProgress;
        double tiltAngle = Math.toRadians(bladeOpenProgress * 60.0D);
        double radius = MID_RADIUS;
        double longWidth = MID_RADIUS;
        double shortWidth = INNER_RADIUS;
        double heightClosed = 0.2D;
        double heightOpen = 1.0D;
        double length = heightOpen - (heightOpen - heightClosed) * bladeOpenProgress * bladeOpenProgress;
        double depth = IRIS_Z;
        double bevel = 0.004D;

        for (int i = 0; i < IRIS_BLADES; i++) {
            double angle = 2.0D * Math.PI * i / IRIS_BLADES;
            // Define one blade in a local radial coordinate system, then rotate
            // it around the gate. p1/p2 are lifted by a tiny bevel so the blade
            // has a little depth instead of reading as a perfectly flat fan.
            IrisPoint p0 = irisPoint(angle, tiltAngle, radius, -longWidth, 0.0D, depth);
            IrisPoint p1 = irisPoint(angle, tiltAngle, radius, 0.0D, 0.0D, depth + bevel);
            IrisPoint p2 = irisPoint(angle, tiltAngle, radius, 0.0D, heightClosed, depth + bevel);
            IrisPoint p3 = irisPoint(angle, tiltAngle, radius, -longWidth + shortWidth, length, depth);
            IrisPoint p4 = irisPoint(angle, tiltAngle, radius, -longWidth, length, depth);

            irisBladeQuad(pose, consumer, packedLight, p0, p1, p2, p3, 0.82F);
            irisBladeQuad(pose, consumer, packedLight, p0, p3, p4, p0, 0.72F);
        }
    }

    private void irisBladeQuad(PoseStack.Pose pose, VertexConsumer consumer, int packedLight, IrisPoint p0, IrisPoint p1, IrisPoint p2, IrisPoint p3, float shade) {
        irisVertex(pose, consumer, packedLight, p0.x(), p0.y(), p0.z(), HORIZON_RADIUS, 1.0F, shade);
        irisVertex(pose, consumer, packedLight, p1.x(), p1.y(), p1.z(), HORIZON_RADIUS, 1.0F, shade);
        irisVertex(pose, consumer, packedLight, p2.x(), p2.y(), p2.z(), HORIZON_RADIUS, 1.0F, shade);
        irisVertex(pose, consumer, packedLight, p3.x(), p3.y(), p3.z(), HORIZON_RADIUS, 1.0F, shade);
    }

    private void renderTexturedIrisDisc(PoseStack.Pose pose, VertexConsumer consumer, int packedLight, double progress, double radius, double z, float alpha) {
        double scaledRadius = radius * progress;
        if (scaledRadius <= 0.05D) {
            return;
        }

        // Draw as concentric bands instead of one triangle fan so UVs remain
        // stable and the texture does not pinch too hard at the center.
        for (int band = 0; band < HORIZON_BANDS; band++) {
            double r0 = scaledRadius * band / HORIZON_BANDS;
            double r1 = scaledRadius * (band + 1) / HORIZON_BANDS;

            for (int i = 0; i < RING_SEGMENTS; i++) {
                irisVertex(pose, consumer, packedLight, r0 * COS[i], r0 * SIN[i], z, radius, alpha, 1.0F);
                irisVertex(pose, consumer, packedLight, r1 * COS[i], r1 * SIN[i], z, radius, alpha, 1.0F);
                irisVertex(pose, consumer, packedLight, r1 * COS[i + 1], r1 * SIN[i + 1], z, radius, alpha, 1.0F);
                irisVertex(pose, consumer, packedLight, r0 * COS[i + 1], r0 * SIN[i + 1], z, radius, alpha, 1.0F);
            }
        }
    }

    private void irisVertex(PoseStack.Pose pose, VertexConsumer consumer, int packedLight, double x, double y, double z, double uvRadius, float alpha, float shade) {
        consumer.addVertex(pose, (float)x, (float)y, (float)z)
                .setColor(shade, shade, shade, alpha)
                // Convert gate-local coordinates into disc texture space:
                // center of the gate -> 0.5/0.5, edge -> roughly 0 or 1.
                .setUv((float)(0.5D + x / (uvRadius * 2.0D)), (float)(0.5D + y / (uvRadius * 2.0D)))
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(packedLight)
                .setNormal(0.0F, 0.0F, 1.0F);
    }

    private IrisPoint irisPoint(double bladeAngle, double tiltAngle, double radius, double localX, double localY, double z) {
        // First tilt the blade shape in its own local 2D plane, then translate
        // it out to the ring radius and rotate the whole blade around center.
        double tiltedX = localX * Math.cos(-tiltAngle) - localY * Math.sin(-tiltAngle);
        double tiltedY = localX * Math.sin(-tiltAngle) + localY * Math.cos(-tiltAngle);
        double x = radius + tiltedX;
        double y = tiltedY;
        double cos = Math.cos(bladeAngle);
        double sin = Math.sin(bladeAngle);
        return new IrisPoint(x * cos - y * sin, x * sin + y * cos, z);
    }

    private void renderChevron(PoseStack.Pose pose, VertexConsumer consumer, int packedLight, double lock) {
        // Coordinates here are local to a chevron whose point faces +X. The
        // caller rotates this geometry into its position around the ring.
        double r1 = MID_RADIUS - 1.0D / 18.0D;
        double r2 = CHEVRON_OUTER_RADIUS;
        double zFront = RING_DEPTH + CHEVRON_DEPTH;
        double x1 = r1;
        double x2 = r2;
        double y1 = CHEVRON_WIDTH / 4.0D;
        double y2 = CHEVRON_WIDTH / 2.0D;
        double inset = CHEVRON_WIDTH / 6.0D;
        double litZ = zFront + 1.0D / 256.0D;

        // Two dark/metal quads form the split V-shaped chevron body.
        quad(pose, consumer, packedLight,
                x2, y2, zFront, 0, 0,
                x1, y1, zFront, 0, 16,
                x1 + inset, 0, zFront, 8, 12,
                x2, y2 - inset, zFront, 4, 2,
                CHEVRON_TEXTURE,
                1.0F, 1.0F, 1.0F);
        quad(pose, consumer, packedLight,
                x2, -y2 + inset, zFront, 12, 2,
                x1 + inset, 0, zFront, 8, 12,
                x1, -y1, zFront, 16, 16,
                x2, -y2, zFront, 16, 0,
                CHEVRON_TEXTURE,
                1.0F, 1.0F, 1.0F);

        // The glow never goes fully black, then ramps up as the chevron locks.
        float lit = (float)(0.28D + Math.min(lock, 1.0D) * 0.72D);
        quad(pose, consumer, packedLight,
                x2, y2 - inset, litZ, 0, 4,
                x1 + inset, y1 - inset, litZ, 4, 16,
                x1 + inset, 0, litZ, 8, 16,
                x2, 0, litZ, 8, 4,
                CHEVRON_LIT_TEXTURE,
                lit, lit, lit);
        quad(pose, consumer, packedLight,
                x2, 0, litZ, 8, 4,
                x1 + inset, 0, litZ, 8, 16,
                x1 + inset, -y1 + inset, litZ, 12, 16,
                x2, -y2 + inset, litZ, 16, 4,
                CHEVRON_LIT_TEXTURE,
                lit, lit, lit);
    }

    private void renderEventHorizon(StargateBaseBlockEntity blockEntity, float partialTick, PoseStack poseStack, VertexConsumer consumer, int packedLight) {
        if (blockEntity.getLevel() == null || !blockEntity.isConnected()) {
            return;
        }

        // While dialing, the ring/chevrons provide the visual feedback. The
        // actual puddle appears only once the connection is established.
        if (blockEntity.isDialing(blockEntity.getLevel().getGameTime())) {
            return;
        }

        double animationTime = presentationElapsed(
                blockEntity,
                dialClockKey(blockEntity),
                blockEntity.dialingAddress(),
                partialTick);
        double openElapsed = animationTime - blockEntity.dialingDurationTicks();
        PoseStack.Pose pose = poseStack.last();
        // The kawoosh is a short-lived protrusion that plays at connection
        // open, unless the iris is already blocking the front of the gate.
        if (!blockEntity.isIrisObstructing() && openElapsed >= 0.0D && openElapsed <= KAWOOSH_TICKS) {
            renderKawoosh(pose, consumer, packedLight, animationTime, openElapsed / KAWOOSH_TICKS);
        }

        // The steady-state event horizon is a rippled translucent disc.
        renderHorizonDisc(pose, consumer, packedLight, animationTime, 1.0D, HORIZON_RADIUS, HORIZON_Z, HORIZON_ALPHA);
    }

    private void renderHorizonDisc(PoseStack.Pose pose, VertexConsumer consumer, int packedLight, double gameTime, double scale, double radius, double baseZ, float alpha) {
        // The horizon is split into radial bands and angular segments so each
        // vertex can have its own Z offset, giving the surface a liquid ripple.
        for (int band = 0; band < HORIZON_BANDS; band++) {
            double r0 = radius * scale * band / HORIZON_BANDS;
            double r1 = radius * scale * (band + 1) / HORIZON_BANDS;

            for (int i = 0; i < RING_SEGMENTS; i++) {
                // Sample all four corners independently. This keeps the wave
                // continuous across band/segment boundaries because adjacent
                // quads reuse the same band/segment inputs.
                double z00 = baseZ + horizonRipple(gameTime, band, i);
                double z01 = baseZ + horizonRipple(gameTime, band + 1, i);
                double z11 = baseZ + horizonRipple(gameTime, band + 1, i + 1);
                double z10 = baseZ + horizonRipple(gameTime, band, i + 1);

                horizonQuad(pose, consumer, packedLight,
                        r0 * COS[i], r0 * SIN[i], z00,
                        r1 * COS[i], r1 * SIN[i], z01,
                        r1 * COS[i + 1], r1 * SIN[i + 1], z11,
                        r0 * COS[i + 1], r0 * SIN[i + 1], z10,
                        radius * scale, alpha, gameTime);
            }
        }
    }

    private void renderKawoosh(PoseStack.Pose pose, VertexConsumer consumer, int packedLight, double gameTime, double progress) {
        // The first 28% blooms outward quickly; the remainder fades/collapses.
        // ease(bloom) avoids a harsh start at the exact opening tick.
        double bloom = progress < 0.28D ? progress / 0.28D : 1.0D;
        double radius = HORIZON_RADIUS * Math.min(1.0D, ease(bloom) * 1.18D);
        float alpha = (float)(KAWOOSH_ALPHA * Math.pow(Math.max(0.0D, 1.0D - progress), 0.7D));

        if (radius <= 0.05D || alpha <= 0.0F) {
            return;
        }

        for (int band = 0; band < HORIZON_BANDS; band++) {
            // f0/f1 are normalized radial positions. The displacement profile
            // uses them to make the center jet out farther than the rim.
            double f0 = (double)band / HORIZON_BANDS;
            double f1 = (double)(band + 1) / HORIZON_BANDS;
            double r0 = radius * f0;
            double r1 = radius * f1;

            for (int i = 0; i < RING_SEGMENTS; i++) {
                double z00 = HORIZON_Z + kawooshDisplacement(gameTime, progress, f0, i);
                double z01 = HORIZON_Z + kawooshDisplacement(gameTime, progress, f1, i);
                double z11 = HORIZON_Z + kawooshDisplacement(gameTime, progress, f1, i + 1);
                double z10 = HORIZON_Z + kawooshDisplacement(gameTime, progress, f0, i + 1);

                horizonQuad(pose, consumer, packedLight,
                        r0 * COS[i], r0 * SIN[i], z00,
                        r1 * COS[i], r1 * SIN[i], z01,
                        r1 * COS[i + 1], r1 * SIN[i + 1], z11,
                        r0 * COS[i + 1], r0 * SIN[i + 1], z10,
                        HORIZON_RADIUS, alpha, gameTime);
            }
        }
    }

    private double horizonRipple(double gameTime, int band, int segment) {
        int wrappedSegment = Math.floorMod(segment, RING_SEGMENTS);
        double radial = (double)band / HORIZON_BANDS;
        // Fade toward the outside so the puddle edge stays relatively stable
        // against the frame instead of visibly clipping through it.
        double edgeFade = 1.0D - radial * 0.55D;
        double angle = 2.0D * Math.PI * wrappedSegment / RING_SEGMENTS;
        // Two low-amplitude waves moving at different speeds/directions create
        // an organic wobble without needing a noise texture or per-frame state.
        return (Math.sin(gameTime * 0.1D + band * 1.7D + angle * 3.0D)
                + Math.cos(gameTime * 0.07D + band * 0.9D - angle * 2.0D) * 0.45D)
                * 0.018D * edgeFade;
    }

    private double kawooshDisplacement(double gameTime, double progress, double radialFraction, int segment) {
        int wrappedSegment = Math.floorMod(segment, RING_SEGMENTS);
        double angle = 2.0D * Math.PI * wrappedSegment / RING_SEGMENTS;
        // bloom controls the initial expansion; collapse controls the later
        // retraction. They deliberately split at the same 28% used for radius.
        double bloom = progress < 0.28D ? progress / 0.28D : 1.0D;
        double collapse = progress < 0.28D ? 1.0D : 1.0D - (progress - 0.28D) / 0.72D;
        double protrude = 3.25D * ease(bloom) * Math.pow(Math.max(0.0D, collapse), 0.45D);
        // Add a small animated surface wave on top of the large forward jet.
        double wave = Math.sin(gameTime * 0.45D + angle * 5.5D + radialFraction * 4.0D) * 0.04D * (1.0D - radialFraction);
        return protrude * kawooshProfile(radialFraction) + wave;
    }

    private double kawooshProfile(double radialFraction) {
        // Strong center spike plus a subtle rim bulge makes the shape read more
        // like an erupting membrane and less like a flat cone.
        double centerJet = Math.pow(1.0D - radialFraction, 2.55D);
        double rimBulge = Math.sin(radialFraction * Math.PI) * 0.18D;
        return centerJet + rimBulge;
    }

    private void horizonQuad(
            PoseStack.Pose pose,
            VertexConsumer consumer,
            int packedLight,
            double x1, double y1, double z1,
            double x2, double y2, double z2,
            double x3, double y3, double z3,
            double x4, double y4, double z4,
            double uvRadius,
            float alpha,
            double gameTime) {
        // Horizon quads use animated UVs, unlike frame quads, so the texture
        // appears to swirl independently of the mesh displacement.
        horizonVertex(pose, consumer, packedLight, x1, y1, z1, animatedU(x1, y1, uvRadius, gameTime), animatedV(x1, y1, uvRadius, gameTime), alpha);
        horizonVertex(pose, consumer, packedLight, x2, y2, z2, animatedU(x2, y2, uvRadius, gameTime), animatedV(x2, y2, uvRadius, gameTime), alpha);
        horizonVertex(pose, consumer, packedLight, x3, y3, z3, animatedU(x3, y3, uvRadius, gameTime), animatedV(x3, y3, uvRadius, gameTime), alpha);
        horizonVertex(pose, consumer, packedLight, x4, y4, z4, animatedU(x4, y4, uvRadius, gameTime), animatedV(x4, y4, uvRadius, gameTime), alpha);
    }

    private double animatedU(double x, double y, double radius, double gameTime) {
        double radial = Math.sqrt(x * x + y * y);
        double angle = Math.atan2(y, x);
        // Start from a normal disc projection, then perturb along radial and
        // angular waves to keep the puddle texture alive while connected.
        double pulse = Math.sin(gameTime * 0.13D - radial * 4.2D) * 0.012D;
        double shimmer = Math.sin(gameTime * 0.19D + angle * 7.0D + radial * 2.0D) * 0.006D;
        return 0.5D + x / (radius * 2.0D) + Math.cos(angle) * pulse + Math.sin(angle * 3.0D) * shimmer;
    }

    private double animatedV(double x, double y, double radius, double gameTime) {
        double radial = Math.sqrt(x * x + y * y);
        double angle = Math.atan2(y, x);
        // Different phase/speeds from U avoid a simple back-and-forth slide.
        double pulse = Math.cos(gameTime * 0.12D - radial * 3.8D) * 0.012D;
        double shimmer = Math.cos(gameTime * 0.17D - angle * 6.0D + radial * 2.4D) * 0.006D;
        return 0.5D + y / (radius * 2.0D) + Math.sin(angle) * pulse + Math.cos(angle * 2.0D) * shimmer;
    }

    private DialingRenderState dialingState(StargateBaseBlockEntity blockEntity, float partialTick) {
        if (blockEntity.getLevel() == null) {
            return connectedState(blockEntity);
        }

        DialClockKey clockKey = dialClockKey(blockEntity);
        if (!blockEntity.isDialing(blockEntity.getLevel().getGameTime())) {
            // Keep the completed dial clock while the wormhole is open: the
            // kawoosh and event horizon continue from this same smooth timeline.
            if (!blockEntity.isConnected()) {
                dialClocks.remove(clockKey);
            }
            return connectedState(blockEntity);
        }

        String address = blockEntity.dialingAddress();
        double elapsed = presentationElapsed(blockEntity, clockKey, address, partialTick);
        double[] chevrons = new double[CHEVRONS];
        double ringRotation = 0.0D;
        double previousRotation = 0.0D;

        int addressLength = Math.min(address.length(), CHEVRONS);
        if (addressLength == 0) {
            return new DialingRenderState(ringRotation, chevrons);
        }

        // A mechanical Milky Way dial alternates direction for every symbol.
        // Pick the first direction from the shortest initial movement, then
        // deliberately reverse after each lock. The remaining rotations must
        // honour that direction even when it means taking nearly a full turn.
        double firstTargetRotation = symbolRotation(address.charAt(0), 0, addressLength);
        boolean firstSpinPositive = shortestAngleDelta(0.0D, firstTargetRotation) >= 0.0D;

        // Each address symbol gets a spin window followed by a chevron lock
        // window. Later symbols start after the previous symbol's total window,
        // so the dialing animation advances one glyph/chevron at a time.
        for (int i = 0; i < addressLength; i++) {
            double symbolStart = i * (SPIN_TICKS + CHEVRON_TICKS);
            double spinProgress = clamp((elapsed - symbolStart) / SPIN_TICKS);
            double targetRotation = symbolRotation(address.charAt(i), i, addressLength);
            boolean spinPositive = (i & 1) == 0 ? firstSpinPositive : !firstSpinPositive;
            double directedTargetRotation = directedTargetRotation(previousRotation, targetRotation, spinPositive);

            // Keep the ring on the selected side of its current rotation. This
            // is intentionally not a shortest-path interpolation: if the next
            // glyph lies behind the ring, the show-style mechanical dial makes
            // the full remaining trip before locking it.
            if (spinProgress > 0.0D) {
                ringRotation = lerp(previousRotation, directedTargetRotation, ease(spinProgress));
            }

            // Once spinning finishes, the mapped chevron eases into its locked
            // position and glow.
            double lockProgress = clamp((elapsed - symbolStart - SPIN_TICKS) / CHEVRON_TICKS);
            if (lockProgress > 0.0D) {
                chevrons[chevronLockIndex(i, addressLength)] = ease(lockProgress);
            }

            // Completed symbols become the new starting angle for subsequent
            // symbols. Keep this angle unwrapped so the prescribed direction
            // survives the 0/360-degree boundary without snapping backwards.
            if (elapsed >= symbolStart + SPIN_TICKS) {
                previousRotation = directedTargetRotation;
                ringRotation = directedTargetRotation;
            }
        }

        return new DialingRenderState(ringRotation, chevrons);
    }

    private double symbolRotation(char symbol, int addressIndex, int addressLength) {
        int index = LEGACY_GLYPHS.indexOf(symbol);
        if (index < 0) {
            return 0.0D;
        }

        // One glyph per ring segment. Rotate the addressed glyph to the
        // chevron that is locking this address position, instead of treating
        // glyph 0 as an arbitrary absolute ring angle. This preserves the
        // legacy ASCII address format while making the visible ring symbol
        // match the DHD symbol the player pressed.
        double symbolRotation = index * RING_SYMBOL_ANGLE;
        double chevronRotation = chevronRotation(addressIndex, addressLength);
        return normalizeAngle(chevronRotation - symbolRotation - RING_SYMBOL_CENTER_OFFSET);
    }

    private double chevronRotation(int addressIndex, int addressLength) {
        int lockIndex = chevronLockIndex(addressIndex, addressLength);
        return lockIndex * CHEVRON_ANGLE - CHEVRON_ANGLE_OFFSET;
    }

    private int chevronLockIndex(int addressIndex, int addressLength) {
        int finalAddressIndex = Math.max(0, Math.min(addressLength, CHEVRONS) - 1);
        if (addressIndex >= finalAddressIndex) {
            return TOP_CHEVRON;
        }

        return PRE_TOP_CHEVRON_LOCK_ORDER[Math.min(addressIndex, PRE_TOP_CHEVRON_LOCK_ORDER.length - 1)];
    }

    private DialingRenderState connectedState(StargateBaseBlockEntity blockEntity) {
        if (!blockEntity.isConnected()) {
            return DialingRenderState.IDLE;
        }

        String address = blockEntity.connectedAddress();
        double[] chevrons = new double[CHEVRONS];
        // A connected gate displays all chevrons for the stored address as
        // fully locked, even when the active dialing animation is over.
        int addressLength = Math.min(address.length(), CHEVRONS);
        for (int i = 0; i < addressLength; i++) {
            chevrons[chevronLockIndex(i, addressLength)] = 1.0D;
        }

        // Leave the ring parked on the final dialed symbol.
        int finalSymbolIndex = addressLength - 1;
        double ringRotation = address.isEmpty() ? 0.0D : symbolRotation(address.charAt(finalSymbolIndex), finalSymbolIndex, addressLength);
        return new DialingRenderState(ringRotation, chevrons);
    }

    private double shortestAngleDelta(double start, double end) {
        double delta = normalizeAngle(end - start);
        if (delta > 180.0D) {
            delta -= 360.0D;
        }

        return delta;
    }

    private double directedTargetRotation(double start, double target, boolean positive) {
        double positiveDelta = normalizeAngle(target - start);
        // Repeated glyphs still need a visible spin. A zero delta therefore
        // becomes one complete turn in the direction chosen for this symbol.
        if (positiveDelta == 0.0D) {
            return positive ? start + 360.0D : start - 360.0D;
        }

        return positive ? start + positiveDelta : start - (360.0D - positiveDelta);
    }

    private DialClockKey dialClockKey(StargateBaseBlockEntity blockEntity) {
        return new DialClockKey(blockEntity.getLevel().dimension().location(), blockEntity.getBlockPos().asLong());
    }

    private double presentationElapsed(StargateBaseBlockEntity blockEntity, DialClockKey clockKey, String address, float partialTick) {
        double synchronizedElapsed = Math.max(0.0D,
                blockEntity.getLevel().getGameTime() + partialTick - blockEntity.dialingStartGameTime());
        long now = System.nanoTime();
        ClientDialClock clock = dialClocks.get(clockKey);
        if (clock == null
                || clock.dialingStartGameTime() != blockEntity.dialingStartGameTime()
                || clock.dialingDurationTicks() != blockEntity.dialingDurationTicks()
                || !clock.address().equals(address)) {
            clock = new ClientDialClock(
                    blockEntity.dialingStartGameTime(),
                    blockEntity.dialingDurationTicks(),
                    address,
                    synchronizedElapsed,
                    now);
            dialClocks.put(clockKey, clock);
        }

        return clock.elapsedAt(now);
    }

    private double lerp(double start, double end, double progress) {
        return start + (end - start) * progress;
    }

    private double normalizeAngle(double angle) {
        double normalized = angle % 360.0D;
        return normalized < 0.0D ? normalized + 360.0D : normalized;
    }

    private double ease(double progress) {
        // Cosine ease-in/ease-out in [0, 1]. Used for chunky mechanical motion
        // so spins and locks do not start/stop linearly.
        return 0.5D - Math.cos(clamp(progress) * Math.PI) * 0.5D;
    }

    private double clamp(double value) {
        if (value < 0.0D) {
            return 0.0D;
        }

        return Math.min(value, 1.0D);
    }

    private int boostBlockLight(int packedLight, int minBlockLight) {
        int blockLight = (packedLight >> 4) & 0xF;
        int skyLight = (packedLight >> 20) & 0xF;
        return LightTexture.pack(Math.max(blockLight, minBlockLight), skyLight);
    }

    private record DialingRenderState(double ringRotation, double[] chevronLocks) {
        private static final DialingRenderState IDLE = new DialingRenderState(0.0D, new double[CHEVRONS]);

        private double chevronLock(int index) {
            // Defensive bounds check keeps rendering resilient if constants or
            // lock-order data change later.
            return index >= 0 && index < chevronLocks.length ? chevronLocks[index] : 0.0D;
        }
    }

    private record DialClockKey(ResourceLocation dimension, long blockPos) {
    }

    private record ClientDialClock(long dialingStartGameTime, int dialingDurationTicks, String address, double initialElapsed, long startedAtNanos) {
        private double elapsedAt(long now) {
            return initialElapsed + (now - startedAtNanos) / 50_000_000.0D;
        }
    }

    private record IrisPoint(double x, double y, double z) {
    }

    private void quad(
            PoseStack.Pose pose,
            VertexConsumer consumer,
            int packedLight,
            double x1, double y1, double z1, double u1, double v1,
            double x2, double y2, double z2, double u2, double v2,
            double x3, double y3, double z3, double u3, double v3,
            double x4, double y4, double z4, double u4, double v4,
            int tile,
            float red, float green, float blue) {
        // tile >= 0 means the U/V values above are 0..16 pixel coordinates
        // inside a tile of the 16x16 frame atlas. tile < 0 means the caller
        // already supplied normalized UVs, as the glyph strip does.
        if (tile >= 0) {
            u1 = tileU(tile, u1);
            v1 = tileV(tile, v1);
            u2 = tileU(tile, u2);
            v2 = tileV(tile, v2);
            u3 = tileU(tile, u3);
            v3 = tileV(tile, v3);
            u4 = tileU(tile, u4);
            v4 = tileV(tile, v4);
        }

        vertex(pose, consumer, packedLight, x1, y1, z1, u1, v1, red, green, blue);
        vertex(pose, consumer, packedLight, x2, y2, z2, u2, v2, red, green, blue);
        vertex(pose, consumer, packedLight, x3, y3, z3, u3, v3, red, green, blue);
        vertex(pose, consumer, packedLight, x4, y4, z4, u4, v4, red, green, blue);
    }

    private double tileU(int tile, double pixelU) {
        // Low nibble is tile X, pixelU is a 0..16 coordinate inside that tile.
        return ((tile & 0xF) * FRAME_TILE_SIZE + pixelU * (FRAME_TILE_SIZE / 16.0D)) / FRAME_SHEET_SIZE;
    }

    private double tileV(int tile, double pixelV) {
        // High nibble is tile Y, pixelV is a 0..16 coordinate inside that tile.
        return (((tile >> 4) & 0xF) * FRAME_TILE_SIZE + pixelV * (FRAME_TILE_SIZE / 16.0D)) / FRAME_SHEET_SIZE;
    }

    private void vertex(PoseStack.Pose pose, VertexConsumer consumer, int packedLight, double x, double y, double z, double u, double v, float red, float green, float blue) {
        // Frame vertices are opaque and use fixed normals. The manual shade
        // values passed into quad provide the visual depth cues.
        consumer.addVertex(pose, (float)x, (float)y, (float)z)
                .setColor(red, green, blue, 1.0F)
                .setUv((float)u, (float)v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(packedLight)
                .setNormal(0.0F, 0.0F, 1.0F);
    }

    private void horizonVertex(PoseStack.Pose pose, VertexConsumer consumer, int packedLight, double x, double y, double z, double u, double v, float alpha) {
        // Tint the event horizon blue at the vertex level so the texture can
        // remain mostly grayscale/white and still pick up the Stargate color.
        consumer.addVertex(pose, (float)x, (float)y, (float)z)
                .setColor(0.62F, 0.86F, 1.0F, alpha)
                .setUv((float)u, (float)v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(packedLight)
                .setNormal(0.0F, 0.0F, 1.0F);
    }
}
