package com.pclogix.lanteacraft.item;

import com.pclogix.lanteacraft.gate.StargateCamouflage;
import java.util.List;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class DecoratorItem extends Item {
    private static final String TAG_CAMOUFLAGE_BLOCK = "camouflageBlock";

    public DecoratorItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        Player player = context.getPlayer();
        ItemStack stack = context.getItemInHand();
        if (player == null || !player.isShiftKeyDown()) {
            return InteractionResult.PASS;
        }

        BlockState camouflage = level.getBlockState(context.getClickedPos());
        if (!StargateCamouflage.isValidCamouflage(camouflage)) {
            if (!level.isClientSide) {
                player.displayClientMessage(Component.translatable("message.lanteacraft.decorator_invalid").withStyle(ChatFormatting.RED), true);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        if (!level.isClientSide) {
            setCamouflage(stack, camouflage);
            player.displayClientMessage(Component.translatable("message.lanteacraft.decorator_configured", camouflage.getBlock().getName()).withStyle(ChatFormatting.GREEN), true);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        Optional<BlockState> camouflage = camouflage(stack);
        if (camouflage.isPresent()) {
            tooltip.add(Component.translatable("item.lanteacraft.decorator.block", camouflage.get().getBlock().getName()).withStyle(ChatFormatting.GRAY));
        } else {
            tooltip.add(Component.translatable("item.lanteacraft.decorator.empty").withStyle(ChatFormatting.GRAY));
        }
        tooltip.add(Component.translatable("item.lanteacraft.decorator.usage").withStyle(ChatFormatting.DARK_GRAY));
    }

    public static Optional<BlockState> camouflage(ItemStack stack) {
        CompoundTag data = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        if (!data.contains(TAG_CAMOUFLAGE_BLOCK)) {
            return Optional.empty();
        }

        Block block = BuiltInRegistries.BLOCK.get(ResourceLocation.parse(data.getString(TAG_CAMOUFLAGE_BLOCK)));
        if (block == Blocks.AIR) {
            return Optional.empty();
        }

        BlockState state = block.defaultBlockState();
        return StargateCamouflage.isValidCamouflage(state) ? Optional.of(state) : Optional.empty();
    }

    private static void setCamouflage(ItemStack stack, BlockState state) {
        CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> tag.putString(TAG_CAMOUFLAGE_BLOCK, BuiltInRegistries.BLOCK.getKey(state.getBlock()).toString()));
    }
}
