package com.pclogix.lanteacraft.worldgen;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.border.WorldBorder;
import net.neoforged.neoforge.event.level.LevelEvent;

public final class ExpeditionWorldBorder {
    private ExpeditionWorldBorder() {
    }

    public static void onLevelLoad(LevelEvent.Load event) {
        if (!(event.getLevel() instanceof ServerLevel level)
                || !level.dimension().equals(LanteaDimensions.EXPEDITIONS)) {
            return;
        }

        WorldBorder border = level.getWorldBorder();
        border.setCenter(0.0D, 0.0D);
        border.setSize(WorldBorder.MAX_SIZE);
    }
}
