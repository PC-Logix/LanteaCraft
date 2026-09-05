package com.pclogix.lanteacraft.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.pclogix.lanteacraft.gate.StargateEntry;
import com.pclogix.lanteacraft.gate.StargateNetworkSavedData;
import com.pclogix.lanteacraft.gate.StargateRecord;
import com.pclogix.lanteacraft.gate.StargateStatus;
import com.pclogix.lanteacraft.worldgen.AtlantisCityManager;
import com.pclogix.lanteacraft.worldgen.ExpeditionGenerator;
import com.pclogix.lanteacraft.worldgen.ExpeditionInstance;
import com.pclogix.lanteacraft.worldgen.ExpeditionSavedData;
import com.pclogix.lanteacraft.worldgen.ExpeditionWorldBorder;
import com.pclogix.lanteacraft.worldgen.LanteaWorldgenEvents;
import com.pclogix.lanteacraft.worldgen.PlannedStargate;
import com.pclogix.lanteacraft.worldgen.PlannedStargateResolver;
import com.pclogix.lanteacraft.worldgen.PlannedStargateSavedData;
import com.pclogix.lanteacraft.worldgen.StargateVillageLocator;
import com.pclogix.lanteacraft.worldgen.StargateVillagePlanner;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.Direction;
import net.minecraft.core.FrontAndTop;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.JigsawBlock;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.JigsawBlockEntity;
import net.minecraft.world.level.block.entity.JigsawBlockEntity.JointType;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.storage.LevelResource;
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
                .then(Commands.literal("atlantis")
                        .then(Commands.literal("place_city")
                                .executes(context -> placeAtlantisCity(context.getSource(), defaultAtlantisOrigin(context.getSource())))
                                .then(Commands.argument("origin", BlockPosArgument.blockPos())
                                        .executes(context -> placeAtlantisCity(context.getSource(), BlockPosArgument.getLoadedBlockPos(context, "origin")))))
                        .then(Commands.literal("city_bounds")
                                .executes(context -> showAtlantisCityBounds(context.getSource(), defaultAtlantisOrigin(context.getSource())))
                                .then(Commands.argument("origin", BlockPosArgument.blockPos())
                                        .executes(context -> showAtlantisCityBounds(context.getSource(), BlockPosArgument.getLoadedBlockPos(context, "origin")))))
                        .then(Commands.literal("export_city")
                                .then(Commands.argument("from", BlockPosArgument.blockPos())
                                        .then(Commands.argument("to", BlockPosArgument.blockPos())
                                                .then(Commands.argument("name", StringArgumentType.word())
                                                        .executes(context -> exportAtlantisCity(
                                                                context.getSource(),
                                                                BlockPosArgument.getLoadedBlockPos(context, "from"),
                                                                BlockPosArgument.getLoadedBlockPos(context, "to"),
                                                                defaultAtlantisOrigin(context.getSource()),
                                                                StringArgumentType.getString(context, "name")))))))
                        .then(Commands.literal("export_city_relative")
                                .then(Commands.argument("from", BlockPosArgument.blockPos())
                                        .then(Commands.argument("to", BlockPosArgument.blockPos())
                                                .then(Commands.argument("origin", BlockPosArgument.blockPos())
                                                        .then(Commands.argument("name", StringArgumentType.word())
                                                                .executes(context -> exportAtlantisCity(
                                                                        context.getSource(),
                                                                        BlockPosArgument.getLoadedBlockPos(context, "from"),
                                                                        BlockPosArgument.getLoadedBlockPos(context, "to"),
                                                                        BlockPosArgument.getLoadedBlockPos(context, "origin"),
                                                                        StringArgumentType.getString(context, "name"))))))))
                        .then(Commands.literal("drain_city")
                                .then(Commands.argument("from", BlockPosArgument.blockPos())
                                        .then(Commands.argument("to", BlockPosArgument.blockPos())
                                                .executes(context -> drainAtlantisCity(
                                                        context.getSource(),
                                                        BlockPosArgument.getLoadedBlockPos(context, "from"),
                                                        BlockPosArgument.getLoadedBlockPos(context, "to"))))))
                        .then(Commands.literal("drain_city_around")
                                .then(Commands.argument("radius", IntegerArgumentType.integer(1, 160))
                                        .executes(context -> drainAtlantisCityAround(
                                                context.getSource().getPlayerOrException(),
                                                IntegerArgumentType.getInteger(context, "radius"))))))
                .then(Commands.literal("loot_chest")
                        .executes(context -> spawnLootChest(context.getSource().getPlayerOrException())))
                .then(Commands.literal("expeditions")
                        .then(Commands.literal("find_spawners")
                                .executes(context -> findExpeditionSpawners(context.getSource().getPlayerOrException())))
                        .then(Commands.literal("export_templates")
                                .executes(context -> exportExpeditionTemplates(context.getSource())))
                        .then(Commands.literal("convert_orange_jigsaws")
                                .then(Commands.argument("from", BlockPosArgument.blockPos())
                                        .then(Commands.argument("to", BlockPosArgument.blockPos())
                                                .then(Commands.argument("pool", ResourceLocationArgument.id())
                                                        .executes(context -> convertOrangeJigsaws(
                                                                context.getSource(),
                                                                BlockPosArgument.getLoadedBlockPos(context, "from"),
                                                                BlockPosArgument.getLoadedBlockPos(context, "to"),
                                                                ResourceLocationArgument.getId(context, "pool"))))))))
                .then(Commands.literal("spawn_trial")
                        .executes(context -> spawnTrial(context.getSource().getPlayerOrException(), randomTrialSeed(context.getSource().getPlayerOrException()), false))
                        .then(Commands.literal("clear")
                                .executes(context -> spawnTrial(context.getSource().getPlayerOrException(), randomTrialSeed(context.getSource().getPlayerOrException()), true)))
                        .then(Commands.argument("seed", LongArgumentType.longArg())
                                .executes(context -> spawnTrial(context.getSource().getPlayerOrException(), LongArgumentType.getLong(context, "seed"), false))
                                .then(Commands.literal("clear")
                                        .executes(context -> spawnTrial(context.getSource().getPlayerOrException(), LongArgumentType.getLong(context, "seed"), true))))));

    }

    private static int findExpeditionSpawners(ServerPlayer player) {
        ServerLevel level = player.serverLevel();
        Optional<ExpeditionInstance> nearest = ExpeditionSavedData.get(level).expeditions().stream()
                .filter(ExpeditionInstance::generated)
                .filter(expedition -> expedition.dimension().equals(level.dimension().location()))
                .min(Comparator.comparingDouble(expedition -> expedition.basePos().distSqr(player.blockPosition())));
        if (nearest.isEmpty()) {
            player.sendSystemMessage(Component.literal("No generated expedition was found.").withStyle(ChatFormatting.RED));
            return 0;
        }

        ExpeditionInstance expedition = nearest.get();
        List<BlockPos> remaining = expedition.combatRoomCenters().stream()
                .filter(pos -> level.getBlockState(pos).is(Blocks.SPAWNER))
                .toList();
        if (expedition.rewardUnlocked() && remaining.isEmpty()) {
            player.sendSystemMessage(Component.literal("Expedition " + expedition.address() + " is already unlocked.")
                    .withStyle(ChatFormatting.GREEN));
        } else if (expedition.rewardUnlocked()) {
            ExpeditionSavedData.get(level).markRewardLocked(expedition.address());
            player.sendSystemMessage(Component.literal("Repaired stale unlocked state for expedition " + expedition.address() + ".")
                    .withStyle(ChatFormatting.YELLOW));
        }
        player.sendSystemMessage(Component.literal("Expedition " + expedition.address() + " has " + remaining.size()
                + " remaining Goa'uld spawner(s):").withStyle(ChatFormatting.GOLD));
        for (BlockPos pos : remaining) {
            player.sendSystemMessage(Component.literal("  " + formatPos(pos)).withStyle(ChatFormatting.YELLOW));
            for (int y = 1; y <= 24; y++) {
                level.sendParticles(ParticleTypes.END_ROD, pos.getX() + 0.5D, pos.getY() + y, pos.getZ() + 0.5D,
                        1, 0.0D, 0.0D, 0.0D, 0.0D);
            }
        }
        return remaining.size();
    }

    private static int exportExpeditionTemplates(CommandSourceStack source) {
        MinecraftServer server = source.getServer();
        Map<ResourceLocation, Resource> resources = server.getResourceManager().listResources(
                "structure/expedition",
                id -> id.getNamespace().equals("lanteacraft") && id.getPath().endsWith(".nbt"));
        if (resources.isEmpty()) {
            source.sendFailure(Component.literal("No bundled expedition templates were found.").withStyle(ChatFormatting.RED));
            return 0;
        }

        Path generatedRoot = server.getWorldPath(LevelResource.GENERATED_DIR);
        int copied = 0;
        try {
            for (Map.Entry<ResourceLocation, Resource> entry : resources.entrySet()) {
                ResourceLocation id = entry.getKey();
                String generatedName = id.getPath()
                        .replaceFirst("^structure/", "")
                        .replaceFirst("\\.nbt$", "");
                ResourceLocation structureId = ResourceLocation.fromNamespaceAndPath(id.getNamespace(), generatedName);
                Path target = generatedStructurePath(generatedRoot, structureId);
                Files.createDirectories(target.getParent());
                try (InputStream input = entry.getValue().open()) {
                    Files.copy(input, target, StandardCopyOption.REPLACE_EXISTING);
                }
                copied++;
            }
        } catch (IOException ex) {
            source.sendFailure(Component.literal("Could not export expedition templates: " + ex.getMessage()).withStyle(ChatFormatting.RED));
            return copied;
        }

        int exported = copied;
        source.sendSuccess(() -> Component.literal("Exported " + exported + " expedition template(s) to " + generatedRoot
                + ". Load gate room as lanteacraft:expedition/gate_room. Use /reload after saving edits.")
                .withStyle(ChatFormatting.GREEN), true);
        return copied;
    }

    private static Path generatedStructurePath(Path generatedRoot, ResourceLocation structureId) {
        Path path = generatedRoot.resolve(structureId.getNamespace()).resolve("structures");
        for (String segment : structureId.getPath().split("/")) {
            path = path.resolve(segment);
        }
        return path.resolveSibling(path.getFileName() + ".nbt");
    }

    private static int convertOrangeJigsaws(CommandSourceStack source, BlockPos from, BlockPos to, ResourceLocation poolId) {
        ServerLevel level = source.getLevel();
        BlockPos min = new BlockPos(
                Math.min(from.getX(), to.getX()),
                Math.min(from.getY(), to.getY()),
                Math.min(from.getZ(), to.getZ()));
        BlockPos max = new BlockPos(
                Math.max(from.getX(), to.getX()),
                Math.max(from.getY(), to.getY()),
                Math.max(from.getZ(), to.getZ()));
        ResourceLocation door = ResourceLocation.fromNamespaceAndPath("lanteacraft", "expedition/door");
        ResourceKey<StructureTemplatePool> poolKey = ResourceKey.create(Registries.TEMPLATE_POOL, poolId);
        int converted = 0;
        Set<BlockPos> visited = new HashSet<>();
        for (int x = min.getX(); x <= max.getX(); x++) {
            for (int y = min.getY(); y <= max.getY(); y++) {
                for (int z = min.getZ(); z <= max.getZ(); z++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    if (visited.contains(pos) || !level.getBlockState(pos).is(Blocks.ORANGE_WOOL)) {
                        continue;
                    }
                    List<BlockPos> marker = collectOrangeMarker(level, pos, min, max, visited);
                    if (marker.isEmpty()) {
                        continue;
                    }
                    BlockPos jigsawPos = markerBottomCenter(marker, min, max);
                    Direction front = outwardDirection(jigsawPos, min, max);
                    for (BlockPos markerPos : marker) {
                        level.setBlock(markerPos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
                    }
                    level.setBlock(jigsawPos, Blocks.JIGSAW.defaultBlockState()
                            .setValue(JigsawBlock.ORIENTATION, FrontAndTop.fromFrontAndTop(front, Direction.UP)), Block.UPDATE_ALL);
                    if (level.getBlockEntity(jigsawPos) instanceof JigsawBlockEntity jigsaw) {
                        jigsaw.setName(door);
                        jigsaw.setTarget(door);
                        jigsaw.setPool(poolKey);
                        jigsaw.setFinalState("minecraft:air");
                        jigsaw.setJoint(JointType.ALIGNED);
                        jigsaw.setChanged();
                    }
                    converted++;
                }
            }
        }
        int count = converted;
        source.sendSuccess(() -> Component.literal("Converted " + count + " orange wool doorway patch(es) to Jigsaw Blocks using pool " + poolId
                + ". Save the structure again with your Structure Block.").withStyle(ChatFormatting.GREEN), true);
        return converted;
    }

    private static List<BlockPos> collectOrangeMarker(ServerLevel level, BlockPos start, BlockPos min, BlockPos max, Set<BlockPos> visited) {
        List<BlockPos> marker = new ArrayList<>();
        Queue<BlockPos> queue = new ArrayDeque<>();
        queue.add(start);
        visited.add(start);
        while (!queue.isEmpty()) {
            BlockPos pos = queue.remove();
            marker.add(pos);
            for (Direction direction : Direction.values()) {
                BlockPos next = pos.relative(direction);
                if (visited.contains(next)
                        || next.getX() < min.getX() || next.getX() > max.getX()
                        || next.getY() < min.getY() || next.getY() > max.getY()
                        || next.getZ() < min.getZ() || next.getZ() > max.getZ()
                        || !level.getBlockState(next).is(Blocks.ORANGE_WOOL)) {
                    continue;
                }
                visited.add(next);
                queue.add(next);
            }
        }
        return marker;
    }

    private static BlockPos markerBottomCenter(List<BlockPos> marker, BlockPos structureMin, BlockPos structureMax) {
        int minX = marker.stream().mapToInt(BlockPos::getX).min().orElse(0);
        int minY = marker.stream().mapToInt(BlockPos::getY).min().orElse(0);
        int minZ = marker.stream().mapToInt(BlockPos::getZ).min().orElse(0);
        int maxX = marker.stream().mapToInt(BlockPos::getX).max().orElse(0);
        int maxZ = marker.stream().mapToInt(BlockPos::getZ).max().orElse(0);
        Direction front = outwardDirection(new BlockPos((minX + maxX) / 2, minY, (minZ + maxZ) / 2), structureMin, structureMax);
        if (front.getAxis() == Direction.Axis.X) {
            return new BlockPos(front == Direction.WEST ? minX : maxX, minY, (minZ + maxZ) / 2);
        }
        return new BlockPos((minX + maxX) / 2, minY, front == Direction.NORTH ? minZ : maxZ);
    }

    private static Direction outwardDirection(BlockPos pos, BlockPos min, BlockPos max) {
        int west = Math.abs(pos.getX() - min.getX());
        int east = Math.abs(max.getX() - pos.getX());
        int north = Math.abs(pos.getZ() - min.getZ());
        int south = Math.abs(max.getZ() - pos.getZ());
        int closest = Math.min(Math.min(west, east), Math.min(north, south));
        if (closest == south) {
            return Direction.SOUTH;
        }
        if (closest == north) {
            return Direction.NORTH;
        }
        if (closest == east) {
            return Direction.EAST;
        }
        return Direction.WEST;
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
        ExpeditionWorldBorder.ensureDisabled(targetLevel);
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

    private static int placeAtlantisCity(CommandSourceStack source, BlockPos origin) {
        if (origin == null) {
            return 0;
        }
        try {
            int blocks = AtlantisCityManager.queuePlace(source.getLevel(), origin, source.getEntity() instanceof ServerPlayer player ? player : null);
            source.sendSuccess(() -> Component.literal("Atlantis city placement queued: " + blocks + " blocks.").withStyle(ChatFormatting.GREEN), true);
            return blocks;
        } catch (IOException ex) {
            source.sendFailure(Component.literal("Could not queue Atlantis city placement: " + ex.getMessage()).withStyle(ChatFormatting.RED));
            return 0;
        }
    }

    private static int showAtlantisCityBounds(CommandSourceStack source, BlockPos origin) {
        if (origin == null) {
            return 0;
        }
        try {
            Optional<AtlantisCityManager.CityBounds> bounds = AtlantisCityManager.cityBounds(source.getLevel(), origin);
            if (bounds.isEmpty()) {
                source.sendFailure(Component.literal("Atlantis city resource is not available.").withStyle(ChatFormatting.RED));
                return 0;
            }
            AtlantisCityManager.CityBounds cityBounds = bounds.get();
            source.sendSuccess(() -> Component.literal("Atlantis city bounds: from " + formatPos(cityBounds.min()) + " to " + formatPos(cityBounds.max())).withStyle(ChatFormatting.AQUA), false);
            return 1;
        } catch (IOException ex) {
            source.sendFailure(Component.literal("Could not read Atlantis city bounds: " + ex.getMessage()).withStyle(ChatFormatting.RED));
            return 0;
        }
    }

    private static int exportAtlantisCity(CommandSourceStack source, BlockPos from, BlockPos to, BlockPos origin, String name) {
        if (origin == null) {
            return 0;
        }
        try {
            int blocks = AtlantisCityManager.queueExport(source.getLevel(), from, to, origin, name, source.getEntity() instanceof ServerPlayer player ? player : null);
            source.sendSuccess(() -> Component.literal("Atlantis city export queued. Source template has " + blocks + " blocks.").withStyle(ChatFormatting.GREEN), true);
            return blocks;
        } catch (IOException ex) {
            source.sendFailure(Component.literal("Could not queue Atlantis city export: " + ex.getMessage()).withStyle(ChatFormatting.RED));
            return 0;
        }
    }

    private static int drainAtlantisCity(CommandSourceStack source, BlockPos from, BlockPos to) {
        try {
            int positions = AtlantisCityManager.queueDrain(source.getLevel(), from, to, source.getEntity() instanceof ServerPlayer player ? player : null);
            source.sendSuccess(() -> Component.literal("Atlantis city drain queued: " + positions + " positions.").withStyle(ChatFormatting.GREEN), true);
            return positions;
        } catch (IOException ex) {
            source.sendFailure(Component.literal("Could not queue Atlantis city drain: " + ex.getMessage()).withStyle(ChatFormatting.RED));
            return 0;
        }
    }

    private static int drainAtlantisCityAround(ServerPlayer player, int radius) {
        try {
            int positions = AtlantisCityManager.queueConnectedDrain(player.serverLevel(), player.blockPosition(), radius, player);
            player.sendSystemMessage(Component.literal("Atlantis connected drain queued: " + positions + " positions.").withStyle(ChatFormatting.GREEN));
            return positions;
        } catch (IOException ex) {
            player.sendSystemMessage(Component.literal("Could not queue Atlantis connected drain: " + ex.getMessage()).withStyle(ChatFormatting.RED));
            return 0;
        }
    }

    private static String formatPos(BlockPos pos) {
        return pos.getX() + "," + pos.getY() + "," + pos.getZ();
    }

    private static BlockPos defaultAtlantisOrigin(CommandSourceStack source) {
        try {
            return AtlantisCityManager.defaultOrigin(source.getLevel());
        } catch (IOException ex) {
            source.sendFailure(Component.literal("Could not choose default Atlantis city origin: " + ex.getMessage()).withStyle(ChatFormatting.RED));
            return null;
        }
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

    private static long randomTrialSeed(ServerPlayer player) {
        return player.serverLevel().random.nextLong() ^ player.blockPosition().asLong() ^ player.serverLevel().getGameTime();
    }

    private static int spawnTrial(ServerPlayer player, long seed, boolean clearBeforePlacement) {
        ServerLevel level = player.serverLevel();
        BlockPos basePos = player.blockPosition().relative(player.getDirection(), 10);
        Optional<ExpeditionInstance> maybeExpedition = ExpeditionGenerator.placeDebugTrial(level, basePos, player.getDirection(), seed, clearBeforePlacement);
        if (maybeExpedition.isEmpty()) {
            player.sendSystemMessage(Component.literal("Expedition trial was not spawned: " + ExpeditionGenerator.lastPlacementFailure())
                    .withStyle(ChatFormatting.RED));
            player.sendSystemMessage(Component.literal("The old Java fallback room is disabled for /spawn_trial so it cannot hide template problems.")
                    .withStyle(ChatFormatting.YELLOW));
            return 0;
        }
        ExpeditionInstance expedition = maybeExpedition.get();
        player.sendSystemMessage(Component.literal("Spawned expedition trial seed " + seed
                + " address " + expedition.address()
                + " at " + formatPos(expedition.basePos())
                + (clearBeforePlacement ? " (cleared first)" : "")).withStyle(ChatFormatting.GREEN));
        return 1;
    }

    private record GateSummary(UUID gateId, String address, net.minecraft.resources.ResourceLocation dimension, BlockPos basePos, net.minecraft.core.Direction facing, String kind, StargateStatus status) {
    }
}
