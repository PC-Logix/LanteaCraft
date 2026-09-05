package com.pclogix.lanteacraft.worldgen;

import net.minecraft.network.protocol.game.ClientboundInitializeBorderPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.border.WorldBorder;
import net.neoforged.neoforge.event.level.LevelEvent;

public final class ExpeditionWorldBorder {
    private ExpeditionWorldBorder() {
    }

    public static void onLevelLoad(LevelEvent.Load event) {
        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }

        ensureDisabled(level);
    }

    public static boolean ensureDisabled(ServerLevel level) {
        if (!level.dimension().equals(LanteaDimensions.EXPEDITIONS)) {
            return false;
        }

        WorldBorder border = level.getWorldBorder();
        boolean changed = false;
        if (Double.compare(border.getCenterX(), 0.0D) != 0
                || Double.compare(border.getCenterZ(), 0.0D) != 0) {
            border.setCenter(0.0D, 0.0D);
            changed = true;
        }
        if (Double.compare(border.getSize(), WorldBorder.MAX_SIZE) != 0
                || border.getLerpRemainingTime() > 0L) {
            border.setSize(WorldBorder.MAX_SIZE);
            changed = true;
        }
        return changed;
    }

    public static void syncToClient(ServerPlayer player) {
        ServerLevel level = player.serverLevel();
        if (!level.dimension().equals(LanteaDimensions.EXPEDITIONS)) {
            return;
        }

        ensureDisabled(level);
        player.connection.send(new ClientboundInitializeBorderPacket(level.getWorldBorder()));
    }
}
