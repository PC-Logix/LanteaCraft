package com.pclogix.lanteacraft.power;

import com.pclogix.lanteacraft.Config;
import com.pclogix.lanteacraft.block.entity.StargateBaseBlockEntity;
import com.pclogix.lanteacraft.gate.StargateEntry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;

public final class StargatePower {
    private StargatePower() {
    }

    public static long calculateDialCost(StargateEntry origin, StargateEntry destination) {
        if (isCrossDimension(origin, destination)) {
            return multiply(Config.DIAL_COST_CROSS_DIMENSION.get(), Config.CROSS_DIMENSION_DIAL_MULTIPLIER.get());
        }

        long base = Config.DIAL_COST_SAME_DIMENSION.get();
        return safeAdd(base, sameDimensionDistanceCost(origin, destination, base));
    }

    public static long calculateSustainCostPerTick(StargateEntry origin, StargateEntry destination) {
        if (isCrossDimension(origin, destination)) {
            return multiply(Config.CROSS_DIMENSION_COST_PER_TICK.get(), Config.CROSS_DIMENSION_SUSTAIN_MULTIPLIER.get());
        }

        long base = Config.ACTIVE_COST_PER_TICK.get();
        return safeAdd(base, sameDimensionDistanceCost(origin, destination, base));
    }

    public static double calculateDistanceMultiplier(StargateEntry origin, StargateEntry destination) {
        if (!Config.ENABLE_DISTANCE_COST.getAsBoolean() || isCrossDimension(origin, destination)) {
            return 1.0D;
        }

        double distance = Math.sqrt(origin.basePos().distSqr(destination.basePos()));
        if (Config.USE_DISTANCE_TIERS.getAsBoolean()) {
            if (distance <= Config.NEAR_DISTANCE_BLOCKS.get()) {
                return Config.NEAR_DISTANCE_MULTIPLIER.get();
            }
            if (distance <= Config.MEDIUM_DISTANCE_BLOCKS.get()) {
                return Config.MEDIUM_DISTANCE_MULTIPLIER.get();
            }
            return Config.FAR_DISTANCE_MULTIPLIER.get();
        }

        return Math.max(1.0D, 1.0D + distance * Config.SAME_DIMENSION_DISTANCE_MULTIPLIER.get());
    }

    public static boolean consumeDialPower(StargateBaseBlockEntity origin, StargateBaseBlockEntity destination, long amount) {
        if (!requiresPowerToDial(origin)) {
            return true;
        }

        return origin != null && origin.consumeEnergy(amount, false);
    }

    public static boolean consumeSustainPower(StargateBaseBlockEntity origin, StargateBaseBlockEntity destination, long amount) {
        if (!Config.ENABLE_FE_POWER.getAsBoolean()) {
            return true;
        }

        return switch (Config.WORMHOLE_POWER_MODE.get()) {
            case ORIGIN_ONLY -> consumeOriginSustain(origin, amount);
            case DESTINATION_ONLY -> consumeDestinationSustain(destination, amount);
            case BOTH_SIDES -> consumeOriginSustain(origin, amount) && consumeDestinationSustain(destination, amount);
            case PREFER_ORIGIN -> consumeOriginSustain(origin, amount) || consumeDestinationSustain(destination, amount);
            case PREFER_DESTINATION -> consumeDestinationSustain(destination, amount) || consumeOriginSustain(origin, amount);
        };
    }

    public static boolean requiresPowerToDial(StargateBaseBlockEntity gate) {
        return Config.ENABLE_FE_POWER.getAsBoolean()
                && Config.PLAYER_BUILT_GATES_REQUIRE_POWER_TO_DIAL.getAsBoolean()
                && (gate == null || !gate.hasAncientPower());
    }

    public static boolean requiresSustainPower(StargateBaseBlockEntity origin, StargateBaseBlockEntity destination) {
        return Config.ENABLE_FE_POWER.getAsBoolean()
                && (requiresPowerToSustain(origin) || (canPayRemoteSustain(destination) && requiresPowerToSustain(destination)));
    }

    public static StargateBaseBlockEntity baseEntity(MinecraftServer server, StargateEntry entry) {
        if (server == null || entry == null) {
            return null;
        }

        ResourceKey<Level> dimension = ResourceKey.create(Registries.DIMENSION, entry.dimension());
        ServerLevel level = server.getLevel(dimension);
        return level != null && level.getBlockEntity(entry.basePos()) instanceof StargateBaseBlockEntity base ? base : null;
    }

    private static boolean consumeOne(StargateBaseBlockEntity gate, long amount, boolean simulate) {
        return gate != null && gate.consumeEnergy(amount, simulate);
    }

    private static boolean consumeOriginSustain(StargateBaseBlockEntity origin, long amount) {
        return !requiresPowerToSustain(origin) || consumeOne(origin, amount, false);
    }

    private static boolean consumeDestinationSustain(StargateBaseBlockEntity destination, long amount) {
        return !canPayRemoteSustain(destination)
                || !requiresPowerToSustain(destination)
                || consumeOne(destination, amount, false);
    }

    private static boolean requiresPowerToSustain(StargateBaseBlockEntity gate) {
        return Config.PLAYER_BUILT_GATES_REQUIRE_POWER_TO_SUSTAIN.getAsBoolean()
                && (gate == null || !gate.hasAncientPower());
    }

    private static boolean canPayRemoteSustain(StargateBaseBlockEntity gate) {
        return gate != null
                && (!gate.isAncientInstallation() || Config.GENERATED_GATES_CAN_BE_REMOTE_DRAINED.getAsBoolean());
    }

    private static long sameDimensionDistanceCost(StargateEntry origin, StargateEntry destination, long base) {
        if (!Config.ENABLE_DISTANCE_COST.getAsBoolean()) {
            return 0L;
        }

        double distance = Math.sqrt(origin.basePos().distSqr(destination.basePos()));
        long cost = Config.USE_DISTANCE_TIERS.getAsBoolean()
                ? Math.max(0L, multiply(base, calculateDistanceMultiplier(origin, destination)) - base)
                : (long)Math.ceil(distance * Config.SAME_DIMENSION_DISTANCE_MULTIPLIER.get());
        long cap = Config.SAME_DIMENSION_DISTANCE_COST_CAP.get();
        return cap <= 0L ? cost : Math.min(cost, cap);
    }

    private static boolean isCrossDimension(StargateEntry origin, StargateEntry destination) {
        return !origin.dimension().equals(destination.dimension());
    }

    private static long multiply(long value, double multiplier) {
        if (value <= 0L || multiplier <= 0.0D) {
            return 0L;
        }

        double result = value * multiplier;
        if (result >= Long.MAX_VALUE) {
            return Long.MAX_VALUE;
        }
        return Math.max(0L, (long)Math.ceil(result));
    }

    private static long safeAdd(long first, long second) {
        return Mth.clamp(first, 0L, Long.MAX_VALUE - Math.max(0L, second)) + Math.max(0L, second);
    }
}
