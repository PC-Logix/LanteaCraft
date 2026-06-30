package com.pclogix.lanteacraft.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.pclogix.lanteacraft.gate.StargateEntry;
import com.pclogix.lanteacraft.gate.StargateNetworkSavedData;
import com.pclogix.lanteacraft.gate.StargateRecord;
import com.pclogix.lanteacraft.gate.StargateStatus;
import com.pclogix.lanteacraft.worldgen.LanteaWorldgenEvents;
import com.pclogix.lanteacraft.worldgen.PlannedStargate;
import com.pclogix.lanteacraft.worldgen.PlannedStargateResolver;
import com.pclogix.lanteacraft.worldgen.PlannedStargateSavedData;
import com.pclogix.lanteacraft.worldgen.StargateVillageLocator;
import com.pclogix.lanteacraft.worldgen.StargateVillagePlanner;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

public final class LanteaCommands {
    private static final int MAX_SCAN_RADIUS = 1024;
    private static final int LIST_LIMIT = 20;
    private static final int DEBUG_LOOT_SCAN_RADIUS = 512;
    private static final int DEBUG_LOOT_MIN_PLANS = 6;

    private LanteaCommands() {
    }

    public static void register(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("lanteacraft")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("planned_gates")
                        .then(Commands.argument("radius", IntegerArgumentType.integer(1, MAX_SCAN_RADIUS))
                                .executes(context -> discoverPlannedGates(context.getSource(), context.getSource().getLevel(), context.getSource().getPlayerOrException().blockPosition(), IntegerArgumentType.getInteger(context, "radius")))))
                .then(Commands.literal("gates")
                        .then(Commands.literal("scan")
                                .then(Commands.argument("radius", IntegerArgumentType.integer(1, MAX_SCAN_RADIUS))
                                        .executes(context -> discoverPlannedGates(context.getSource(), context.getSource().getLevel(), context.getSource().getPlayerOrException().blockPosition(), IntegerArgumentType.getInteger(context, "radius")))))
                        .then(Commands.literal("list")
                                .executes(context -> listGates(context.getSource(), context.getSource().getLevel(), context.getSource().getPlayerOrException().blockPosition())))
                        .then(Commands.literal("nearest")
                                .executes(context -> nearestGate(context.getSource(), context.getSource().getLevel(), context.getSource().getPlayerOrException().blockPosition())))
                        .then(Commands.literal("teleport")
                                .then(Commands.argument("address", StringArgumentType.word())
                                        .executes(context -> teleportToGate(context.getSource().getPlayerOrException(), StringArgumentType.getString(context, "address"))))))
                .then(Commands.literal("loot_chest")
                        .executes(context -> spawnLootChest(context.getSource().getPlayerOrException()))));
    }

    private static int discoverPlannedGates(CommandSourceStack source, ServerLevel level, BlockPos origin, int radius) {
        Set<BlockPos> villages = new LinkedHashSet<>();
        int step = Math.max(64, radius * 16 / 8);
        int blockRadius = radius * 16;
        for (int x = -blockRadius; x <= blockRadius; x += step) {
            for (int z = -blockRadius; z <= blockRadius; z += step) {
                StargateVillageLocator.nearestVillage(level, origin.offset(x, 0, z), radius, false).ifPresent(villages::add);
            }
        }

        PlannedStargateSavedData data = PlannedStargateSavedData.get(level);
        int planned = 0;
        for (BlockPos village : villages) {
            if (StargateVillagePlanner.shouldHaveGate(level, village)) {
                PlannedStargate plan = data.remember(StargateVillagePlanner.plan(level, village));
                StargateNetworkSavedData.get(level).reserveGate(plan.address(), plan.dimension(), plan.basePos(), plan.facing(), plan.variant(), "planned");
                planned++;
            }
        }

        int discovered = planned;
        source.sendSuccess(() -> Component.literal("Planned Stargates discovered: " + discovered).withStyle(ChatFormatting.AQUA), false);
        showKnownGates(source, level, origin);
        return planned;
    }

    private static int listGates(CommandSourceStack source, ServerLevel level, BlockPos origin) {
        showKnownGates(source, level, origin);
        return knownGates(level).size();
    }

    private static int nearestGate(CommandSourceStack source, ServerLevel level, BlockPos origin) {
        Optional<GateSummary> nearest = knownGates(level).stream()
                .filter(gate -> gate.dimension().equals(level.dimension().location()))
                .min(Comparator.comparingDouble(gate -> gate.basePos().distSqr(origin)));
        if (nearest.isEmpty()) {
            source.sendFailure(Component.literal("No known gates in this dimension. Try /lanteacraft gates scan 256").withStyle(ChatFormatting.RED));
            return 0;
        }

        GateSummary gate = nearest.get();
        source.sendSuccess(() -> Component.literal("Nearest gate: " + formatGate(gate, level, origin)).withStyle(ChatFormatting.AQUA), false);
        return 1;
    }

    private static int teleportToGate(ServerPlayer player, String address) {
        ServerLevel level = player.serverLevel();
        Optional<StargateEntry> resolved = PlannedStargateResolver.resolve(level, address);
        if (resolved.isEmpty()) {
            Optional<StargateRecord> record = StargateNetworkSavedData.get(level).findByAddress(address);
            String message = record.isPresent() && record.get().status() == StargateStatus.DORMANT
                    ? "Gate target is unavailable: " + address
                    : "Unknown gate address: " + address + ". Try /lanteacraft gates scan 256";
            player.sendSystemMessage(Component.literal(message).withStyle(ChatFormatting.RED));
            return 0;
        }

        StargateEntry gate = resolved.get();
        ServerLevel targetLevel = level.getServer().getLevel(ResourceKey.create(Registries.DIMENSION, gate.dimension()));
        if (targetLevel == null) {
            player.sendSystemMessage(Component.literal("Gate dimension is not loaded: " + gate.dimension()).withStyle(ChatFormatting.RED));
            return 0;
        }

        BlockPos landing = landingPos(targetLevel, gate);
        player.teleportTo(targetLevel, landing.getX() + 0.5D, landing.getY(), landing.getZ() + 0.5D, player.getYRot(), player.getXRot());
        player.sendSystemMessage(Component.literal("Teleported to " + gate.address() + " at " + gate.basePos().getX() + "," + gate.basePos().getY() + "," + gate.basePos().getZ()).withStyle(ChatFormatting.GREEN));
        return 1;
    }

    private static void showKnownGates(CommandSourceStack source, ServerLevel level, BlockPos origin) {
        List<GateSummary> gates = knownGates(level);
        if (gates.isEmpty()) {
            source.sendSuccess(() -> Component.literal("No known gates yet. Try /lanteacraft gates scan 256").withStyle(ChatFormatting.YELLOW), false);
            return;
        }

        source.sendSuccess(() -> Component.literal("Known Stargates: " + gates.size()).withStyle(ChatFormatting.AQUA), false);
        gates.stream()
                .sorted(Comparator.comparingDouble(gate -> gate.dimension().equals(level.dimension().location()) ? gate.basePos().distSqr(origin) : Double.MAX_VALUE))
                .limit(LIST_LIMIT)
                .forEach(gate -> source.sendSuccess(() -> Component.literal(formatGate(gate, level, origin)).withStyle(gate.status() == StargateStatus.ACTIVE ? ChatFormatting.GRAY : ChatFormatting.DARK_GRAY), false));
    }

    private static List<GateSummary> knownGates(ServerLevel level) {
        List<GateSummary> gates = new ArrayList<>();
        StargateNetworkSavedData network = StargateNetworkSavedData.get(level);
        for (StargateRecord record : network.getAllRecords()) {
            gates.add(new GateSummary(
                    record.gateId(),
                    record.address(),
                    record.dimension(),
                    record.basePos(),
                    record.facing(),
                    record.variant().id(),
                    record.status()));
        }

        PlannedStargateSavedData planned = PlannedStargateSavedData.get(level);
        for (PlannedStargate plan : planned.plans(level)) {
            boolean alreadyLive = gates.stream().anyMatch(gate -> gate.address().equals(plan.address()));
            if (!alreadyLive) {
                gates.add(new GateSummary(null, plan.address(), plan.dimension(), plan.basePos(), plan.facing(), plan.variant().id(), planned.isPlaced(plan.address()) ? StargateStatus.ACTIVE : StargateStatus.RESERVED));
            }
        }
        return gates;
    }

    private static String formatGate(GateSummary gate, ServerLevel level, BlockPos origin) {
        String distance = gate.dimension().equals(level.dimension().location())
                ? " ~" + (int)Math.sqrt(gate.basePos().distSqr(origin)) + "m"
                : " other-dimension";
        return gate.address() + " " + gate.kind()
                + " " + gate.status().name().toLowerCase()
                + (gate.gateId() == null ? "" : " " + gate.gateId())
                + " " + gate.dimension()
                + " gate " + gate.basePos().getX() + "," + gate.basePos().getY() + "," + gate.basePos().getZ()
                + " facing " + gate.facing().getName()
                + distance;
    }

    private static BlockPos landingPos(ServerLevel level, StargateEntry gate) {
        BlockPos preferred = gate.basePos().relative(gate.facing(), 6);
        int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, preferred.getX(), preferred.getZ());
        return new BlockPos(preferred.getX(), Math.max(y, gate.basePos().getY() + 1), preferred.getZ());
    }

    private static int spawnLootChest(ServerPlayer player) {
        ServerLevel level = player.serverLevel();
        BlockPos pos = player.blockPosition().relative(player.getDirection(), 2);
        level.setBlock(pos, Blocks.CHEST.defaultBlockState(), 3);
        if (level.getBlockEntity(pos) instanceof ChestBlockEntity chest) {
            PlannedStargateSavedData data = PlannedStargateSavedData.get(level);
            if (data.plans(level).size() < DEBUG_LOOT_MIN_PLANS) {
                discoverPlannedGates(player.createCommandSourceStack(), level, player.blockPosition(), DEBUG_LOOT_SCAN_RADIUS);
            }
            LanteaWorldgenEvents.fillDebugLootChest(level, chest);
        }
        player.sendSystemMessage(Component.translatable("message.lanteacraft.loot_chest_spawned").withStyle(ChatFormatting.GREEN));
        return 1;
    }

    private record GateSummary(UUID gateId, String address, net.minecraft.resources.ResourceLocation dimension, BlockPos basePos, net.minecraft.core.Direction facing, String kind, StargateStatus status) {
    }
}
