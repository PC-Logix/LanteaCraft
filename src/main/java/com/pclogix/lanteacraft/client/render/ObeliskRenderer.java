package com.pclogix.lanteacraft.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.pclogix.lanteacraft.block.entity.ObeliskBlockEntity;
import com.pclogix.lanteacraft.registry.ModItems;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class ObeliskRenderer implements BlockEntityRenderer<ObeliskBlockEntity> {
    private static final float MODEL_SCALE = 0.35F;
    // World-space nudge for tuning the OBJ over the anchor block: +X east, +Z south.
    private static final double MODEL_OFFSET_X = 0.125D;
    private static final double MODEL_OFFSET_Y = 0.0D;
    private static final double MODEL_OFFSET_Z = 0.125D;
    private final ItemRenderer itemRenderer;

    public ObeliskRenderer(BlockEntityRendererProvider.Context context) {
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(
            ObeliskBlockEntity blockEntity,
            float partialTick,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight,
            int packedOverlay
    ) {
        poseStack.pushPose();
        poseStack.translate(0.5D + MODEL_OFFSET_X, MODEL_OFFSET_Y, 0.5D + MODEL_OFFSET_Z);
        poseStack.scale(MODEL_SCALE, MODEL_SCALE, MODEL_SCALE);
        itemRenderer.renderStatic(
                new ItemStack(ModItems.OBELISK.get()),
                ItemDisplayContext.NONE,
                packedLight,
                OverlayTexture.NO_OVERLAY,
                poseStack,
                bufferSource,
                blockEntity.getLevel(),
                0
        );
        poseStack.popPose();
    }

    @Override
    public boolean shouldRenderOffScreen(ObeliskBlockEntity blockEntity) {
        return true;
    }

    @Override
    public int getViewDistance() {
        return 128;
    }

    @Override
    public boolean shouldRender(ObeliskBlockEntity blockEntity, Vec3 cameraPos) {
        return getRenderBoundingBox(blockEntity).distanceToSqr(cameraPos) < getViewDistance() * getViewDistance();
    }

    @Override
    public AABB getRenderBoundingBox(ObeliskBlockEntity blockEntity) {
        BlockPos pos = blockEntity.getBlockPos();
        return new AABB(pos).inflate(1.0D, 0.5D, 1.0D).expandTowards(0.0D, 4.0D, 0.0D);
    }
}
