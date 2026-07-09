package com.pclogix.lanteacraft.item;

import com.pclogix.lanteacraft.Config;
import com.pclogix.lanteacraft.registry.ModDataComponents;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

public class ZpmItem extends Item {
    public static final int DEFAULT_CAPACITY = 1_000_000_000;
    public static final int DEFAULT_MAX_TRANSFER = 100_000_000;

    public ZpmItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public ItemStack getDefaultInstance() {
        ItemStack stack = super.getDefaultInstance();
        stack.set(ModDataComponents.ENERGY, capacity());
        return stack;
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return energyStored(stack) > 0;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return Math.round(13.0F * energyStored(stack) / capacity());
    }

    @Override
    public int getBarColor(ItemStack stack) {
        float filled = (float)energyStored(stack) / capacity();
        return Mth.hsvToRgb(0.54F, 0.95F, 0.45F + filled * 0.45F);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        int energy = energyStored(stack);
        int capacity = capacity();
        double percent = capacity > 0 ? 100.0D * energy / capacity : 0.0D;
        tooltip.add(Component.translatable("item.lanteacraft.zpm.energy", String.format("%.2f%%", percent)).withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("item.lanteacraft.zpm.fe", energy, capacity).withStyle(ChatFormatting.DARK_GRAY));
    }

    public static int capacity() {
        return Config.ZPM_CAPACITY == null ? DEFAULT_CAPACITY : Config.ZPM_CAPACITY.get();
    }

    public static int maxTransfer() {
        return Config.ZPM_MAX_TRANSFER == null ? DEFAULT_MAX_TRANSFER : Config.ZPM_MAX_TRANSFER.get();
    }

    private int energyStored(ItemStack stack) {
        return Mth.clamp(stack.getOrDefault(ModDataComponents.ENERGY, 0), 0, capacity());
    }
}
