package com.pclogix.lanteacraft.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.pclogix.lanteacraft.block.entity.OfferingAltarBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class OfferingAltarRenderer implements BlockEntityRenderer<OfferingAltarBlockEntity> {
    private final ItemRenderer itemRenderer;

    public OfferingAltarRenderer(BlockEntityRendererProvider.Context context) {
        itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(OfferingAltarBlockEntity altar, float partialTick, PoseStack poseStack, MultiBufferSource buffers, int packedLight, int packedOverlay) {
        ItemStack stack = altar.displayedItem();
        if (stack.isEmpty()) {
            return;
        }
        poseStack.pushPose();
        poseStack.translate(0.5D, 1.08D, 0.5D);
        float cameraYaw = Minecraft.getInstance().gameRenderer.getMainCamera().getYRot();
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - cameraYaw));
        poseStack.scale(0.5F, 0.5F, 0.5F);
        itemRenderer.renderStatic(stack, ItemDisplayContext.FIXED, packedLight, OverlayTexture.NO_OVERLAY,
                poseStack, buffers, altar.getLevel(), 0);
        poseStack.popPose();
    }
}
