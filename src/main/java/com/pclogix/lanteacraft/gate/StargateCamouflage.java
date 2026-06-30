package com.pclogix.lanteacraft.gate;

import com.pclogix.lanteacraft.block.StargateBaseBlock;
import com.pclogix.lanteacraft.block.StargateComponentBlock;
import com.pclogix.lanteacraft.block.entity.StargateBaseBlockEntity;
import com.pclogix.lanteacraft.item.DecoratorItem;
import com.pclogix.lanteacraft.registry.ModBlocks;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public final class StargateCamouflage {
    private StargateCamouflage() {
    }

    public static ItemInteractionResult tryApplyBottomCamouflage(ItemStack stack, BlockState clickedState, Level level, BlockPos clickedPos, Player player) {
        if (stack.isEmpty() || !clickedState.hasProperty(StargateBaseBlock.ASSEMBLED) && !clickedState.hasProperty(StargateComponentBlock.ASSEMBLED)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        if (clickedState.hasProperty(StargateBaseBlock.ASSEMBLED) && !clickedState.getValue(StargateBaseBlock.ASSEMBLED)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        if (clickedState.hasProperty(StargateComponentBlock.ASSEMBLED) && !clickedState.getValue(StargateComponentBlock.ASSEMBLED)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        if (!(stack.getItem() instanceof DecoratorItem)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        Optional<BlockState> camouflage = DecoratorItem.camouflage(stack);
        if (camouflage.isEmpty()) {
            if (!level.isClientSide) {
                player.displayClientMessage(Component.translatable("message.lanteacraft.decorator_empty"), true);
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }

        Optional<BlockPos> basePos = StargateMultiblock.findBasePosFrom(level, clickedPos);
        if (basePos.isEmpty() || !isBottomRowClick(level, basePos.get(), clickedPos)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        if (!level.isClientSide && level.getBlockEntity(basePos.get()) instanceof StargateBaseBlockEntity base) {
            base.setBottomCamouflage(camouflage.get());
            player.displayClientMessage(Component.translatable("message.lanteacraft.stargate_disguised", camouflage.get().getBlock().getName()), true);
        }

        return ItemInteractionResult.sidedSuccess(level.isClientSide);
    }

    public static boolean isValidCamouflage(BlockState state) {
        return !state.isAir()
                && !(state.getBlock() instanceof StargateBaseBlock)
                && !(state.getBlock() instanceof StargateComponentBlock)
                && !state.is(ModBlocks.DHD.get())
                && !state.is(ModBlocks.NOX_DHD.get())
                && !state.is(ModBlocks.WRAITH_DHD.get())
                && !state.is(ModBlocks.PEGASUS_DHD.get())
                && state.canOcclude();
    }

    private static boolean isBottomRowClick(Level level, BlockPos basePos, BlockPos clickedPos) {
        BlockState baseState = level.getBlockState(basePos);
        if (!(baseState.getBlock() instanceof StargateBaseBlock) || !baseState.getValue(StargateBaseBlock.ASSEMBLED)) {
            return false;
        }

        Direction right = baseState.getValue(StargateBaseBlock.FACING).getClockWise();
        for (int x = -3; x <= 3; x++) {
            if (basePos.relative(right, x).equals(clickedPos)) {
                return true;
            }
        }

        return false;
    }
}
