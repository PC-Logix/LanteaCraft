package com.pclogix.lanteacraft.worldgen;

import com.pclogix.lanteacraft.entity.GoauldSoldierEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

public final class ExpeditionTrialTracker {
    private static final int CHECK_INTERVAL_TICKS = 20;
    private static final double COMBAT_ROOM_RADIUS = 8.0D;

    private ExpeditionTrialTracker() {
    }

    public static void onLevelTick(LevelTickEvent.Post event) {
        if (!(event.getLevel() instanceof ServerLevel level)
                || !level.dimension().equals(LanteaDimensions.EXPEDITIONS)
                || level.getGameTime() % CHECK_INTERVAL_TICKS != 0L) {
            return;
        }

        ExpeditionSavedData data = ExpeditionSavedData.get(level);
        for (ExpeditionInstance expedition : data.expeditions()) {
            if (!expedition.generated() || expedition.rewardUnlocked() || expedition.combatRoomCenters().isEmpty()) {
                continue;
            }

            if (combatRoomsCleared(level, expedition)) {
                ExpeditionGenerator.unlockRewardDoor(level, expedition);
                data.markRewardUnlocked(expedition.address());
            }
        }
    }

    private static boolean combatRoomsCleared(ServerLevel level, ExpeditionInstance expedition) {
        for (BlockPos center : expedition.combatRoomCenters()) {
            AABB roomBounds = new AABB(center).inflate(COMBAT_ROOM_RADIUS, 5.0D, COMBAT_ROOM_RADIUS);
            if (!level.getEntitiesOfClass(GoauldSoldierEntity.class, roomBounds, soldier -> !soldier.isRemoved()).isEmpty()) {
                return false;
            }
        }
        return true;
    }
}
