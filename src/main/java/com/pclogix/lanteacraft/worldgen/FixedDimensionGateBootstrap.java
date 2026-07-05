package com.pclogix.lanteacraft.worldgen;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

public final class FixedDimensionGateBootstrap {
    private static boolean remembered;

    private FixedDimensionGateBootstrap() {
    }

    public static void onLevelTick(LevelTickEvent.Post event) {
        Level tickLevel = event.getLevel();
        if (remembered || !(tickLevel instanceof ServerLevel level) || !level.dimension().equals(Level.OVERWORLD)) {
            return;
        }

        FixedDimensionGates.rememberAll(level);
        remembered = true;
    }
}
