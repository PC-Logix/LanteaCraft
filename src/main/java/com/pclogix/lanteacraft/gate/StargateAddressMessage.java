package com.pclogix.lanteacraft.gate;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

public final class StargateAddressMessage {
    private StargateAddressMessage() {
    }

    public static void send(Player player, StargateEntry entry) {
        Component address = Component.literal(entry.address())
                .withStyle(style -> style
                        .withColor(ChatFormatting.AQUA)
                        .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, entry.address())));

        player.displayClientMessage(
                Component.literal("Stargate address: ")
                        .withStyle(ChatFormatting.GRAY)
                        .append(address)
                        .append(Component.literal(" (click to copy)").withStyle(ChatFormatting.DARK_GRAY)),
                false);
    }
}
