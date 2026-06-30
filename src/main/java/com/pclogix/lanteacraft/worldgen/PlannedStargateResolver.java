package com.pclogix.lanteacraft.worldgen;

import com.pclogix.lanteacraft.gate.StargateEntry;
import com.pclogix.lanteacraft.gate.StargateNetworkSavedData;
import com.pclogix.lanteacraft.gate.StargateRecord;
import com.pclogix.lanteacraft.gate.StargateStatus;
import com.pclogix.lanteacraft.item.AddressTabletItem;
import java.util.Optional;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

public final class PlannedStargateResolver {
    private PlannedStargateResolver() {
    }

    public static Optional<StargateEntry> resolve(ServerLevel dialingLevel, String address) {
        StargateNetworkSavedData network = StargateNetworkSavedData.get(dialingLevel);
        Optional<StargateRecord> liveGate = network.findByAddress(address);
        if (liveGate.isPresent() && liveGate.get().status() == StargateStatus.ACTIVE) {
            return liveGate.get().activeEntry();
        }
        if (liveGate.isPresent() && liveGate.get().status() == StargateStatus.DORMANT) {
            return Optional.empty();
        }

        PlannedStargateSavedData plannedData = PlannedStargateSavedData.get(dialingLevel);
        Optional<PlannedStargate> plannedGate = plannedData.findByAddress(address)
                .or(() -> AddressTabletItem.findTabletPlan(dialingLevel.getServer(), address).map(plannedData::remember));
        if (plannedGate.isEmpty()) {
            return Optional.empty();
        }

        PlannedStargate plan = plannedGate.get();
        network.reserveGate(plan.address(), plan.dimension(), plan.basePos(), plan.facing(), plan.variant(), "planned");
        ServerLevel targetLevel = dialingLevel.getServer().getLevel(ResourceKey.create(Registries.DIMENSION, plan.dimension()));
        if (targetLevel == null) {
            return Optional.empty();
        }

        loadPlacementArea(targetLevel, new ChunkPos(plan.basePos()));
        StargateVillageGenerator.placeIfNeeded(targetLevel, plan);
        return StargateNetworkSavedData.get(targetLevel).findByAddress(address).flatMap(StargateRecord::activeEntry);
    }

    private static void loadPlacementArea(ServerLevel level, ChunkPos center) {
        for (int chunkX = center.x - 1; chunkX <= center.x + 1; chunkX++) {
            for (int chunkZ = center.z - 1; chunkZ <= center.z + 1; chunkZ++) {
                level.getChunk(chunkX, chunkZ);
            }
        }
    }
}
