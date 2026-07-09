package com.pclogix.lanteacraft.worldgen;

import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.event.entity.living.MobSpawnEvent;

public final class AtlantisSpawnRules {
    private AtlantisSpawnRules() {
    }

    public static void onSpawnPlacementCheck(MobSpawnEvent.SpawnPlacementCheck event) {
        if (event.getLevel().getLevel().dimension().equals(LanteaDimensions.ATLANTIS)
                && event.getEntityType().getCategory() == MobCategory.MONSTER) {
            event.setResult(MobSpawnEvent.SpawnPlacementCheck.Result.FAIL);
        }
    }

    public static void onPositionCheck(MobSpawnEvent.PositionCheck event) {
        if (event.getLevel().getLevel().dimension().equals(LanteaDimensions.ATLANTIS)
                && event.getEntity().getType().getCategory() == MobCategory.MONSTER) {
            event.setResult(MobSpawnEvent.PositionCheck.Result.FAIL);
        }
    }
}
