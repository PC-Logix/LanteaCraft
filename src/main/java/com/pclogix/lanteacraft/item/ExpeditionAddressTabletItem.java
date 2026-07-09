package com.pclogix.lanteacraft.item;

import com.pclogix.lanteacraft.worldgen.ExpeditionInstance;
import com.pclogix.lanteacraft.worldgen.ExpeditionSavedData;
import com.pclogix.lanteacraft.worldgen.LanteaDimensions;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;

public class ExpeditionAddressTabletItem extends AddressTabletItem {
    public static final String TAG_EXPEDITION = "expeditionAddress";
    public static final String TAG_TIER = "expeditionTier";

    public ExpeditionAddressTabletItem(Properties properties) {
        super(properties);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (!level.isClientSide && entity instanceof Player player && level instanceof ServerLevel serverLevel && !hasAddress(stack)) {
            ExpeditionInstance expedition = ExpeditionSavedData.get(serverLevel).discover(serverLevel.getSeed(), player.blockPosition());
            forExpedition(stack, expedition);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        CompoundTag data = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        if (data.contains(TAG_TIER)) {
            tooltip.add(Component.translatable("item.lanteacraft.expedition_address_tablet.tier", data.getInt(TAG_TIER)).withStyle(ChatFormatting.DARK_AQUA));
        }
        tooltip.add(Component.translatable("item.lanteacraft.expedition_address_tablet.tooltip").withStyle(ChatFormatting.DARK_GRAY));
    }

    public static ItemStack forExpedition(ItemStack stack, ExpeditionInstance expedition) {
        CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> {
            tag.putString(TAG_ADDRESS, expedition.address());
            tag.putString(TAG_DIMENSION, LanteaDimensions.EXPEDITIONS.location().toString());
            tag.putInt(TAG_GATE_X, expedition.basePos().getX());
            tag.putInt(TAG_GATE_Y, expedition.basePos().getY());
            tag.putInt(TAG_GATE_Z, expedition.basePos().getZ());
            tag.putInt(TAG_VILLAGE_X, expedition.basePos().getX());
            tag.putInt(TAG_VILLAGE_Y, expedition.basePos().getY());
            tag.putInt(TAG_VILLAGE_Z, expedition.basePos().getZ());
            tag.putString(TAG_FACING, expedition.facing().getName());
            tag.putString(TAG_VARIANT, expedition.variant().name());
            tag.putString(TAG_EXPEDITION, expedition.address());
            tag.putInt(TAG_TIER, expedition.tier());
        });
        stack.set(DataComponents.CUSTOM_NAME, Component.translatable("item.lanteacraft.expedition_address_tablet.named", expedition.address()).withStyle(ChatFormatting.GOLD));
        return stack;
    }
}
