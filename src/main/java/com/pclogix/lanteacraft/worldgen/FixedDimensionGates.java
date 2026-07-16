package com.pclogix.lanteacraft.worldgen;

import com.pclogix.lanteacraft.gate.StargateVariant;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;

public final class FixedDimensionGates {
    // Series glyphs 27-7-15-32-12-30 map to 1-G-O-6-L-4 in the legacy alphabet.
    public static final String ABYDOS_ADDRESS = "1GO6L4X";
    private static final String LEGACY_ABYDOS_ADDRESS = "ABYDOSX";
    public static final String ATLANTIS_ADDRESS = "ATLANTIS";

    private static final PlannedStargate ABYDOS = new PlannedStargate(
            ABYDOS_ADDRESS,
            LanteaDimensions.ABYDOS.location(),
            new BlockPos(0, 72, 0),
            new BlockPos(0, 72, 0),
            Direction.SOUTH,
            StargateVariant.MILKY_WAY);

    private static final PlannedStargate ATLANTIS = new PlannedStargate(
            ATLANTIS_ADDRESS,
            LanteaDimensions.ATLANTIS.location(),
            new BlockPos(0, 42, 0),
            new BlockPos(0, 42, 0),
            Direction.SOUTH,
            StargateVariant.PEGASUS);

    private FixedDimensionGates() {
    }

    public static void rememberAll(ServerLevel level) {
        PlannedStargateSavedData data = PlannedStargateSavedData.get(level);
        data.remember(ABYDOS);
        data.remember(ATLANTIS);
    }

    public static Optional<PlannedStargate> byAddress(String address) {
        String normalized = address == null ? "" : address.trim().toUpperCase();
        if (ABYDOS.address().equals(normalized) || LEGACY_ABYDOS_ADDRESS.equals(normalized)) {
            return Optional.of(ABYDOS);
        }
        if (ATLANTIS.address().equals(normalized)) {
            return Optional.of(ATLANTIS);
        }
        return Optional.empty();
    }

    public static Optional<PlannedStargate> forDimension(ResourceLocation dimension) {
        if (ABYDOS.dimension().equals(dimension)) {
            return Optional.of(ABYDOS);
        }
        if (ATLANTIS.dimension().equals(dimension)) {
            return Optional.of(ATLANTIS);
        }
        return Optional.empty();
    }

    public static boolean isFixedDimension(ResourceLocation dimension) {
        return forDimension(dimension).isPresent();
    }

    public static boolean isFixedPlan(PlannedStargate plan) {
        return forDimension(plan.dimension())
                .map(fixed -> fixed.address().equals(plan.address()))
                .orElse(false);
    }
}
