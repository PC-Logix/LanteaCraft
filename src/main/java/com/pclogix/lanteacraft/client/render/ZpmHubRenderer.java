package com.pclogix.lanteacraft.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.pclogix.lanteacraft.block.ZpmHubBlock;
import com.pclogix.lanteacraft.block.entity.ZpmHubBlockEntity;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class ZpmHubRenderer implements BlockEntityRenderer<ZpmHubBlockEntity> {
    private final ItemRenderer itemRenderer;

    public ZpmHubRenderer(BlockEntityRendererProvider.Context context) {
        this.itemRenderer = context.getItemRenderer();
    }

    /*
     * Coordinate notes:
     *
     * Valid hub slots:
     * - slot 0 = top left
     * - slot 1 = top right
     * - slot 2 = middle bottom
     *
     * The hub block model renders normally.
     * This renderer only renders installed ZPM ItemStacks.
     *
     * The ZPM item model should use the NeoForge OBJ loader in assets/lanteacraft/models/item/zpm.json.
     * This renderer should NOT manually parse or render OBJ geometry.
     */

    private static double zpmBaseY() {
        return 0.68D;
    }

    private static float zpmScaleXZ() {
        return 0.42F;
    }

    private static float zpmScaleY() {
        return 0.42F;
    }

    /*
     * Model-space correction for the ZPM OBJ/item origin.
     * These happen after scaling, so the values are in item/model space.
     *
     * Tune these before touching slotX/slotZ if all ZPMs appear offset the same way.
     */
    private static double zpmModelOffsetX() {
        return 0.50D;
    }

    private static double zpmModelOffsetY() {
        return -0.3D;
    }

    private static double zpmModelOffsetZ() {
        return 0.5D;
    }

    private static double slotX(int slot) {
        return switch (slot) {
            case 0 -> -0.15D; // slot 1 / top left
            case 1 ->  0.15D; // slot 2 / top right
            case 2 ->  0.00D; // slot 3 / bottom middle
            default -> 0.0D;
        };
    }

    private static double slotZ(int slot) {
        return switch (slot) {
            case 0 -> -0.10D; // slot 1 / top left
            case 1 -> -0.10D; // slot 2 / top right
            case 2 ->  0.16D; // slot 3 / bottom middle
            default -> 0.0D;
        };
    }

    /*
     * Local in-place ZPM rotation per slot.
     *
     * IMPORTANT:
     * Do not add block facing here. The whole cluster is already rotated by renderYaw().
     */
    private static float slotYaw(int slot) {
        return switch (slot) {
            case 0 -> 180.0F;
            case 1 ->  60.0F;
            case 2 -> -60.0F;
            default -> 0.0F;
        };
    }

    /*
     * Rotates the whole installed-ZPM cluster to match the hub block facing.
     *
     * This is the previous mapping rotated 180 degrees because the item-rendered
     * ZPM cluster was backwards relative to the hub sockets.
     */
    private static float renderYaw(Direction facing) {
        return switch (facing) {
            case NORTH -> 180.0F;
            case SOUTH -> 0.0F;
            case EAST  -> 90.0F;
            case WEST  -> 270.0F;
            default -> 180.0F;
        };
    }

    private static Direction hubFacing(ZpmHubBlockEntity blockEntity) {
        if (blockEntity.getBlockState().hasProperty(ZpmHubBlock.FACING)) {
            return blockEntity.getBlockState().getValue(ZpmHubBlock.FACING);
        }

        return Direction.NORTH;
    }

    @Override
    public void render(
            ZpmHubBlockEntity blockEntity,
            float partialTick,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight,
            int packedOverlay
    ) {
        Direction facing = hubFacing(blockEntity);

        poseStack.pushPose();

        // Move to the center of the hub block, then orient the whole local slot layout.
        poseStack.translate(0.5D, zpmBaseY(), 0.5D);
        poseStack.mulPose(Axis.YP.rotationDegrees(renderYaw(facing)));

        for (int slot = 0; slot < ZpmHubBlockEntity.ZPM_SLOTS; slot++) {
            ItemStack stack = blockEntity.zpmStack(slot);
            if (!stack.isEmpty()) {
                renderZpm(stack, slot, poseStack, bufferSource, blockEntity);
            }
        }

        poseStack.popPose();
    }

    private void renderZpm(
            ItemStack stack,
            int slot,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            ZpmHubBlockEntity blockEntity
    ) {
        poseStack.pushPose();

        // 1. Move to this slot's socket position in local hub space.
        poseStack.translate(slotX(slot), 0.0D, slotZ(slot));

        /*
         * 2. Optional per-slot in-place rotation.
         *
         * Leave this OFF while tuning slot placement/model origin.
         * Turn it back on only after the ZPMs are centered in the sockets.
         */
        // poseStack.mulPose(Axis.YP.rotationDegrees(slotYaw(slot)));

        // 3. Scale the item model to hub size.
        poseStack.scale(zpmScaleXZ(), zpmScaleY(), zpmScaleXZ());

        // 4. Correct the OBJ/item model origin.
        poseStack.translate(zpmModelOffsetX(), zpmModelOffsetY(), zpmModelOffsetZ());

        /*
         * Use NONE so the hub renderer owns placement.
         * GUI/fixed/ground/etc. JSON transforms won't fight this renderer.
         */
        itemRenderer.renderStatic(
                stack,
                ItemDisplayContext.NONE,
                LightTexture.FULL_BRIGHT,
                OverlayTexture.NO_OVERLAY,
                poseStack,
                bufferSource,
                blockEntity.getLevel(),
                slot
        );

        poseStack.popPose();
    }
}