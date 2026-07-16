package com.pclogix.lanteacraft.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.pclogix.lanteacraft.entity.P90BulletEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

public class P90BulletRenderer extends EntityRenderer<P90BulletEntity> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("lanteacraft", "textures/models/solid_white.png");
    private static final RenderType RENDER_TYPE = RenderType.entityTranslucent(TEXTURE);
    private static final float FADE_START_DISTANCE = 18.0F;
    private static final float FADE_END_DISTANCE = 34.0F;

    public P90BulletRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected int getBlockLightLevel(P90BulletEntity entity, BlockPos pos) {
        return 15;
    }

    @Override
    public void render(P90BulletEntity entity, float yaw, float partialTick, PoseStack poseStack, MultiBufferSource buffers, int packedLight) {
        double distance = Math.sqrt(entity.distanceToSqr(entityRenderDispatcher.camera.getPosition()));
        if (distance >= FADE_END_DISTANCE) {
            return;
        }
        float fade = distance <= FADE_START_DISTANCE
                ? 1.0F
                : 1.0F - (float)(distance - FADE_START_DISTANCE) / (FADE_END_DISTANCE - FADE_START_DISTANCE);
        int alpha = Math.max(0, Math.min(210, Math.round(210.0F * fade)));

        poseStack.pushPose();
        poseStack.scale(0.18F, 0.045F, 0.045F);
        poseStack.mulPose(entityRenderDispatcher.cameraOrientation());
        PoseStack.Pose pose = poseStack.last();
        VertexConsumer consumer = buffers.getBuffer(RENDER_TYPE);
        vertex(consumer, pose, packedLight, alpha, 0.0F, 0.0F, 0.0F, 1.0F);
        vertex(consumer, pose, packedLight, alpha, 1.0F, 0.0F, 1.0F, 1.0F);
        vertex(consumer, pose, packedLight, alpha, 1.0F, 1.0F, 1.0F, 0.0F);
        vertex(consumer, pose, packedLight, alpha, 0.0F, 1.0F, 0.0F, 0.0F);
        poseStack.popPose();
        super.render(entity, yaw, partialTick, poseStack, buffers, packedLight);
    }

    private static void vertex(VertexConsumer consumer, PoseStack.Pose pose, int light, int alpha, float x, float y, float u, float v) {
        consumer.addVertex(pose, x - 0.5F, y - 0.5F, 0.0F)
                .setColor(255, 220, 120, alpha).setUv(u, v).setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(light).setNormal(pose, 0.0F, 1.0F, 0.0F);
    }

    @Override
    public ResourceLocation getTextureLocation(P90BulletEntity entity) {
        return TEXTURE;
    }
}
