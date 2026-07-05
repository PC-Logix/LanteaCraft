package com.pclogix.lanteacraft.item;

import com.pclogix.lanteacraft.registry.ModDataComponents;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

public class EnergyCrystalItem extends Item {
    public static final int CAPACITY = 2_000_000;
    public static final int MAX_TRANSFER = 20_000;

    public EnergyCrystalItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return energyStored(stack) > 0;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return Math.round(13.0F * energyStored(stack) / CAPACITY);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        float filled = (float)energyStored(stack) / CAPACITY;
        return Mth.hsvToRgb(0.36F, 1.0F, 0.45F + filled * 0.35F);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        int energy = energyStored(stack);
        double percent = 100.0D * energy / CAPACITY;
        tooltip.add(Component.translatable("item.lanteacraft.energy_crystal.energy", String.format("%.2f%%", percent)).withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("item.lanteacraft.energy_crystal.fe", energy, CAPACITY).withStyle(ChatFormatting.DARK_GRAY));
    }

    private int energyStored(ItemStack stack) {
        return Mth.clamp(stack.getOrDefault(ModDataComponents.ENERGY, 0), 0, CAPACITY);
    }
}
