package com.pclogix.lanteacraft.client;

import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public final class AddressTabletClientActions {
    private AddressTabletClientActions() {
    }

    public static void copyAddress(Optional<String> address) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null) {
            return;
        }

        if (address.isEmpty()) {
            minecraft.player.displayClientMessage(Component.translatable("message.lanteacraft.address_tablet_no_address").withStyle(ChatFormatting.RED), false);
            return;
        }

        minecraft.keyboardHandler.setClipboard(address.get());
        minecraft.player.displayClientMessage(Component.translatable("message.lanteacraft.address_tablet_copied", address.get()).withStyle(ChatFormatting.GREEN), false);
    }
}
