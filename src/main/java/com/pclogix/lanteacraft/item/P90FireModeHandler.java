package com.pclogix.lanteacraft.item;

import com.pclogix.lanteacraft.registry.ModDataComponents;
import com.pclogix.lanteacraft.registry.ModItems;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.entity.living.LivingSwapItemsEvent;

public final class P90FireModeHandler {
    private P90FireModeHandler() {
    }

    public static void register(IEventBus eventBus) {
        eventBus.addListener(P90FireModeHandler::onSwapHands);
    }

    private static void onSwapHands(LivingSwapItemsEvent.Hands event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        ItemStack weapon = player.getMainHandItem();
        if (!weapon.is(ModItems.P90.get())) {
            return;
        }

        event.setCanceled(true);
        if (!player.level().isClientSide) {
            boolean automatic = !P90Item.isAutomatic(weapon);
            weapon.set(ModDataComponents.P90_AUTOMATIC, automatic);
            player.displayClientMessage(Component.translatable(automatic
                    ? "message.lanteacraft.p90.mode.auto"
                    : "message.lanteacraft.p90.mode.single"), true);
            player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.LEVER_CLICK, SoundSource.PLAYERS, 0.45F, automatic ? 1.15F : 0.9F);
        }
    }
}
