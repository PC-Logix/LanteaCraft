package com.pclogix.lanteacraft.item;

import com.pclogix.lanteacraft.gate.IrisType;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

public class IrisUpgradeItem extends Item {
    private final IrisType irisType;

    public IrisUpgradeItem(IrisType irisType, Properties properties) {
        super(properties);
        this.irisType = irisType;
    }

    public IrisType irisType() {
        return irisType;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("item.lanteacraft.iris_upgrade.type",
                Component.translatable("item.lanteacraft.iris_upgrade.type." + irisType.serializedName()))
                .withStyle(ChatFormatting.GRAY));
    }
}
