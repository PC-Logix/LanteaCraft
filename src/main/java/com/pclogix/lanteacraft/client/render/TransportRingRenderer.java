package com.pclogix.lanteacraft.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.pclogix.lanteacraft.LanteaCraft;
import com.pclogix.lanteacraft.block.TransportRingBlock;
import com.pclogix.lanteacraft.block.entity.TransportRingBlockEntity;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class TransportRingRenderer implements BlockEntityRenderer<TransportRingBlockEntity> {
    private static final ResourceLocation RING_TEXTURE = ResourceLocation.fromNamespaceAndPath(LanteaCraft.MODID, "textures/models/transport_rings_64.png");
    private static final ResourceLocation BASE_TEXTURE = ResourceLocation.fromNamespaceAndPath(LanteaCraft.MODID, "textures/models/transport_rings_base_64.png");
    private static final ResourceLocation CONTROL_PANEL_TEXTURE = ResourceLocation.fromNamespaceAndPath(LanteaCraft.MODID, "textures/models/ring_control_panel_64.png");
    private static final ResourceLocation BEAM_TEXTURE = ResourceLocation.fromNamespaceAndPath(LanteaCraft.MODID, "textures/fx/eventhorizon.png");
    private static final ResourceLocation RING_MODEL = ResourceLocation.fromNamespaceAndPath(LanteaCraft.MODID, "models/obj/model_transport_ring.obj");
    private static final int RING_COUNT = 6;
    private static final double ENGAGE_TICKS = 50.0D;
    private static final double TRANSPORT_TICKS = 20.0D;
    private static final double DISENGAGE_TICKS = 50.0D;
    private static final double TOTAL_TICKS = ENGAGE_TICKS + TRANSPORT_TICKS + DISENGAGE_TICKS;
    private static final double RING_OUTER = 2.35D;
    private static final double RING_INNER = 1.55D;
    private static final double RING_THICKNESS = 0.08D;
    private static final double RING_SPACING = 0.5D;
    private static final int SEGMENTS = 48;
    private static ObjMesh ringMesh;
    private static boolean ringMeshLoadAttempted;

    public TransportRingRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(TransportRingBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        if (!blockEntity.isAnimating()) {
            return;
        }

        double age = animationAge(blockEntity, partialTick);
        if (age < 0.0D || age > TOTAL_TICKS) {
            return;
        }

        poseStack.pushPose();
        poseStack.translate(0.5D, 0.08D, 0.5D);
        if (blockEntity.getBlockState().hasProperty(TransportRingBlock.FACING)) {
            poseStack.mulPose(Axis.YP.rotationDegrees(-blockEntity.getBlockState().getValue(TransportRingBlock.FACING).toYRot()));
        }

        int light = LightTexture.FULL_BRIGHT;
        VertexConsumer baseConsumer = bufferSource.getBuffer(RenderType.entitySolid(BASE_TEXTURE));
        renderBase(poseStack.last(), baseConsumer, light);
        VertexConsumer panelConsumer = bufferSource.getBuffer(RenderType.entitySolid(CONTROL_PANEL_TEXTURE));
        renderControlPanel(poseStack.last(), panelConsumer, light);

        VertexConsumer ringConsumer = bufferSource.getBuffer(RenderType.entitySolid(RING_TEXTURE));
        for (int ring = 0; ring < RING_COUNT; ring++) {
            renderRing(poseStack.last(), ringConsumer, light, ringHeight(ring, age), ringAlpha(age));
        }

        if (age >= ENGAGE_TICKS && age <= ENGAGE_TICKS + TRANSPORT_TICKS) {
            VertexConsumer beamConsumer = bufferSource.getBuffer(RenderType.entityTranslucent(BEAM_TEXTURE));
            renderBeam(poseStack.last(), beamConsumer, light, beamAlpha(age));
        }

        poseStack.popPose();
    }

    @Override
    public boolean shouldRenderOffScreen(TransportRingBlockEntity blockEntity) {
        return true;
    }

    @Override
    public int getViewDistance() {
        return 96;
    }

    @Override
    public boolean shouldRender(TransportRingBlockEntity blockEntity, Vec3 cameraPos) {
        return getRenderBoundingBox(blockEntity).distanceToSqr(cameraPos) < getViewDistance() * getViewDistance();
    }

    @Override
    public AABB getRenderBoundingBox(TransportRingBlockEntity blockEntity) {
        BlockPos pos = blockEntity.getBlockPos();
        return new AABB(pos).inflate(4.0D, 5.0D, 4.0D);
    }

    private double animationAge(TransportRingBlockEntity blockEntity, float partialTick) {
        if (blockEntity.getLevel() == null) {
            return -1.0D;
        }

        return blockEntity.getLevel().getGameTime() + partialTick - blockEntity.animationStartGameTime();
    }

    private double ringHeight(int ring, double age) {
        double target = ring * RING_SPACING;
        if (age < ENGAGE_TICKS) {
            double start = ring * 6.0D;
            return target * ease((age - start) / 10.0D);
        }

        if (age <= ENGAGE_TICKS + TRANSPORT_TICKS) {
            return target;
        }

        double collapseAge = age - ENGAGE_TICKS - TRANSPORT_TICKS;
        double start = (RING_COUNT - ring - 1) * 6.0D;
        return target * (1.0D - ease((collapseAge - start) / 10.0D));
    }

    private float ringAlpha(double age) {
        return 1.0F;
    }

    private float beamAlpha(double age) {
        double pulseAge = age - ENGAGE_TICKS;
        return (float)(0.18D + 0.35D * Math.sin(Math.PI * clamp(pulseAge / TRANSPORT_TICKS)));
    }

    private void renderRing(PoseStack.Pose pose, VertexConsumer consumer, int packedLight, double y, float alpha) {
        ObjMesh mesh = ringMesh();
        if (mesh != null) {
            mesh.render(pose, consumer, packedLight, 0.0D, y, 0.0D, alpha);
            return;
        }

        for (int i = 0; i < SEGMENTS; i++) {
            double a0 = Math.toRadians(360.0D * i / SEGMENTS);
            double a1 = Math.toRadians(360.0D * (i + 1) / SEGMENTS);
            double c0 = Math.cos(a0);
            double s0 = Math.sin(a0);
            double c1 = Math.cos(a1);
            double s1 = Math.sin(a1);
            double u0 = (double)i / SEGMENTS;
            double u1 = (double)(i + 1) / SEGMENTS;
            quad(pose, consumer, packedLight,
                    c0 * RING_OUTER, y + RING_THICKNESS, s0 * RING_OUTER, u0, 0.0D,
                    c1 * RING_OUTER, y + RING_THICKNESS, s1 * RING_OUTER, u1, 0.0D,
                    c1 * RING_INNER, y + RING_THICKNESS, s1 * RING_INNER, u1, 1.0D,
                    c0 * RING_INNER, y + RING_THICKNESS, s0 * RING_INNER, u0, 1.0D,
                    1.0F, 1.0F, 1.0F, alpha);
        }
    }

    private ObjMesh ringMesh() {
        if (!ringMeshLoadAttempted) {
            ringMeshLoadAttempted = true;
            ringMesh = ObjMesh.load(RING_MODEL);
        }

        return ringMesh;
    }

    private void renderBase(PoseStack.Pose pose, VertexConsumer consumer, int packedLight) {
        double r = 2.45D;
        quad(pose, consumer, packedLight,
                -r, 0.0D, -r, 0.0D, 0.0D,
                r, 0.0D, -r, 1.0D, 0.0D,
                r, 0.0D, r, 1.0D, 1.0D,
                -r, 0.0D, r, 0.0D, 1.0D,
                1.0F, 1.0F, 1.0F, 1.0F);
    }

    private void renderControlPanel(PoseStack.Pose pose, VertexConsumer consumer, int packedLight) {
        double w = 0.6D;
        double z = -2.48D;
        quad(pose, consumer, packedLight,
                -w, 0.012D, z, 0.0D, 0.0D,
                w, 0.012D, z, 1.0D, 0.0D,
                w, 0.012D, z + 0.45D, 1.0D, 1.0D,
                -w, 0.012D, z + 0.45D, 0.0D, 1.0D,
                1.0F, 1.0F, 1.0F, 1.0F);
    }

    private void renderBeam(PoseStack.Pose pose, VertexConsumer consumer, int packedLight, float alpha) {
        double radius = 2.15D;
        double height = 3.25D;
        for (int i = 0; i < SEGMENTS; i++) {
            double a0 = Math.toRadians(360.0D * i / SEGMENTS);
            double a1 = Math.toRadians(360.0D * (i + 1) / SEGMENTS);
            double c0 = Math.cos(a0) * radius;
            double s0 = Math.sin(a0) * radius;
            double c1 = Math.cos(a1) * radius;
            double s1 = Math.sin(a1) * radius;
            double u0 = (double)i / SEGMENTS;
            double u1 = (double)(i + 1) / SEGMENTS;
            quad(pose, consumer, packedLight,
                    c0, 0.0D, s0, u0, 0.0D,
                    c1, 0.0D, s1, u1, 0.0D,
                    c1, height, s1, u1, 1.0D,
                    c0, height, s0, u0, 1.0D,
                    0.45F, 0.85F, 1.0F, alpha);
        }
    }

    private void quad(
            PoseStack.Pose pose,
            VertexConsumer consumer,
            int packedLight,
            double x1, double y1, double z1, double u1, double v1,
            double x2, double y2, double z2, double u2, double v2,
            double x3, double y3, double z3, double u3, double v3,
            double x4, double y4, double z4, double u4, double v4,
            float red, float green, float blue, float alpha) {
        vertex(pose, consumer, packedLight, x1, y1, z1, u1, v1, red, green, blue, alpha);
        vertex(pose, consumer, packedLight, x2, y2, z2, u2, v2, red, green, blue, alpha);
        vertex(pose, consumer, packedLight, x3, y3, z3, u3, v3, red, green, blue, alpha);
        vertex(pose, consumer, packedLight, x4, y4, z4, u4, v4, red, green, blue, alpha);
    }

    private void vertex(PoseStack.Pose pose, VertexConsumer consumer, int packedLight, double x, double y, double z, double u, double v, float red, float green, float blue, float alpha) {
        consumer.addVertex(pose, (float)x, (float)y, (float)z)
                .setColor(red, green, blue, alpha)
                .setUv((float)u, (float)v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(packedLight)
                .setNormal(0.0F, 1.0F, 0.0F);
    }

    private double ease(double progress) {
        double clamped = clamp(progress);
        return 0.5D - Math.cos(clamped * Math.PI) * 0.5D;
    }

    private double clamp(double value) {
        if (value < 0.0D) {
            return 0.0D;
        }

        return Math.min(value, 1.0D);
    }

    private record ObjMesh(List<MeshVertex[]> triangles) {
        private static ObjMesh load(ResourceLocation location) {
            try {
                List<double[]> positions = new ArrayList<>();
                List<double[]> uvs = new ArrayList<>();
                List<MeshVertex[]> triangles = new ArrayList<>();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(Minecraft.getInstance().getResourceManager().open(location), StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        line = line.trim();
                        if (line.startsWith("v ")) {
                            String[] parts = line.split("\\s+");
                            positions.add(new double[] { Double.parseDouble(parts[1]) - 0.5D, Double.parseDouble(parts[2]), Double.parseDouble(parts[3]) - 0.5D });
                        } else if (line.startsWith("vt ")) {
                            String[] parts = line.split("\\s+");
                            uvs.add(new double[] { Double.parseDouble(parts[1]), 1.0D - Double.parseDouble(parts[2]) });
                        } else if (line.startsWith("f ")) {
                            String[] parts = line.split("\\s+");
                            if (parts.length >= 4) {
                                MeshVertex first = vertex(parts[1], positions, uvs);
                                for (int i = 2; i < parts.length - 1; i++) {
                                    triangles.add(new MeshVertex[] { first, vertex(parts[i], positions, uvs), vertex(parts[i + 1], positions, uvs) });
                                }
                            }
                        }
                    }
                }

                return new ObjMesh(triangles);
            } catch (IOException | RuntimeException exception) {
                LanteaCraft.LOGGER.warn("Failed to load transport ring OBJ {}", location, exception);
                return null;
            }
        }

        private static MeshVertex vertex(String token, List<double[]> positions, List<double[]> uvs) {
            String[] indices = token.split("/");
            double[] position = positions.get(Integer.parseInt(indices[0]) - 1);
            double[] uv = indices.length > 1 && !indices[1].isBlank() ? uvs.get(Integer.parseInt(indices[1]) - 1) : new double[] { 0.0D, 0.0D };
            return new MeshVertex(position[0], position[1], position[2], uv[0], uv[1]);
        }

        private void render(PoseStack.Pose pose, VertexConsumer consumer, int packedLight, double x, double y, double z, float alpha) {
            for (MeshVertex[] triangle : triangles) {
                emitTriangle(pose, consumer, packedLight, x, y, z, alpha, triangle[0], triangle[1], triangle[2]);
                emitTriangle(pose, consumer, packedLight, x, y, z, alpha, triangle[2], triangle[1], triangle[0]);
            }
        }

        private void emitTriangle(PoseStack.Pose pose, VertexConsumer consumer, int packedLight, double x, double y, double z, float alpha, MeshVertex first, MeshVertex second, MeshVertex third) {
            emitVertex(pose, consumer, packedLight, x, y, z, alpha, first);
            emitVertex(pose, consumer, packedLight, x, y, z, alpha, second);
            emitVertex(pose, consumer, packedLight, x, y, z, alpha, third);
        }

        private void emitVertex(PoseStack.Pose pose, VertexConsumer consumer, int packedLight, double x, double y, double z, float alpha, MeshVertex vertex) {
            consumer.addVertex(pose, (float)(vertex.x + x), (float)(vertex.y + y), (float)(vertex.z + z))
                    .setColor(1.0F, 1.0F, 1.0F, alpha)
                    .setUv((float)vertex.u, (float)vertex.v)
                    .setOverlay(OverlayTexture.NO_OVERLAY)
                    .setLight(packedLight)
                    .setNormal(0.0F, 1.0F, 0.0F);
        }
    }

    private record MeshVertex(double x, double y, double z, double u, double v) {
    }
}
