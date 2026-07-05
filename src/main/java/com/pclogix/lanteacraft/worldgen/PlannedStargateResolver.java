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
import net.minecraft.world.level.Level;

public final class PlannedStargateResolver {
    private PlannedStargateResolver() {
    }

    public static Optional<StargateEntry> resolve(ServerLevel dialingLevel, String address) {
        StargateNetworkSavedData network = StargateNetworkSavedData.get(dialingLevel);
        Optional<PlannedStargate> fixedGate = FixedDimensionGates.byAddress(address);
        if (fixedGate.isPresent()) {
            PlannedStargate plan = fixedGate.get();
            PlannedStargateSavedData.get(dialingLevel).remember(plan);
            network.reserveGate(plan.address(), plan.dimension(), plan.basePos(), plan.facing(), plan.variant(), "fixed_dimension");
            ServerLevel targetLevel = dialingLevel.getServer().getLevel(ResourceKey.create(Registries.DIMENSION, plan.dimension()));
            if (targetLevel == null) {
                return Optional.empty();
            }

            DimensionGateGenerator.placeIfNeeded(targetLevel, plan);
            return StargateNetworkSavedData.get(targetLevel).findByAddress(plan.address()).flatMap(StargateRecord::activeEntry);
        }

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

        if (FixedDimensionGates.isFixedPlan(plan)) {
            DimensionGateGenerator.placeIfNeeded(targetLevel, plan);
        } else {
            StargateVillageGenerator.placeAtPlannedBaseIfNeeded(targetLevel, plan);
        }
        return StargateNetworkSavedData.get(targetLevel).findByAddress(address).flatMap(StargateRecord::activeEntry);
    }
}
