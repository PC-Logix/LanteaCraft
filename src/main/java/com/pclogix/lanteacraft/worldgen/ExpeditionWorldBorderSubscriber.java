package com.pclogix.lanteacraft.worldgen;

import com.pclogix.lanteacraft.LanteaCraft;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.border.WorldBorder;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.LevelEvent;

@EventBusSubscriber(modid = LanteaCraft.MODID, bus = EventBusSubscriber.Bus.GAME)
public final class ExpeditionWorldBorderSubscriber {
    private ExpeditionWorldBorderSubscriber() {
    }

    @SubscribeEvent
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
