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

    static final ModConfigSpec SPEC = BUILDER.build();

    private Config() {
    }
}
