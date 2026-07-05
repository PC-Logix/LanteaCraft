package com.pclogix.lanteacraft.item;

import com.pclogix.lanteacraft.worldgen.FixedDimensionGates;
import com.pclogix.lanteacraft.worldgen.PlannedStargate;
import java.util.List;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public class FixedAddressTabletItem extends AddressTabletItem {
    private final String address;
    private final String descriptionKey;

    public FixedAddressTabletItem(String address, String descriptionKey, Properties properties) {
        super(properties);
        this.address = address;
        this.descriptionKey = descriptionKey;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        fixedPlan().ifPresent(plan -> forPlan(stack, plan));
        super.inventoryTick(stack, level, entity, slotId, isSelected);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        if (!hasAddress(stack)) {
            fixedPlan().ifPresent(plan -> forPlan(stack, plan));
        }
        super.appendHoverText(stack, context, tooltip, flag);
        tooltip.add(Component.translatable(descriptionKey).withStyle(ChatFormatting.DARK_AQUA));
    }

    private Optional<PlannedStargate> fixedPlan() {
        return FixedDimensionGates.byAddress(address);
    }
}
