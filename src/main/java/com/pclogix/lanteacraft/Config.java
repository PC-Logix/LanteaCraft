package com.pclogix.lanteacraft;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue DEBUG_LOGGING = BUILDER
            .comment("Enables extra startup logging for LanteaCraft systems.")
            .define("debugLogging", false);

    public static final ModConfigSpec.BooleanValue ALLOW_INCOMING_WORMHOLE_TRAVEL = BUILDER
            .comment("Allows entities to travel back through the incoming side of a Stargate wormhole.")
            .define("allowIncomingWormholeTravel", false);

    public static final ModConfigSpec.BooleanValue KAWOOSH_DEATH = BUILDER
            .comment("Makes the opening kawoosh kill entities caught in front of an active Stargate.")
            .define("kawooshDeath", true);

    public static final ModConfigSpec.BooleanValue AUTO_ASSEMBLE_STARGATES = BUILDER
            .comment("Automatically assembles Stargate multiblocks when a complete frame is placed.")
            .define("autoAssembleStargates", true);

    public static final ModConfigSpec.IntValue GATE_TIMEOUT_SECONDS = BUILDER
            .comment("How long an open Stargate wormhole remains active, in seconds. Set to 0 to disable automatic timeout.")
            .defineInRange("gateTimeoutSeconds", 60, 0, 86400);

    public static final ModConfigSpec.IntValue DHD_SEARCH_RADIUS = BUILDER
            .comment("Maximum block radius a DHD searches for a nearby assembled Stargate.")
            .defineInRange("dhdSearchRadius", 8, 1, 64);

    public static final ModConfigSpec.IntValue GATE_TELEPORT_COOLDOWN_TICKS = BUILDER
            .comment("Cooldown in ticks before the same entity can trigger Stargate travel again.")
            .defineInRange("gateTeleportCooldownTicks", 40, 1, 200);

    public static final ModConfigSpec.BooleanValue ENABLE_ORE_RETROGEN = BUILDER
            .comment("Generates LanteaCraft ores in already-existing chunks when they are loaded. Newly generated chunks already receive ores through normal worldgen.")
            .define("enableOreRetrogen", false);

    public static final ModConfigSpec.BooleanValue ENABLE_EXPLODING_ENCHANTMENT = BUILDER
            .comment("Enables explosions from projectiles fired by ranged weapons with the Exploding enchantment.")
            .define("enableExplodingEnchantment", true);

    public static final ModConfigSpec.BooleanValue PROTECT_GENERATED_GATES = BUILDER
            .comment("Prevents non-creative players from breaking or disassembling generated Stargate installations and their linked DHDs.")
            .define("protectGeneratedGates", true);

    public static final ModConfigSpec.IntValue ORE_RETROGEN_CHUNKS_PER_TICK = BUILDER
            .comment("Maximum number of old chunks to process for LanteaCraft ore retrogen per dimension tick.")
            .defineInRange("oreRetrogenChunksPerTick", 1, 1, 16);

    public static final ModConfigSpec.BooleanValue ENABLE_FE_POWER;
    public static final ModConfigSpec.BooleanValue REQUIRE_POWER_TO_DIAL;
    public static final ModConfigSpec.BooleanValue REQUIRE_POWER_TO_MAINTAIN_WORMHOLE;
    public static final ModConfigSpec.BooleanValue REQUIRE_POWER_FOR_ENERGY_IRIS;
    public static final ModConfigSpec.BooleanValue GENERATED_GATES_HAVE_ANCIENT_POWER;
    public static final ModConfigSpec.BooleanValue GENERATED_GATES_CAN_BE_REMOTE_DRAINED;
    public static final ModConfigSpec.BooleanValue GENERATED_GATES_ACCEPT_PLAYER_POWER;
    public static final ModConfigSpec.BooleanValue GENERATED_GATES_PREFER_PLAYER_POWER;
    public static final ModConfigSpec.LongValue GENERATED_GATES_ANCIENT_POWER_OPERATION_LIMIT;
    public static final ModConfigSpec.IntValue ZPM_CAPACITY;
    public static final ModConfigSpec.IntValue ZPM_MAX_TRANSFER;
    public static final ModConfigSpec.IntValue ZPM_HUB_MAX_EXTRACT;
    public static final ModConfigSpec.BooleanValue PLAYER_BUILT_GATES_REQUIRE_POWER_TO_DIAL;
    public static final ModConfigSpec.BooleanValue PLAYER_BUILT_GATES_REQUIRE_POWER_TO_SUSTAIN;
    public static final ModConfigSpec.LongValue GATE_BUFFER_CAPACITY;
    public static final ModConfigSpec.IntValue GATE_MAX_RECEIVE;
    public static final ModConfigSpec.IntValue GATE_MAX_EXTRACT;
    public static final ModConfigSpec.BooleanValue ALLOW_GATE_ENERGY_EXTRACT;
    public static final ModConfigSpec.LongValue DIAL_COST_SAME_DIMENSION;
    public static final ModConfigSpec.LongValue DIAL_COST_CROSS_DIMENSION;
    public static final ModConfigSpec.LongValue DIAL_COST_ATLANTIS;
    public static final ModConfigSpec.LongValue ACTIVE_COST_PER_TICK;
    public static final ModConfigSpec.LongValue ACTIVE_COST_ATLANTIS_PER_TICK;
    public static final ModConfigSpec.LongValue CROSS_DIMENSION_COST_PER_TICK;
    public static final ModConfigSpec.BooleanValue CLOSE_WORMHOLE_WHEN_POWER_RUNS_OUT;
    public static final ModConfigSpec.BooleanValue ORIGIN_PAYS_DIAL_COST;
    public static final ModConfigSpec.EnumValue<com.pclogix.lanteacraft.power.WormholePowerMode> WORMHOLE_POWER_MODE;
    public static final ModConfigSpec.BooleanValue ENABLE_DISTANCE_COST;
    public static final ModConfigSpec.DoubleValue SAME_DIMENSION_DISTANCE_MULTIPLIER;
    public static final ModConfigSpec.LongValue SAME_DIMENSION_DISTANCE_COST_CAP;
    public static final ModConfigSpec.DoubleValue CROSS_DIMENSION_DIAL_MULTIPLIER;
    public static final ModConfigSpec.DoubleValue CROSS_DIMENSION_SUSTAIN_MULTIPLIER;
    public static final ModConfigSpec.BooleanValue USE_DISTANCE_TIERS;
    public static final ModConfigSpec.IntValue NEAR_DISTANCE_BLOCKS;
    public static final ModConfigSpec.IntValue MEDIUM_DISTANCE_BLOCKS;
    public static final ModConfigSpec.DoubleValue NEAR_DISTANCE_MULTIPLIER;
    public static final ModConfigSpec.DoubleValue MEDIUM_DISTANCE_MULTIPLIER;
    public static final ModConfigSpec.DoubleValue FAR_DISTANCE_MULTIPLIER;
    public static final ModConfigSpec.LongValue ENERGY_IRIS_CLOSED_COST_PER_TICK;
    public static final ModConfigSpec.LongValue ENERGY_IRIS_ENTITY_IMPACT_COST;
    public static final ModConfigSpec.LongValue ENERGY_IRIS_KAWOOSH_BLOCK_COST;
    public static final ModConfigSpec.BooleanValue ENERGY_IRIS_FAIL_OPEN_WHEN_UNPOWERED;
    public static final ModConfigSpec.BooleanValue NAQUADAH_GENERATOR_ENABLED;
    public static final ModConfigSpec.LongValue NAQUADAH_GENERATOR_CAPACITY;
    public static final ModConfigSpec.IntValue NAQUADAH_GENERATOR_MAX_OUTPUT;
    public static final ModConfigSpec.IntValue NAQUADAH_GENERATOR_FE_PER_TICK;
    public static final ModConfigSpec.IntValue NAQUADAH_GENERATOR_FUEL_TICKS_PER_NAQUADAH;
    public static final ModConfigSpec.LongValue NAQUADAH_GENERATOR_SHARD_FE;
    public static final ModConfigSpec.LongValue NAQUADAH_GENERATOR_INGOT_FE;
    public static final ModConfigSpec.LongValue NAQUADAH_GENERATOR_BLOCK_FE;

    static {
        BUILDER.push("power");
        ENABLE_FE_POWER = BUILDER.define("enableFePower", true);
        REQUIRE_POWER_TO_DIAL = BUILDER.define("requirePowerToDial", false);
        REQUIRE_POWER_TO_MAINTAIN_WORMHOLE = BUILDER.define("requirePowerToMaintainWormhole", false);
        REQUIRE_POWER_FOR_ENERGY_IRIS = BUILDER.define("requirePowerForEnergyIris", true);

        BUILDER.push("generated_gates");
        GENERATED_GATES_HAVE_ANCIENT_POWER = BUILDER
                .comment("Generated gates are ancient installations with buried power systems.",
                        "They can operate without normal FE when this is enabled.")
                .define("generatedGatesHaveAncientPower", true);
        GENERATED_GATES_CAN_BE_REMOTE_DRAINED = BUILDER
                .comment("If false, generated gates cannot be used as remote sustain batteries by other gates.",
                        "This prevents players from accidentally draining discovered ancient gates.")
                .define("generatedGatesCanBeRemoteDrained", false);
        GENERATED_GATES_ACCEPT_PLAYER_POWER = BUILDER
                .comment("Allows players to connect FE or install crystals into generated gates for local upgrades, automation, irises, or pack-specific behavior.")
                .define("generatedGatesAcceptPlayerPower", true);
        GENERATED_GATES_PREFER_PLAYER_POWER = BUILDER
                .comment("If true, generated gates spend connected FE/crystal power before using their ancient power allowance for gate operation.")
                .define("generatedGatesPreferPlayerPower", false);
        GENERATED_GATES_ANCIENT_POWER_OPERATION_LIMIT = BUILDER
                .comment("Maximum FE cost a generated gate's ancient power can satisfy for a single operation.",
                        "Costs above this value must be paid with player-provided FE or an installed DHD energy crystal.")
                .defineInRange("generatedGatesAncientPowerOperationLimit", 2_000_000L, 0L, Long.MAX_VALUE);
        BUILDER.pop();

        BUILDER.push("zpm");
        ZPM_CAPACITY = BUILDER
                .comment("Stored FE in a single Zero Point Module.")
                .defineInRange("zpmCapacity", (Integer.MAX_VALUE / 3) + 1, 0, Integer.MAX_VALUE);
        ZPM_MAX_TRANSFER = BUILDER
                .comment("Maximum FE a bare ZPM item can provide in one extraction call.")
                .defineInRange("zpmMaxTransfer", 100_000_000, 0, Integer.MAX_VALUE);
        ZPM_HUB_MAX_EXTRACT = BUILDER
                .comment("Maximum FE a ZPM Hub can provide in one extraction call.",
                        "This is intended to allow high instantaneous Stargate bursts without making ZPMs passive generators.")
                .defineInRange("zpmHubMaxExtract", 100_000_000, 0, Integer.MAX_VALUE);
        BUILDER.pop();

        BUILDER.push("player_built_gates");
        PLAYER_BUILT_GATES_REQUIRE_POWER_TO_DIAL = BUILDER.define("playerBuiltGatesRequirePowerToDial", true);
        PLAYER_BUILT_GATES_REQUIRE_POWER_TO_SUSTAIN = BUILDER.define("playerBuiltGatesRequirePowerToSustain", true);
        BUILDER.pop();

        GATE_BUFFER_CAPACITY = BUILDER.defineInRange("gateBufferCapacity", 2_000_000L, 0L, Integer.MAX_VALUE);
        GATE_MAX_RECEIVE = BUILDER.defineInRange("gateMaxReceive", 20_000, 0, Integer.MAX_VALUE);
        GATE_MAX_EXTRACT = BUILDER.defineInRange("gateMaxExtract", 20_000, 0, Integer.MAX_VALUE);
        ALLOW_GATE_ENERGY_EXTRACT = BUILDER.define("allowGateEnergyExtract", false);
        DIAL_COST_SAME_DIMENSION = BUILDER.defineInRange("dialCostSameDimension", 50_000L, 0L, Long.MAX_VALUE);
        DIAL_COST_CROSS_DIMENSION = BUILDER.defineInRange("dialCostCrossDimension", 100_000L, 0L, Long.MAX_VALUE);
        DIAL_COST_ATLANTIS = BUILDER
                .comment("Special long-range dial cost for the fixed Atlantis address. Set to 0 to use the normal cross-dimension cost.")
                .defineInRange("dialCostAtlantis", 75_000_000L, 0L, Long.MAX_VALUE);
        ACTIVE_COST_PER_TICK = BUILDER.defineInRange("activeCostPerTick", 100L, 0L, Long.MAX_VALUE);
        ACTIVE_COST_ATLANTIS_PER_TICK = BUILDER
                .comment("Special sustain cost for wormholes involving the fixed Atlantis address.",
                        "This cost must be paid from linked ZPM Hub energy rather than ordinary FE buffers.")
                .defineInRange("activeCostAtlantisPerTick", 50_000L, 0L, Long.MAX_VALUE);
        CROSS_DIMENSION_COST_PER_TICK = BUILDER.defineInRange("crossDimensionCostPerTick", 250L, 0L, Long.MAX_VALUE);
        CLOSE_WORMHOLE_WHEN_POWER_RUNS_OUT = BUILDER.define("closeWormholeWhenPowerRunsOut", true);
        ORIGIN_PAYS_DIAL_COST = BUILDER.define("originPaysDialCost", true);
        WORMHOLE_POWER_MODE = BUILDER.defineEnum("wormholePowerMode", com.pclogix.lanteacraft.power.WormholePowerMode.PREFER_ORIGIN);

        BUILDER.push("distance");
        ENABLE_DISTANCE_COST = BUILDER.define("enableDistanceCost", true);
        SAME_DIMENSION_DISTANCE_MULTIPLIER = BUILDER.defineInRange("sameDimensionDistanceMultiplier", 0.05D, 0.0D, 1_000_000.0D);
        SAME_DIMENSION_DISTANCE_COST_CAP = BUILDER.defineInRange("sameDimensionDistanceCostCap", 250_000L, 0L, Long.MAX_VALUE);
        CROSS_DIMENSION_DIAL_MULTIPLIER = BUILDER.defineInRange("crossDimensionDialMultiplier", 1.0D, 0.0D, 1_000_000.0D);
        CROSS_DIMENSION_SUSTAIN_MULTIPLIER = BUILDER.defineInRange("crossDimensionSustainMultiplier", 2.5D, 0.0D, 1_000_000.0D);
        USE_DISTANCE_TIERS = BUILDER.define("useDistanceTiers", true);
        NEAR_DISTANCE_BLOCKS = BUILDER.defineInRange("nearDistanceBlocks", 1_000, 0, Integer.MAX_VALUE);
        MEDIUM_DISTANCE_BLOCKS = BUILDER.defineInRange("mediumDistanceBlocks", 10_000, 0, Integer.MAX_VALUE);
        NEAR_DISTANCE_MULTIPLIER = BUILDER.defineInRange("nearDistanceMultiplier", 1.0D, 0.0D, 1_000_000.0D);
        MEDIUM_DISTANCE_MULTIPLIER = BUILDER.defineInRange("mediumDistanceMultiplier", 1.5D, 0.0D, 1_000_000.0D);
        FAR_DISTANCE_MULTIPLIER = BUILDER.defineInRange("farDistanceMultiplier", 3.0D, 0.0D, 1_000_000.0D);
        BUILDER.pop();

        BUILDER.push("iris");
        ENERGY_IRIS_CLOSED_COST_PER_TICK = BUILDER.defineInRange("energyIrisClosedCostPerTick", 20L, 0L, Long.MAX_VALUE);
        ENERGY_IRIS_ENTITY_IMPACT_COST = BUILDER.defineInRange("energyIrisEntityImpactCost", 1_000L, 0L, Long.MAX_VALUE);
        ENERGY_IRIS_KAWOOSH_BLOCK_COST = BUILDER.defineInRange("energyIrisKawooshBlockCost", 25_000L, 0L, Long.MAX_VALUE);
        ENERGY_IRIS_FAIL_OPEN_WHEN_UNPOWERED = BUILDER.define("energyIrisFailOpenWhenUnpowered", true);
        BUILDER.pop();

        BUILDER.push("naquadahGenerator");
        NAQUADAH_GENERATOR_ENABLED = BUILDER.define("enabled", true);
        NAQUADAH_GENERATOR_CAPACITY = BUILDER.defineInRange("capacity", 2_000_000L, 0L, Integer.MAX_VALUE);
        NAQUADAH_GENERATOR_MAX_OUTPUT = BUILDER.defineInRange("maxOutput", 20_000, 0, Integer.MAX_VALUE);
        NAQUADAH_GENERATOR_FE_PER_TICK = BUILDER.defineInRange("fePerTick", 20_000, 0, Integer.MAX_VALUE);
        NAQUADAH_GENERATOR_FUEL_TICKS_PER_NAQUADAH = BUILDER.defineInRange("fuelTicksPerNaquadah", 1, 1, Integer.MAX_VALUE);
        BUILDER.push("fuels");
        NAQUADAH_GENERATOR_SHARD_FE = BUILDER.defineInRange("naquadahShardFE", 8_192L, 0L, Long.MAX_VALUE);
        NAQUADAH_GENERATOR_INGOT_FE = BUILDER.defineInRange("naquadahIngotFE", 0L, 0L, Long.MAX_VALUE);
        NAQUADAH_GENERATOR_BLOCK_FE = BUILDER.defineInRange("naquadahBlockFE", 0L, 0L, Long.MAX_VALUE);
        BUILDER.pop();
        BUILDER.pop();
        BUILDER.pop();
    }

    static final ModConfigSpec SPEC = BUILDER.build();

    private Config() {
    }
}
