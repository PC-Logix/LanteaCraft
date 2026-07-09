package com.pclogix.lanteacraft.worldgen;

import com.pclogix.lanteacraft.LanteaCraft;
import com.pclogix.lanteacraft.block.DhdBlock;
import com.pclogix.lanteacraft.block.StargateBaseBlock;
import com.pclogix.lanteacraft.block.StargateComponentBlock;
import com.pclogix.lanteacraft.block.entity.DhdBlockEntity;
import com.pclogix.lanteacraft.block.entity.StargateBaseBlockEntity;
import com.pclogix.lanteacraft.gate.StargateCamouflage;
import com.pclogix.lanteacraft.gate.StargateMultiblock;
import com.pclogix.lanteacraft.gate.StargateNetworkSavedData;
import com.pclogix.lanteacraft.gate.StargateVariant;
import com.pclogix.lanteacraft.registry.ModBlocks;
import com.pclogix.lanteacraft.registry.ModEntities;
import com.pclogix.lanteacraft.registry.ModItems;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BarrelBlock;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.JigsawBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.DecoratedPotBlockEntity;
import net.minecraft.world.level.block.entity.JigsawBlockEntity;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.LevelResource;

public final class ExpeditionGenerator {
    private static final int RIGHT_RADIUS = 12;
    private static final int BACK_RADIUS = 8;
    private static final int FRONT_RADIUS = 28;
    private static final int HEIGHT = 10;
    private static final int TEMPLATE_WIDTH = RIGHT_RADIUS * 2 + 1;
    private static final int TEMPLATE_HEIGHT = HEIGHT + 2;
    private static final int TEMPLATE_GATE_LENGTH = 15;
    private static final int TEMPLATE_HALL_LENGTH = 8;
    private static final int TEMPLATE_ROOM_LENGTH = 12;
    private static final int TEMPLATE_INTERSECTION_LENGTH = 12;
    private static final int TEMPLATE_COMBAT_LENGTH = 12;
    private static final int TEMPLATE_REWARD_LENGTH = 9;
    private static final int TEMPLATE_CLEAR_MARGIN = 20;
    private static final ResourceLocation GATE_ROOM_TEMPLATE = template("gate_room");
    private static final ResourceLocation REWARD_ROOM_TEMPLATE = template("reward_room");
    private static final ResourceLocation GATE_ROOM_RESOURCE = structureResource("gate_room");
    private static final ResourceLocation START_POOL = ResourceLocation.fromNamespaceAndPath(LanteaCraft.MODID, "expedition/start");
    private static final ResourceKey<LootTable> REWARD_LOOT_TABLE = ResourceKey.create(
            Registries.LOOT_TABLE,
            ResourceLocation.fromNamespaceAndPath(LanteaCraft.MODID, "chests/expedition_reward"));
    private static final ResourceLocation DOOR_JIGSAW = ResourceLocation.fromNamespaceAndPath(LanteaCraft.MODID, "expedition/door");
    private static final int JIGSAW_MAX_DEPTH = 48;
    private static final String EMPTY_JIGSAW = "minecraft:empty";
    private static final List<ResourceLocation> INTERSECTION_TEMPLATES = List.of(
            template("intersection_cross"),
            template("intersection_tee")
    );
    private static final Pattern NUMBERED_EXPEDITION_TEMPLATE = Pattern.compile("^structure/expedition/([a-z_]+)_(\\d+)\\.nbt$");
    private static final Pattern NUMBERED_EXPEDITION_TEMPLATE_ID = Pattern.compile("^expedition/([a-z_]+)_(\\d+)$");
    private static String lastPlacementFailure = "";

    private ExpeditionGenerator() {
    }

    public static void placeIfNeeded(ServerLevel level, ExpeditionInstance expedition) {
        if (isAssembledGateAt(level, expedition.basePos())) {
            register(level, expedition, expedition.basePos());
            return;
        }

        place(level, expedition, true);
    }

    public static Optional<ExpeditionInstance> placeDebugTrial(ServerLevel level, BlockPos basePos, Direction facing, long seed, boolean clearBeforePlacement) {
        ExpeditionInstance expedition = new ExpeditionInstance(
                debugAddress(seed),
                (int)seed,
                3,
                basePos.immutable(),
                facing,
                StargateVariant.MILKY_WAY,
                false,
                false,
                "",
                List.of(),
                false,
                seed,
                null,
                Direction.SOUTH);
        return place(level, expedition, false, clearBeforePlacement) ? Optional.of(expedition) : Optional.empty();
    }

    public static String lastPlacementFailure() {
        return lastPlacementFailure.isBlank() ? "unknown placement failure" : lastPlacementFailure;
    }

    public static DebugPlacementCheck checkDebugTrialPlacement(ServerLevel level, BlockPos basePos, Direction facing, long seed, int sampleLimit) {
        ExpeditionInstance expedition = new ExpeditionInstance(
                debugAddress(seed),
                (int)seed,
                3,
                basePos.immutable(),
                facing,
                StargateVariant.MILKY_WAY,
                false,
                false,
                "",
                List.of(),
                false,
                seed,
                null,
                Direction.SOUTH);
        BlockPos checkBasePos = basePos;
        int radius = debugClearRadius(level, expedition);
        int minY = -2;
        int maxY = HEIGHT + 1;
        List<BlockPos> blocked = new ArrayList<>();
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                for (int y = minY; y <= maxY; y++) {
                    BlockPos pos = checkBasePos.offset(x, y, z);
                    if (!level.getBlockState(pos).isAir()) {
                        blocked.add(pos.immutable());
                        if (blocked.size() >= sampleLimit) {
                            return new DebugPlacementCheck(false, checkBasePos, radius, minY, maxY, List.copyOf(blocked));
                        }
                    }
                }
            }
        }
        return new DebugPlacementCheck(true, checkBasePos, radius, minY, maxY, List.copyOf(blocked));
    }

    private static boolean place(ServerLevel level, ExpeditionInstance expedition, boolean allowFallback) {
        return place(level, expedition, allowFallback, true);
    }

    private static boolean place(ServerLevel level, ExpeditionInstance expedition, boolean allowFallback, boolean clearBeforePlacement) {
        clearPendingPlacementState();
        lastPlacementFailure = "";
        BlockPos basePos = expedition.basePos();
        Direction facing = expedition.facing();
        TemplateAnchor anchor = gateRoomAnchor(level);
        BlockPos layoutBasePos = layoutBaseForGate(basePos, facing, anchor);
        if (!placeTemplateArena(level, expedition, basePos, facing, clearBeforePlacement)) {
            if (!allowFallback) {
                return false;
            }
            clearRoom(level, basePos, facing);
            placeShell(level, basePos, facing);
            placeInterior(level, basePos, facing);
            layoutBasePos = basePos;
        }
        TemplatePlacementMarkers markers = TemplatePlacementMarkers.takePending();
        BlockPos gatePos = markers.gatePos().orElse(basePos);
        Direction gateFacing = markers.gateFacing().orElse(facing);
        placeFrame(level, gatePos, gateFacing, expedition.variant());
        markers.dhdPos().ifPresentOrElse(
                dhdPos -> placeDhdAt(level, dhdPos, markers.dhdFacing().orElse(gateFacing), expedition.variant()),
                () -> placeDhd(level, gatePos, gateFacing, expedition.variant()));
        BlockPos rewardFallbackBasePos = layoutBasePos;
        RewardPlacement rewardPlacement = ExpeditionTrialPlacements.takeReward()
                .orElseGet(() -> new RewardPlacement(rewardFallbackBasePos.relative(facing, 22), facing));
        placeRewardChest(level, expedition, rewardPlacement);
        fillSideLoot(level, expedition);
        BlockPos combatFallbackBasePos = layoutBasePos;
        List<BlockPos> combatRoomCenters = ExpeditionSavedData.get(level).findByAddress(expedition.address())
                .map(ExpeditionInstance::combatRoomCenters)
                .filter(centers -> !centers.isEmpty())
                .orElseGet(() -> List.of(combatFallbackBasePos.relative(facing, 16).above()));
        spawnGuards(level, combatRoomCenters, expedition.tier());
        register(level, expedition, gatePos, gateFacing);
        StargateMultiblock.tryAssembleAtBase(level, gatePos);
        applyExpeditionCamouflage(level, gatePos, gateFacing);
        ExpeditionSavedData.get(level).markGenerated(expedition, gatePos, gateFacing);
        return true;
    }

    private static boolean placeTemplateArena(ServerLevel level, ExpeditionInstance expedition, BlockPos basePos, Direction facing, boolean clearBeforePlacement) {
        int clearRadius = TEMPLATE_CLEAR_MARGIN + 128;
        if (clearBeforePlacement) {
            clearTemplateArea(level, basePos, clearRadius);
        }

        ResourceKey<StructureTemplatePool> key = ResourceKey.create(Registries.TEMPLATE_POOL, START_POOL);
        Optional<Holder.Reference<StructureTemplatePool>> maybePool = level.registryAccess()
                .registryOrThrow(Registries.TEMPLATE_POOL)
                .getHolder(key);
        if (maybePool.isEmpty()) {
            return failPlacement("Missing jigsaw template pool: " + START_POOL);
        }

        boolean placed = JigsawPlacement.generateJigsaw(level, maybePool.get(), DOOR_JIGSAW, JIGSAW_MAX_DEPTH, basePos, false);
        if (!placed) {
            return failPlacement("Minecraft jigsaw placement failed for pool " + START_POOL + ".");
        }

        WorldMarkers markers = processWorldMarkers(level, basePos, clearRadius, facing);
        LanteaCraft.LOGGER.info(
                "Expedition jigsaw placed at {}; processed {} combat markers, {} spawn markers, sealed {} unresolved doorway(s), reward door marker present: {}",
                basePos,
                markers.combatRoomCenters().size(),
                markers.spawnPositions().size(),
                markers.sealedDoorways(),
                markers.rewardPlacement().isPresent());
        markers.rewardPlacement().ifPresent(reward -> {
            ExpeditionTrialPlacements.rememberReward(reward.entrance(), reward.facing());
            ExpeditionSavedData.get(level).rememberRewardDoor(expedition.address(), reward.entrance(), reward.facing());
        });
        ExpeditionSavedData.get(level).rememberTrialState(expedition.address(), markers.combatRoomCenters(), false);
        ExpeditionSpawnMarkers.remember(expedition.address(), markers.spawnPositions());
        return true;
    }

    private static WorldMarkers processWorldMarkers(ServerLevel level, BlockPos center, int radius, Direction facing) {
        List<BlockPos> spawnPositions = new ArrayList<>();
        List<BlockPos> combatRoomCenters = new ArrayList<>();
        Optional<RewardPlacement> rewardPlacement = Optional.empty();
        int sealedDoorways = 0;
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                for (int y = -8; y <= HEIGHT + 48; y++) {
                    BlockPos pos = center.offset(x, y, z);
                    BlockState state = level.getBlockState(pos);
                    if (state.is(Blocks.ORANGE_WOOL)) {
                        level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
                    } else if (state.is(Blocks.LIME_WOOL)) {
                        level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
                        BlockPos spawn = pos.above();
                        spawnPositions.add(spawn);
                        combatRoomCenters.add(spawn);
                    } else if (state.is(Blocks.BLUE_GLAZED_TERRACOTTA)) {
                        TemplatePlacementMarkers.rememberGate(pos, facingFromMarker(state));
                        level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
                    } else if (state.is(Blocks.CYAN_GLAZED_TERRACOTTA)) {
                        TemplatePlacementMarkers.rememberDhd(pos, facingFromMarker(state));
                        level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
                    } else if (state.is(ModBlocks.EXPEDITION_REWARD_DOOR_MARKER.get())) {
                        if (rewardPlacement.isEmpty()) {
                            rewardPlacement = Optional.of(new RewardPlacement(pos, facing));
                            lockRewardDoor(level, pos, facing);
                        } else {
                            level.setBlock(pos, ModBlocks.ANCIENT_CONTAINMENT_BLOCK.get().defaultBlockState(), Block.UPDATE_ALL);
                        }
                    } else if (state.is(Blocks.TRAPPED_CHEST)) {
                        ExpeditionLootMarkers.rememberRewardChest(pos, horizontalFacingFromState(state, facing.getOpposite()));
                    } else if (state.is(Blocks.CHEST) || state.is(Blocks.BARREL)) {
                        ExpeditionLootMarkers.rememberSideContainer(pos);
                    } else if (state.is(Blocks.DECORATED_POT)) {
                        ExpeditionLootMarkers.rememberJar(pos);
                    } else if (state.is(Blocks.JIGSAW)) {
                        if (sealUnresolvedDoorway(level, pos, state)) {
                            sealedDoorways++;
                        } else {
                            level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
                        }
                    }
                }
            }
        }
        sealedDoorways += sealExteriorDoorwayOpenings(level, center, radius);
        return new WorldMarkers(List.copyOf(spawnPositions), List.copyOf(combatRoomCenters), rewardPlacement, sealedDoorways);
    }

    private static boolean sealUnresolvedDoorway(ServerLevel level, BlockPos pos, BlockState state) {
        if (!(level.getBlockEntity(pos) instanceof JigsawBlockEntity jigsaw)
                || isEmptyJigsaw(jigsaw.getTarget())
                || isEmptyJigsaw(jigsaw.getPool().location())) {
            return false;
        }

        Direction front = state.getValue(JigsawBlock.ORIENTATION).front();
        if (front.getAxis() == Direction.Axis.Y) {
            return false;
        }

        Direction right = front.getClockWise();
        BlockState seal = ModBlocks.GOAULD_CONTAINMENT_BLOCK.get().defaultBlockState();
        for (int r = -2; r <= 2; r++) {
            for (int y = 0; y <= 4; y++) {
                level.setBlock(pos.relative(right, r).above(y), seal, Block.UPDATE_ALL);
            }
        }
        return true;
    }

    private static boolean isEmptyJigsaw(ResourceLocation location) {
        return location.equals(ResourceLocation.withDefaultNamespace("empty"));
    }

    private static int sealExteriorDoorwayOpenings(ServerLevel level, BlockPos center, int radius) {
        Set<BlockPos> sealedCenters = new HashSet<>();
        int sealed = 0;
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                for (int y = -4; y <= HEIGHT + 48; y++) {
                    BlockPos pos = center.offset(x, y, z);
                    for (Direction direction : Direction.Plane.HORIZONTAL) {
                        Optional<DoorwayOpening> opening = exteriorDoorwayOpening(level, pos, direction);
                        if (sealedCenters.contains(pos) || opening.isEmpty()) {
                            continue;
                        }
                        fillDoorwaySeal(level, pos, direction, opening.get());
                        sealedCenters.add(pos.immutable());
                        sealed++;
                    }
                }
            }
        }
        return sealed;
    }

    private static Optional<DoorwayOpening> exteriorDoorwayOpening(ServerLevel level, BlockPos pos, Direction direction) {
        Optional<DoorwayOpening> opening = doorwayOpening(level, pos, direction);
        if (opening.isEmpty() || !hasDoorwayFrame(level, pos, direction, opening.get())) {
            return Optional.empty();
        }

        int forwardFloor = floorSupport(level, pos, direction, opening.get().width());
        int backwardFloor = floorSupport(level, pos, direction.getOpposite(), opening.get().width());
        int minimumInterior = opening.get().width() * 2;
        if ((forwardFloor <= 1 && backwardFloor >= minimumInterior)
                || (backwardFloor <= 1 && forwardFloor >= minimumInterior)) {
            return opening;
        }
        return Optional.empty();
    }

    private static Optional<DoorwayOpening> doorwayOpening(ServerLevel level, BlockPos pos, Direction direction) {
        if (isAirRectangle(level, pos, direction, 5, 5)) {
            return Optional.of(new DoorwayOpening(5, 5));
        }
        if (isAirRectangle(level, pos, direction, 3, 4)) {
            return Optional.of(new DoorwayOpening(3, 4));
        }
        if (isAirRectangle(level, pos, direction, 3, 5)) {
            return Optional.of(new DoorwayOpening(3, 5));
        }
        return Optional.empty();
    }

    private static boolean isAirRectangle(ServerLevel level, BlockPos pos, Direction direction, int width, int height) {
        Direction right = direction.getClockWise();
        int halfWidth = width / 2;
        for (int r = -halfWidth; r <= halfWidth; r++) {
            for (int y = 0; y < height; y++) {
                if (!level.getBlockState(pos.relative(right, r).above(y)).isAir()) {
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean hasDoorwayFrame(ServerLevel level, BlockPos pos, Direction direction, DoorwayOpening opening) {
        Direction right = direction.getClockWise();
        int halfWidth = opening.width() / 2;
        int floor = 0;
        int header = 0;
        int left = 0;
        int rightSide = 0;
        for (int r = -halfWidth; r <= halfWidth; r++) {
            if (!level.getBlockState(pos.relative(right, r).below()).isAir()) {
                floor++;
            }
            if (!level.getBlockState(pos.relative(right, r).above(opening.height())).isAir()) {
                header++;
            }
        }
        for (int y = 0; y < opening.height(); y++) {
            if (!level.getBlockState(pos.relative(right, -halfWidth - 1).above(y)).isAir()) {
                left++;
            }
            if (!level.getBlockState(pos.relative(right, halfWidth + 1).above(y)).isAir()) {
                rightSide++;
            }
        }
        return floor >= halfWidth + 1 && header >= halfWidth + 1 && left >= opening.height() - 1 && rightSide >= opening.height() - 1;
    }

    private static int floorSupport(ServerLevel level, BlockPos pos, Direction direction, int width) {
        Direction right = direction.getClockWise();
        int halfWidth = width / 2;
        int support = 0;
        for (int distance = 1; distance <= 3; distance++) {
            BlockPos rowCenter = pos.relative(direction, distance).below();
            for (int r = -halfWidth; r <= halfWidth; r++) {
                if (!level.getBlockState(rowCenter.relative(right, r)).isAir()) {
                    support++;
                }
            }
        }
        return support;
    }

    private static void fillDoorwaySeal(ServerLevel level, BlockPos pos, Direction direction, DoorwayOpening opening) {
        Direction right = direction.getClockWise();
        int halfWidth = opening.width() / 2;
        BlockState seal = ModBlocks.GOAULD_CONTAINMENT_BLOCK.get().defaultBlockState();
        for (int r = -halfWidth; r <= halfWidth; r++) {
            for (int y = 0; y < opening.height(); y++) {
                level.setBlock(pos.relative(right, r).above(y), seal, Block.UPDATE_ALL);
            }
        }
    }

    private static boolean failPlacement(String message) {
        lastPlacementFailure = message;
        LanteaCraft.LOGGER.warn("Expedition placement failed: {}", message);
        return false;
    }

    private static int debugClearRadius(ServerLevel level, ExpeditionInstance expedition) {
        ExpeditionTemplates templates = discoverTemplates(level);
        if (!hasTemplate(level, GATE_ROOM_TEMPLATE) || !hasTemplate(level, REWARD_ROOM_TEMPLATE) || templates.combatRooms().isEmpty()) {
            return Math.max(RIGHT_RADIUS + 1, FRONT_RADIUS + 1);
        }
        return clearRadiusForModules(buildModules(level, expedition, templates));
    }

    private static int clearRadiusForModules(List<ExpeditionModule> modules) {
        return modules.stream().mapToInt(ExpeditionModule::length).sum() + TEMPLATE_CLEAR_MARGIN;
    }

    private static List<ExpeditionModule> buildModules(ServerLevel level, ExpeditionInstance expedition, ExpeditionTemplates templates) {
        List<ExpeditionModule> modules = new ArrayList<>();
        modules.add(new ExpeditionModule(GATE_ROOM_TEMPLATE, TEMPLATE_GATE_LENGTH, false, false, Turn.STRAIGHT));

        RandomSource random = RandomSource.create(moduleSeed(level, expedition));
        int combatRooms = Mth.clamp(expedition.tier() + 1 + random.nextInt(2), 2, 5);
        for (int i = 0; i < combatRooms; i++) {
            List<ExpeditionModule> hallModules = hallModules(templates);
            int halls = hallModules.isEmpty() ? 0 : 1 + random.nextInt(2);
            for (int hall = 0; hall < halls; hall++) {
                selectModule(hallModules, random).ifPresent(modules::add);
            }

            if (i > 0 && !templates.rooms().isEmpty() && random.nextFloat() < 0.65F) {
                int fillerRooms = 1 + random.nextInt(expedition.tier() >= 3 ? 2 : 1);
                for (int room = 0; room < fillerRooms; room++) {
                    selectTemplate(templates.rooms(), random).ifPresent(template ->
                            modules.add(new ExpeditionModule(template, TEMPLATE_ROOM_LENGTH, false, false, Turn.STRAIGHT)));
                }
            }

            if (i > 0 && !templates.intersections().isEmpty() && random.nextFloat() < 0.35F) {
                selectTemplate(templates.intersections(), random).ifPresent(template ->
                        modules.add(new ExpeditionModule(template, TEMPLATE_INTERSECTION_LENGTH, false, false, randomTurn(random))));
            }

            ResourceLocation combatTemplate = selectTemplate(templates.combatRooms(), random).orElseThrow();
            modules.add(new ExpeditionModule(combatTemplate, TEMPLATE_COMBAT_LENGTH, true, false, Turn.STRAIGHT));
        }

        List<ExpeditionModule> hallModules = hallModules(templates);
        if (!hallModules.isEmpty()) {
            int finalHalls = 1 + random.nextInt(2);
            for (int i = 0; i < finalHalls; i++) {
                selectModule(hallModules, random).ifPresent(modules::add);
            }
        }
        modules.add(new ExpeditionModule(REWARD_ROOM_TEMPLATE, TEMPLATE_REWARD_LENGTH, false, true, Turn.STRAIGHT));
        return List.copyOf(modules);
    }

    private static List<ExpeditionModule> hallModules(ExpeditionTemplates templates) {
        List<ExpeditionModule> modules = new ArrayList<>();
        templates.halls().forEach(template -> modules.add(new ExpeditionModule(template, TEMPLATE_HALL_LENGTH, false, false, Turn.STRAIGHT)));
        templates.leftHalls().forEach(template -> modules.add(new ExpeditionModule(template, TEMPLATE_HALL_LENGTH, false, false, Turn.LEFT)));
        templates.rightHalls().forEach(template -> modules.add(new ExpeditionModule(template, TEMPLATE_HALL_LENGTH, false, false, Turn.RIGHT)));
        return List.copyOf(modules);
    }

    private static Optional<ExpeditionModule> selectModule(List<ExpeditionModule> modules, RandomSource random) {
        if (modules.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(modules.get(random.nextInt(modules.size())));
    }

    private static Turn randomTurn(RandomSource random) {
        return switch (random.nextInt(3)) {
            case 1 -> Turn.LEFT;
            case 2 -> Turn.RIGHT;
            default -> Turn.STRAIGHT;
        };
    }

    private static void lockRewardDoor(ServerLevel level, BlockPos basePos, Direction facing) {
        Direction right = facing.getClockWise();
        for (int r = -2; r <= 2; r++) {
            for (int y = 0; y <= 4; y++) {
                level.setBlock(basePos.relative(right, r).above(y), ModBlocks.ANCIENT_CONTAINMENT_BLOCK.get().defaultBlockState(), Block.UPDATE_ALL);
            }
        }
    }

    public static void unlockRewardDoor(ServerLevel level, ExpeditionInstance expedition) {
        RewardPlacement reward = expedition.rewardDoorPos() == null
                ? rewardPlacement(level, expedition)
                : new RewardPlacement(expedition.rewardDoorPos(), expedition.rewardDoorFacing());
        Direction right = reward.facing().getClockWise();
        BlockPos basePos = reward.entrance();
        for (int r = -2; r <= 2; r++) {
            for (int y = 0; y <= 4; y++) {
                level.setBlock(basePos.relative(right, r).above(y), Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
            }
        }
    }

    private static void clearTemplateArea(ServerLevel level, BlockPos basePos, int radius) {
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                for (int y = -2; y <= HEIGHT + 1; y++) {
                    level.setBlock(basePos.offset(x, y, z), Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
                }
            }
        }
    }

    // Expedition templates are authored facing south. Orange wool on template
    // edges defines sockets that connect pieces together.
    private static void placeTemplate(ServerLevel level, ResourceLocation templateId, BlockPos origin, Direction facing, int salt) {
        StructureTemplate template = level.getStructureManager().get(templateId).orElseThrow();
        StructurePlaceSettings settings = placeSettings(facing);
        template.placeInWorld(level, origin, origin, settings, RandomSource.create(level.getSeed() ^ origin.asLong() ^ salt), Block.UPDATE_ALL);
    }

    private static TemplateMarkers processTemplateMarkers(ServerLevel level, TemplateInfo info, BlockPos origin, Direction facing) {
        List<BlockPos> spawnPositions = new ArrayList<>();
        Optional<RewardPlacement> rewardPlacement = Optional.empty();
        for (int x = 0; x < info.size().getX(); x++) {
            for (int y = 0; y < info.size().getY(); y++) {
                for (int z = 0; z < info.size().getZ(); z++) {
                    BlockPos pos = transformLocal(origin, facing, new BlockPos(x, y, z));
                    BlockState state = level.getBlockState(pos);
                    if (state.is(Blocks.ORANGE_WOOL)) {
                        level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
                    } else if (state.is(Blocks.LIME_WOOL)) {
                        level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
                        spawnPositions.add(pos.above());
                    } else if (state.is(Blocks.BLUE_GLAZED_TERRACOTTA)) {
                        TemplatePlacementMarkers.rememberGate(pos, facingFromMarker(state));
                        level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
                    } else if (state.is(Blocks.CYAN_GLAZED_TERRACOTTA)) {
                        TemplatePlacementMarkers.rememberDhd(pos, facingFromMarker(state));
                        level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
                    } else if (state.is(Blocks.RED_GLAZED_TERRACOTTA)) {
                        Direction markerFacing = facingFromMarker(state);
                        level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
                        rewardPlacement = Optional.of(new RewardPlacement(pos, markerFacing));
                    } else if (state.is(Blocks.TRAPPED_CHEST)) {
                        ExpeditionLootMarkers.rememberRewardChest(pos, horizontalFacingFromState(state, facing.getOpposite()));
                    } else if (state.is(Blocks.CHEST) || state.is(Blocks.BARREL)) {
                        ExpeditionLootMarkers.rememberSideContainer(pos);
                    } else if (state.is(Blocks.DECORATED_POT)) {
                        ExpeditionLootMarkers.rememberJar(pos);
                    } else if (state.is(Blocks.JIGSAW)) {
                        level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
                    }
                }
            }
        }
        return new TemplateMarkers(List.copyOf(spawnPositions), rewardPlacement);
    }

    private static Optional<TemplateInfo> templateInfo(ServerLevel level, ResourceLocation templateId) {
        return structureTag(level, templateId).flatMap(tag -> templateInfo(templateId, tag));
    }

    private static Optional<TemplateInfo> templateInfo(ResourceLocation templateId, CompoundTag tag) {
        ListTag sizeTag = tag.getList("size", Tag.TAG_INT);
        if (sizeTag.size() < 3) {
            return Optional.empty();
        }
        BlockPos size = new BlockPos(sizeTag.getInt(0), sizeTag.getInt(1), sizeTag.getInt(2));
        ListTag palette = tag.getList("palette", Tag.TAG_COMPOUND);
        List<CompoundTag> states = new ArrayList<>();
        List<String> stateNames = new ArrayList<>();
        for (int i = 0; i < palette.size(); i++) {
            CompoundTag state = palette.getCompound(i);
            states.add(state);
            stateNames.add(state.getString("Name"));
        }

        List<Connector> connectors = new ArrayList<>();
        Optional<BlockPos> gateAnchor = Optional.empty();
        ListTag blocks = tag.getList("blocks", Tag.TAG_COMPOUND);
        for (int i = 0; i < blocks.size(); i++) {
            CompoundTag block = blocks.getCompound(i);
            int state = block.getInt("state");
            if (state < 0 || state >= stateNames.size()) {
                continue;
            }
            ListTag posTag = block.getList("pos", Tag.TAG_INT);
            if (posTag.size() < 3) {
                continue;
            }
            BlockPos pos = new BlockPos(posTag.getInt(0), posTag.getInt(1), posTag.getInt(2));
            String name = stateNames.get(state);
            if (name.equals("minecraft:orange_wool")) {
                connectors.add(new Connector(pos, inferConnectorDirection(size, pos), EMPTY_JIGSAW, EMPTY_JIGSAW, EMPTY_JIGSAW));
            } else if (name.equals("minecraft:jigsaw")) {
                Direction direction = directionFromJigsawState(states.get(state), size, pos);
                if (direction.getAxis().isHorizontal()) {
                    CompoundTag nbt = block.contains("nbt", Tag.TAG_COMPOUND) ? block.getCompound("nbt") : new CompoundTag();
                    connectors.add(new Connector(
                            pos,
                            direction,
                            normalizeJigsawId(nbt.getString("name")),
                            normalizeJigsawId(nbt.getString("target")),
                            normalizeJigsawId(nbt.getString("pool"))));
                }
            } else if (name.equals("minecraft:blue_glazed_terracotta") && gateAnchor.isEmpty()) {
                gateAnchor = Optional.of(pos);
            }
        }
        if (connectors.isEmpty()) {
            LanteaCraft.LOGGER.warn("Expedition template {} has no expedition connectors.", templateId);
        }
        return Optional.of(new TemplateInfo(templateId, size, List.copyOf(connectors), gateAnchor));
    }

    private static Direction directionFromJigsawState(CompoundTag state, BlockPos size, BlockPos pos) {
        if (state.contains("Properties", Tag.TAG_COMPOUND)) {
            CompoundTag properties = state.getCompound("Properties");
            String orientation = properties.getString("orientation");
            int separator = orientation.indexOf('_');
            String frontName = separator >= 0 ? orientation.substring(0, separator) : orientation;
            Direction front = Direction.byName(frontName);
            if (front != null) {
                return front;
            }
        }
        return inferConnectorDirection(size, pos);
    }

    private static String normalizeJigsawId(String value) {
        return value == null || value.isBlank() ? EMPTY_JIGSAW : value;
    }

    private static Optional<CompoundTag> structureTag(ServerLevel level, ResourceLocation templateId) {
        Optional<CompoundTag> generated = generatedStructureTag(level, templateId);
        if (generated.isPresent()) {
            return generated;
        }

        ResourceLocation resourceId = ResourceLocation.fromNamespaceAndPath(templateId.getNamespace(), "structure/" + templateId.getPath() + ".nbt");
        Optional<Resource> resource = level.getServer().getResourceManager().getResource(resourceId);
        if (resource.isEmpty()) {
            return Optional.empty();
        }
        try (InputStream input = resource.get().open()) {
            return Optional.of(NbtIo.readCompressed(input, NbtAccounter.unlimitedHeap()));
        } catch (IOException | RuntimeException ex) {
            LanteaCraft.LOGGER.warn("Could not read expedition template metadata from {}.", resourceId, ex);
            return Optional.empty();
        }
    }

    private static Direction inferConnectorDirection(BlockPos size, BlockPos pos) {
        int west = pos.getX();
        int east = Math.max(0, size.getX() - 1 - pos.getX());
        int north = pos.getZ();
        int south = Math.max(0, size.getZ() - 1 - pos.getZ());
        int min = Math.min(Math.min(west, east), Math.min(north, south));
        if (min == south) {
            return Direction.SOUTH;
        }
        if (min == north) {
            return Direction.NORTH;
        }
        if (min == east) {
            return Direction.EAST;
        }
        return Direction.WEST;
    }

    private static Optional<Connector> chooseEntrance(TemplateInfo info, Direction worldDirection) {
        List<Connector> connectors = info.connectors().stream()
                .filter(connector -> facingForConnector(connector.direction(), worldDirection).isPresent())
                .toList();
        Optional<Connector> northEntrance = connectors.stream()
                .filter(connector -> connector.direction() == Direction.NORTH)
                .findAny();
        return northEntrance.or(() -> connectors.stream().findAny());
    }

    private static Optional<PlacedConnector> chooseExit(TemplateInfo info, BlockPos origin, Direction facing, Optional<Connector> entrance, List<IntBox> placedBoxes, RandomSource random) {
        List<Connector> exits = info.connectors().stream()
                .filter(connector -> entrance.isEmpty() || !connector.pos().equals(entrance.get().pos()))
                .filter(connector -> entrance.isEmpty() || connector.direction() != entrance.get().direction())
                .filter(Connector::isOutgoing)
                .filter(connector -> rotationFor(facing).rotate(connector.direction()).getAxis().isHorizontal())
                .filter(connector -> !containsAny(transformLocal(origin, facing, connector.pos()).relative(rotationFor(facing).rotate(connector.direction())), placedBoxes))
                .toList();
        if (exits.isEmpty()) {
            return Optional.empty();
        }
        List<Connector> preferred = exits.stream()
                .filter(connector -> connector.direction() == Direction.SOUTH)
                .toList();
        List<Connector> choices = preferred.isEmpty() ? exits : preferred;
        Connector exit = choices.get(random.nextInt(choices.size()));
        return Optional.of(new PlacedConnector(transformLocal(origin, facing, exit.pos()), rotationFor(facing).rotate(exit.direction()), exit.target(), exit.pool()));
    }

    private static Optional<PlacedConnector> chooseMainExit(TemplateInfo info, BlockPos origin, Direction facing, Optional<Connector> entrance, RandomSource random, boolean allowReward) {
        List<Connector> exits = info.connectors().stream()
                .filter(connector -> entrance.isEmpty() || !connector.pos().equals(entrance.get().pos()))
                .filter(connector -> entrance.isEmpty() || connector.direction() != entrance.get().direction())
                .filter(Connector::isOutgoing)
                .filter(connector -> !isCombatPool(connector.pool()))
                .filter(connector -> allowReward || !isRewardTarget(connector.target(), connector.pool()))
                .toList();
        if (exits.isEmpty() && allowReward) {
            exits = info.connectors().stream()
                    .filter(connector -> entrance.isEmpty() || !connector.pos().equals(entrance.get().pos()))
                    .filter(connector -> entrance.isEmpty() || connector.direction() != entrance.get().direction())
                    .filter(Connector::isOutgoing)
                    .filter(connector -> !isCombatPool(connector.pool()))
                    .toList();
        }
        if (exits.isEmpty()) {
            return Optional.empty();
        }
        List<Connector> rewardExits = exits.stream()
                .filter(connector -> isRewardTarget(connector.target(), connector.pool()))
                .toList();
        List<Connector> choices = allowReward && !rewardExits.isEmpty() ? rewardExits : exits;
        Connector exit = choices.get(random.nextInt(choices.size()));
        return Optional.of(new PlacedConnector(transformLocal(origin, facing, exit.pos()), rotationFor(facing).rotate(exit.direction()), exit.target(), exit.pool()));
    }

    private static boolean placeCombatBranches(ServerLevel level, TemplateInfo info, BlockPos origin, Direction facing, Optional<Connector> entrance, List<IntBox> placedBoxes, List<BlockPos> combatRoomCenters, List<BlockPos> combatSpawnMarkers, RandomSource random, int salt) {
        List<Connector> branches = info.connectors().stream()
                .filter(connector -> entrance.isEmpty() || !connector.pos().equals(entrance.get().pos()))
                .filter(connector -> entrance.isEmpty() || connector.direction() != entrance.get().direction())
                .filter(Connector::isOutgoing)
                .filter(connector -> isCombatPool(connector.pool()) || connector.target().contains("combat"))
                .toList();
        List<PlacedPiece> plannedBranches = new ArrayList<>();
        List<IntBox> plannedBoxes = new ArrayList<>(placedBoxes);
        for (Connector branch : branches) {
            PlacedConnector connector = new PlacedConnector(transformLocal(origin, facing, branch.pos()), rotationFor(facing).rotate(branch.direction()), branch.target(), branch.pool());
            Optional<PlacedPiece> piece = chooseJigsawPiece(level, connector, origin.getY(), plannedBoxes, random, true);
            if (piece.isEmpty()) {
                return false;
            }
            plannedBranches.add(piece.get());
            plannedBoxes.add(piece.get().footprint());
        }
        for (PlacedPiece placed : plannedBranches) {
            placeTemplate(level, placed.info().id(), placed.origin(), placed.facing(), salt);
            placedBoxes.add(placed.footprint());
            TemplateMarkers markers = processTemplateMarkers(level, placed.info(), placed.origin(), placed.facing());
            combatRoomCenters.add(templateCenter(placed.info(), placed.origin(), placed.facing()));
            combatSpawnMarkers.addAll(markers.spawnPositions());
        }
        return true;
    }

    private static Optional<PlacedPiece> chooseJigsawPiece(ServerLevel level, PlacedConnector previousExit, int originY, List<IntBox> placedBoxes, RandomSource random, boolean allowReward) {
        List<ResourceLocation> templates = new ArrayList<>(poolTemplates(level, previousExit.pool()));
        Collections.shuffle(templates, new java.util.Random(random.nextLong()));
        for (ResourceLocation template : templates) {
            if (!allowReward && isDeadEndBeforeCombatTemplate(template)) {
                continue;
            }
            Optional<TemplateInfo> maybeInfo = templateInfo(level, template);
            if (maybeInfo.isEmpty()) {
                continue;
            }
            if (isIntersectionTemplate(template) && !intersectionRoutesThroughHalls(maybeInfo.get(), previousExit.target())) {
                continue;
            }
            Optional<TemplatePlacement> placement = choosePlacement(maybeInfo.get(), previousExit, originY, placedBoxes);
            if (placement.isPresent()) {
                TemplatePlacement selected = placement.get();
                return Optional.of(new PlacedPiece(maybeInfo.get(), selected.origin(), selected.facing(), selected.entrance().orElseThrow(), selected.footprint()));
            }
        }
        return Optional.empty();
    }

    private static List<ResourceLocation> poolTemplates(ServerLevel level, String poolId) {
        if (poolId.equals(EMPTY_JIGSAW) || poolId.equals("minecraft:empty")) {
            return List.of();
        }
        ResourceLocation pool = parseResourceLocation(poolId);
        ResourceLocation resourceId = ResourceLocation.fromNamespaceAndPath(pool.getNamespace(), "worldgen/template_pool/" + pool.getPath() + ".json");
        Optional<Resource> resource = level.getServer().getResourceManager().getResource(resourceId);
        if (resource.isEmpty()) {
            return List.of();
        }
        try (InputStreamReader reader = new InputStreamReader(resource.get().open(), StandardCharsets.UTF_8)) {
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
            JsonArray elements = root.has("elements") && root.get("elements").isJsonArray() ? root.getAsJsonArray("elements") : new JsonArray();
            List<ResourceLocation> templates = new ArrayList<>();
            for (JsonElement elementEntry : elements) {
                JsonObject entry = elementEntry.getAsJsonObject();
                int weight = entry.has("weight") ? Math.max(1, entry.get("weight").getAsInt()) : 1;
                JsonObject element = entry.has("element") && entry.get("element").isJsonObject() ? entry.getAsJsonObject("element") : new JsonObject();
                if (!element.has("location")) {
                    continue;
                }
                ResourceLocation location = parseResourceLocation(element.get("location").getAsString());
                for (int i = 0; i < weight; i++) {
                    templates.add(location);
                }
            }
            return List.copyOf(templates);
        } catch (IOException | RuntimeException ex) {
            LanteaCraft.LOGGER.warn("Could not read expedition template pool {}.", resourceId, ex);
            return List.of();
        }
    }

    private static ResourceLocation parseResourceLocation(String id) {
        int separator = id.indexOf(':');
        if (separator >= 0) {
            return ResourceLocation.fromNamespaceAndPath(id.substring(0, separator), id.substring(separator + 1));
        }
        return ResourceLocation.fromNamespaceAndPath("minecraft", id);
    }

    private static boolean isCombatPool(String pool) {
        return pool.contains("combat");
    }

    private static boolean isRewardTarget(String target, String pool) {
        return target.contains("reward") || pool.contains("reward");
    }

    private static boolean isCombatTemplate(ResourceLocation id) {
        return id.getPath().contains("combat");
    }

    private static boolean isRewardTemplate(ResourceLocation id) {
        return id.getPath().contains("reward");
    }

    private static boolean isIntersectionTemplate(ResourceLocation id) {
        return id.getPath().contains("intersection");
    }

    private static boolean intersectionRoutesThroughHalls(TemplateInfo info, String entranceName) {
        return info.connectors().stream()
                .filter(Connector::isOutgoing)
                .filter(connector -> !connector.name().equals(entranceName))
                .allMatch(connector -> connector.pool().endsWith("/halls") && connector.target().endsWith("/hall_in"));
    }

    private static boolean isDeadEndBeforeCombatTemplate(ResourceLocation id) {
        String path = id.getPath();
        return path.contains("intersection") || path.contains("reward") || path.contains("final_corridor");
    }

    private static Optional<TemplatePlacement> choosePlacement(TemplateInfo info, PlacedConnector previousExit, int originY, List<IntBox> placedBoxes) {
        Direction requiredEntranceDirection = previousExit.direction().getOpposite();
        BlockPos target = previousExit.pos().relative(previousExit.direction());
        List<Connector> entrances = info.connectors().stream()
                .filter(connector -> connector.name().equals(previousExit.target()))
                .filter(connector -> facingForConnector(connector.direction(), requiredEntranceDirection).isPresent())
                .sorted(Comparator.comparingInt(connector -> connector.direction() == Direction.NORTH ? 0 : 1))
                .toList();
        for (Connector entrance : entrances) {
            Optional<Direction> maybeFacing = facingForConnector(entrance.direction(), requiredEntranceDirection);
            if (maybeFacing.isEmpty()) {
                continue;
            }
            Direction facing = maybeFacing.get();
            BlockPos origin = originForLocalAtY(target, entrance.pos(), facing, originY);
            IntBox footprint = footprint(origin, facing, info.size());
            if (!overlapsAny(footprint, placedBoxes)) {
                return Optional.of(new TemplatePlacement(origin, facing, Optional.of(entrance), footprint));
            }
        }
        return Optional.empty();
    }

    private static Optional<Direction> facingForConnector(Direction localDirection, Direction worldDirection) {
        for (Direction facing : Direction.Plane.HORIZONTAL) {
            if (rotationFor(facing).rotate(localDirection) == worldDirection) {
                return Optional.of(facing);
            }
        }
        return Optional.empty();
    }

    private static BlockPos originForLocal(BlockPos targetWorldPos, BlockPos localPos, Direction facing) {
        BlockPos relative = StructureTemplate.calculateRelativePosition(placeSettings(facing), localPos);
        return new BlockPos(
                targetWorldPos.getX() - relative.getX(),
                targetWorldPos.getY() - relative.getY(),
                targetWorldPos.getZ() - relative.getZ());
    }

    private static BlockPos originForLocalAtY(BlockPos targetWorldPos, BlockPos localPos, Direction facing, int originY) {
        BlockPos relative = StructureTemplate.calculateRelativePosition(placeSettings(facing), localPos);
        return new BlockPos(
                targetWorldPos.getX() - relative.getX(),
                originY,
                targetWorldPos.getZ() - relative.getZ());
    }

    private static BlockPos transformLocal(BlockPos origin, Direction facing, BlockPos localPos) {
        return origin.offset(StructureTemplate.calculateRelativePosition(placeSettings(facing), localPos));
    }

    private static IntBox footprint(BlockPos origin, Direction facing, BlockPos size) {
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int minZ = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;
        int maxZ = Integer.MIN_VALUE;
        for (int x : new int[] {0, Math.max(0, size.getX() - 1)}) {
            for (int y : new int[] {0, Math.max(0, size.getY() - 1)}) {
                for (int z : new int[] {0, Math.max(0, size.getZ() - 1)}) {
                    BlockPos pos = transformLocal(origin, facing, new BlockPos(x, y, z));
                    minX = Math.min(minX, pos.getX());
                    minY = Math.min(minY, pos.getY());
                    minZ = Math.min(minZ, pos.getZ());
                    maxX = Math.max(maxX, pos.getX());
                    maxY = Math.max(maxY, pos.getY());
                    maxZ = Math.max(maxZ, pos.getZ());
                }
            }
        }
        return new IntBox(minX, minY, minZ, maxX, maxY, maxZ);
    }

    private static boolean overlapsAny(IntBox box, List<IntBox> boxes) {
        return boxes.stream().anyMatch(box::overlaps);
    }

    private static boolean containsAny(BlockPos pos, List<IntBox> boxes) {
        return boxes.stream().anyMatch(box -> box.contains(pos));
    }

    private static BlockPos templateCenter(TemplateInfo info, BlockPos origin, Direction facing) {
        return transformLocal(origin, facing, new BlockPos(info.size().getX() / 2, 1, info.size().getZ() / 2)).above();
    }

    private static StructurePlaceSettings placeSettings(Direction facing) {
        return new StructurePlaceSettings()
                .setRotation(rotationFor(facing))
                .setRotationPivot(BlockPos.ZERO)
                .setIgnoreEntities(false)
                .setKnownShape(true);
    }

    private static RandomSource randomForConnector(ServerLevel level, ExpeditionInstance expedition, int salt) {
        return RandomSource.create(moduleSeed(level, expedition) ^ ((long)salt * 0x9E3779B97F4A7C15L));
    }

    private static void clearPendingPlacementState() {
        TemplatePlacementMarkers.takePending();
        ExpeditionTrialPlacements.takeReward();
        ExpeditionSpawnMarkers.takePending();
        ExpeditionLootMarkers.takeRewardChests();
        ExpeditionLootMarkers.takeSideContainers();
        ExpeditionLootMarkers.takeJars();
    }

    private static Direction facingFromMarker(BlockState state) {
        return state.hasProperty(HorizontalDirectionalBlock.FACING) ? state.getValue(HorizontalDirectionalBlock.FACING) : Direction.SOUTH;
    }

    private static Direction horizontalFacingFromState(BlockState state, Direction fallback) {
        if (state.hasProperty(ChestBlock.FACING)) {
            return state.getValue(ChestBlock.FACING);
        }
        if (state.hasProperty(BarrelBlock.FACING)) {
            Direction facing = state.getValue(BarrelBlock.FACING);
            return facing.getAxis().isHorizontal() ? facing : fallback;
        }
        if (state.hasProperty(HorizontalDirectionalBlock.FACING)) {
            return state.getValue(HorizontalDirectionalBlock.FACING);
        }
        return fallback;
    }

    private static Optional<ResourceLocation> selectTemplate(List<ResourceLocation> templates, RandomSource random) {
        if (templates.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(templates.get(random.nextInt(templates.size())));
    }

    private static boolean hasTemplate(ServerLevel level, ResourceLocation template) {
        return level.getStructureManager().get(template).isPresent();
    }

    private static ExpeditionTemplates discoverTemplates(ServerLevel level) {
        List<ResourceLocation> halls = new ArrayList<>();
        List<ResourceLocation> leftHalls = new ArrayList<>();
        List<ResourceLocation> rightHalls = new ArrayList<>();
        List<ResourceLocation> rooms = new ArrayList<>();
        List<ResourceLocation> combatRooms = new ArrayList<>();

        level.getStructureManager()
                .listTemplates()
                .filter(id -> id.getNamespace().equals(LanteaCraft.MODID) && id.getPath().startsWith("expedition/"))
                .forEach(id -> collectNumberedTemplateId(id, halls, leftHalls, rightHalls, rooms, combatRooms));

        level.getServer().getResourceManager()
                .listResources("structure/expedition", id -> id.getNamespace().equals(LanteaCraft.MODID) && id.getPath().endsWith(".nbt"))
                .keySet()
                .forEach(id -> collectNumberedTemplate(id, halls, leftHalls, rightHalls, rooms, combatRooms));

        List<ResourceLocation> intersections = INTERSECTION_TEMPLATES.stream()
                .filter(template -> hasTemplate(level, template))
                .toList();

        return new ExpeditionTemplates(sortTemplates(halls), sortTemplates(leftHalls), sortTemplates(rightHalls), sortTemplates(rooms), intersections, sortTemplates(combatRooms));
    }

    private static void collectNumberedTemplateId(ResourceLocation templateId, List<ResourceLocation> halls, List<ResourceLocation> leftHalls, List<ResourceLocation> rightHalls, List<ResourceLocation> rooms, List<ResourceLocation> combatRooms) {
        Matcher matcher = NUMBERED_EXPEDITION_TEMPLATE_ID.matcher(templateId.getPath());
        if (!matcher.matches()) {
            return;
        }
        collectNumberedTemplate(matcher.group(1), templateId, halls, leftHalls, rightHalls, rooms, combatRooms);
    }

    private static void collectNumberedTemplate(ResourceLocation resourceId, List<ResourceLocation> halls, List<ResourceLocation> leftHalls, List<ResourceLocation> rightHalls, List<ResourceLocation> rooms, List<ResourceLocation> combatRooms) {
        Matcher matcher = NUMBERED_EXPEDITION_TEMPLATE.matcher(resourceId.getPath());
        if (!matcher.matches()) {
            return;
        }

        ResourceLocation templateId = template(matcher.group(1) + "_" + matcher.group(2));
        collectNumberedTemplate(matcher.group(1), templateId, halls, leftHalls, rightHalls, rooms, combatRooms);
    }

    private static void collectNumberedTemplate(String kind, ResourceLocation templateId, List<ResourceLocation> halls, List<ResourceLocation> leftHalls, List<ResourceLocation> rightHalls, List<ResourceLocation> rooms, List<ResourceLocation> combatRooms) {
        switch (kind) {
            case "hall_left" -> leftHalls.add(templateId);
            case "hall_right" -> rightHalls.add(templateId);
            case "combat_room" -> combatRooms.add(templateId);
            default -> {
                if (kind.startsWith("hall")) {
                    halls.add(templateId);
                } else if (kind.startsWith("room")) {
                    rooms.add(templateId);
                }
            }
        }
    }

    private static List<ResourceLocation> sortTemplates(List<ResourceLocation> templates) {
        return templates.stream()
                .distinct()
                .sorted(Comparator.comparing(ResourceLocation::toString))
                .toList();
    }

    private static long moduleSeed(ServerLevel level, ExpeditionInstance expedition) {
        long seed = expedition.layoutSeed();
        seed ^= (long)expedition.tier() * 0xBF58476D1CE4E5B9L;
        return seed;
    }

    private static String debugAddress(long seed) {
        long state = seed ^ 0x544C414E544541L;
        StringBuilder address = new StringBuilder(com.pclogix.lanteacraft.gate.StargateAddress.ADDRESS_LENGTH);
        for (int i = 0; i < com.pclogix.lanteacraft.gate.StargateAddress.GATE_ID_LENGTH; i++) {
            state ^= state >>> 33;
            state *= 0xff51afd7ed558ccdL;
            state ^= state >>> 33;
            address.append(com.pclogix.lanteacraft.gate.StargateAddress.LEGACY_GLYPHS.charAt(Math.floorMod(state, com.pclogix.lanteacraft.gate.StargateAddress.LEGACY_GLYPHS.length())));
            state += i + 1;
        }
        address.append('X');
        return address.toString();
    }

    private static ResourceLocation template(String name) {
        return ResourceLocation.fromNamespaceAndPath(LanteaCraft.MODID, "expedition/" + name);
    }

    private static ResourceLocation structureResource(String name) {
        return ResourceLocation.fromNamespaceAndPath(LanteaCraft.MODID, "structure/expedition/" + name + ".nbt");
    }

    private static TemplateAnchor gateRoomAnchor(ServerLevel level) {
        Optional<TemplateAnchor> generatedAnchor = generatedStructureTag(level, GATE_ROOM_TEMPLATE)
                .flatMap(ExpeditionGenerator::findGateAnchor);
        if (generatedAnchor.isPresent()) {
            return generatedAnchor.get();
        }

        Optional<Resource> resource = level.getServer().getResourceManager().getResource(GATE_ROOM_RESOURCE);
        if (resource.isEmpty()) {
            return TemplateAnchor.DEFAULT;
        }

        try (InputStream input = resource.get().open()) {
            CompoundTag tag = NbtIo.readCompressed(input, NbtAccounter.unlimitedHeap());
            return findGateAnchor(tag).orElse(TemplateAnchor.DEFAULT);
        } catch (IOException | RuntimeException ex) {
            LanteaCraft.LOGGER.warn("Could not read expedition gate room anchor from {}; using default anchor.", GATE_ROOM_RESOURCE, ex);
            return TemplateAnchor.DEFAULT;
        }
    }

    private static Optional<CompoundTag> generatedStructureTag(ServerLevel level, ResourceLocation templateId) {
        Path path = generatedStructurePath(level, templateId);
        if (!Files.exists(path)) {
            return Optional.empty();
        }
        try {
            return Optional.of(NbtIo.readCompressed(path, NbtAccounter.unlimitedHeap()));
        } catch (IOException | RuntimeException ex) {
            LanteaCraft.LOGGER.warn("Could not read generated expedition structure {}; using resource fallback.", path, ex);
            return Optional.empty();
        }
    }

    private static Path generatedStructurePath(ServerLevel level, ResourceLocation templateId) {
        Path path = level.getServer()
                .getWorldPath(LevelResource.GENERATED_DIR)
                .resolve(templateId.getNamespace())
                .resolve("structures");
        for (String segment : templateId.getPath().split("/")) {
            path = path.resolve(segment);
        }
        return path.resolveSibling(path.getFileName() + ".nbt");
    }

    private static Optional<TemplateAnchor> findGateAnchor(CompoundTag tag) {
        ListTag palette = tag.getList("palette", Tag.TAG_COMPOUND);
        int gateState = -1;
        for (int i = 0; i < palette.size(); i++) {
            CompoundTag state = palette.getCompound(i);
            if (state.getString("Name").equals("minecraft:blue_glazed_terracotta")) {
                gateState = i;
                break;
            }
        }
        if (gateState < 0) {
            return Optional.empty();
        }

        ListTag blocks = tag.getList("blocks", Tag.TAG_COMPOUND);
        for (int i = 0; i < blocks.size(); i++) {
            CompoundTag block = blocks.getCompound(i);
            if (block.getInt("state") != gateState) {
                continue;
            }
            ListTag pos = block.getList("pos", Tag.TAG_INT);
            if (pos.size() >= 3) {
                return Optional.of(new TemplateAnchor(pos.getInt(0), pos.getInt(1), pos.getInt(2)));
            }
        }
        return Optional.empty();
    }

    private static BlockPos layoutBaseForGate(BlockPos gatePos, Direction facing, TemplateAnchor anchor) {
        Direction right = facing.getClockWise();
        int rightOffset = anchor.x() - RIGHT_RADIUS;
        int upOffset = anchor.y() - 1;
        int forwardOffset = -BACK_RADIUS + anchor.z();
        return gatePos.relative(right, -rightOffset)
                .relative(facing, -forwardOffset)
                .offset(0, -upOffset, 0);
    }

    private static RewardPlacement rewardPlacement(ServerLevel level, ExpeditionInstance expedition) {
        Optional<RewardPlacement> connectorPlacement = connectorRewardPlacement(level, expedition);
        if (connectorPlacement.isPresent()) {
            return connectorPlacement.get();
        }

        ExpeditionTemplates templates = discoverTemplates(level);
        if (!hasTemplate(level, GATE_ROOM_TEMPLATE) || !hasTemplate(level, REWARD_ROOM_TEMPLATE) || templates.combatRooms().isEmpty()) {
            return new RewardPlacement(expedition.basePos().relative(expedition.facing(), 22), expedition.facing());
        }

        BlockPos basePos = layoutBaseForGate(expedition.basePos(), expedition.facing(), gateRoomAnchor(level));
        BlockPos moduleStart = basePos.relative(expedition.facing(), -BACK_RADIUS);
        Direction moduleFacing = expedition.facing();
        for (ExpeditionModule module : buildModules(level, expedition, templates)) {
            if (module.reward()) {
                return new RewardPlacement(moduleStart, moduleFacing);
            }
            Direction exitFacing = module.exitFacing(moduleFacing);
            moduleStart = moduleStart.relative(moduleFacing, module.length());
            moduleFacing = exitFacing;
        }
        return new RewardPlacement(moduleStart, moduleFacing);
    }

    private static Optional<RewardPlacement> connectorRewardPlacement(ServerLevel level, ExpeditionInstance expedition) {
        ExpeditionTemplates templates = discoverTemplates(level);
        if (!hasTemplate(level, GATE_ROOM_TEMPLATE) || !hasTemplate(level, REWARD_ROOM_TEMPLATE) || templates.combatRooms().isEmpty()) {
            return Optional.empty();
        }

        List<ExpeditionModule> modules = buildModules(level, expedition, templates);
        PlacedConnector nextConnector = null;
        List<IntBox> placedBoxes = new ArrayList<>();
        int pathOriginY = Integer.MIN_VALUE;
        int salt = expedition.slot();
        for (ExpeditionModule module : modules) {
            Optional<TemplateInfo> maybeInfo = templateInfo(level, module.template());
            if (maybeInfo.isEmpty()) {
                continue;
            }
            TemplateInfo info = maybeInfo.get();
            Direction moduleFacing;
            BlockPos origin;
            Optional<Connector> entrance = Optional.empty();
            if (nextConnector == null) {
                moduleFacing = expedition.facing();
                BlockPos anchor = info.gateAnchor()
                        .orElse(new BlockPos(RIGHT_RADIUS, 1, BACK_RADIUS));
                origin = originForLocal(expedition.basePos(), anchor, moduleFacing);
                pathOriginY = origin.getY();
            } else {
                Optional<TemplatePlacement> placement = choosePlacement(info, nextConnector, pathOriginY, placedBoxes);
                if (placement.isEmpty()) {
                    return Optional.empty();
                }
                TemplatePlacement selected = placement.get();
                entrance = selected.entrance();
                moduleFacing = selected.facing();
                origin = selected.origin();
            }

            IntBox footprint = footprint(origin, moduleFacing, info.size());
            if (overlapsAny(footprint, placedBoxes)) {
                return Optional.empty();
            }
            placedBoxes.add(footprint);

            if (module.reward()) {
                BlockPos entrancePos = entrance
                        .map(connector -> transformLocal(origin, moduleFacing, connector.pos()))
                        .orElseGet(() -> templateCenter(info, origin, moduleFacing));
                Direction entranceFacing = nextConnector == null ? moduleFacing : nextConnector.direction();
                return Optional.of(new RewardPlacement(entrancePos, entranceFacing));
            }

            salt++;
            Optional<PlacedConnector> exit = chooseExit(info, origin, moduleFacing, entrance, placedBoxes, randomForConnector(level, expedition, salt));
            if (exit.isEmpty()) {
                return Optional.empty();
            }
            nextConnector = exit.get();
        }
        return Optional.empty();
    }

    private static Rotation rotationFor(Direction facing) {
        return switch (facing) {
            case EAST -> Rotation.COUNTERCLOCKWISE_90;
            case NORTH -> Rotation.CLOCKWISE_180;
            case WEST -> Rotation.CLOCKWISE_90;
            default -> Rotation.NONE;
        };
    }

    private static void register(ServerLevel level, ExpeditionInstance expedition, BlockPos basePos) {
        register(level, expedition, basePos, expedition.facing());
    }

    private static void register(ServerLevel level, ExpeditionInstance expedition, BlockPos basePos, Direction facing) {
        if (level.getBlockEntity(basePos) instanceof StargateBaseBlockEntity base) {
            base.setAncientPower(true);
        }
        StargateNetworkSavedData.get(level).registerOrUpdateActiveGate(
                expedition.address(),
                level.dimension(),
                basePos,
                facing,
                expedition.variant(),
                "expedition");
    }

    private static void clearRoom(ServerLevel level, BlockPos basePos, Direction facing) {
        Direction right = facing.getClockWise();
        for (int r = -RIGHT_RADIUS - 1; r <= RIGHT_RADIUS + 1; r++) {
            for (int f = -BACK_RADIUS - 1; f <= FRONT_RADIUS + 1; f++) {
                for (int y = -2; y <= HEIGHT + 1; y++) {
                    level.setBlock(basePos.relative(right, r).relative(facing, f).above(y), Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
                }
            }
        }
    }

    private static void placeShell(ServerLevel level, BlockPos basePos, Direction facing) {
        Direction right = facing.getClockWise();
        for (int r = -RIGHT_RADIUS; r <= RIGHT_RADIUS; r++) {
            for (int f = -BACK_RADIUS; f <= FRONT_RADIUS; f++) {
                for (int y = -1; y <= HEIGHT; y++) {
                    boolean boundary = r == -RIGHT_RADIUS || r == RIGHT_RADIUS || f == -BACK_RADIUS || f == FRONT_RADIUS || y == -1 || y == HEIGHT;
                    if (!boundary) {
                        continue;
                    }
                    level.setBlock(basePos.relative(right, r).relative(facing, f).above(y), shellState(r, f, y), Block.UPDATE_ALL);
                }
            }
        }
    }

    private static void placeInterior(ServerLevel level, BlockPos basePos, Direction facing) {
        Direction right = facing.getClockWise();
        for (int r = -4; r <= 4; r++) {
            for (int f = -4; f <= 6; f++) {
                level.setBlock(basePos.relative(right, r).relative(facing, f).below(), ModBlocks.LANTEAN_PANEL.get().defaultBlockState(), Block.UPDATE_ALL);
            }
        }

        for (int r = -8; r <= 8; r++) {
            for (int f = 8; f <= 24; f++) {
                BlockPos floor = basePos.relative(right, r).relative(facing, f).below();
                level.setBlock(floor, (Math.abs(r) == 8 || f == 8 || f == 24)
                        ? ModBlocks.LANTEAN_DARK_TRIM.get().defaultBlockState()
                        : ModBlocks.LANTEAN_PANEL.get().defaultBlockState(), Block.UPDATE_ALL);
            }
        }
    }

    private static BlockState shellState(int r, int f, int y) {
        if (y == HEIGHT && Math.abs(r) <= 5 && f >= 6 && f <= 20) {
            return ModBlocks.LANTEAN_GLASS.get().defaultBlockState();
        }
        if (Math.abs(r) == RIGHT_RADIUS && y >= 2 && y <= 6 && f >= 6 && f <= 22) {
            return ModBlocks.LANTEAN_GLASS.get().defaultBlockState();
        }
        return ModBlocks.ANCIENT_CONTAINMENT_BLOCK.get().defaultBlockState();
    }

    private static void placeFrame(ServerLevel level, BlockPos basePos, Direction facing, StargateVariant variant) {
        Direction right = facing.getClockWise();
        for (int y = 0; y <= 6; y++) {
            for (int x = -3; x <= 3; x++) {
                if (x == 0 && y == 0) {
                    level.setBlock(basePos, baseBlock(variant).defaultBlockState().setValue(StargateBaseBlock.FACING, facing), Block.UPDATE_ALL);
                    if (level.getBlockEntity(basePos) instanceof StargateBaseBlockEntity base) {
                        base.setAncientPower(true);
                    }
                } else if (isFramePosition(x, y)) {
                    Block block = isChevronPosition(x, y) ? chevronBlock(variant) : ringBlock(variant);
                    level.setBlock(basePos.relative(right, x).above(y), block.defaultBlockState(), Block.UPDATE_ALL);
                }
            }
        }
    }

    private static void applyExpeditionCamouflage(ServerLevel level, BlockPos basePos, Direction facing) {
        if (!(level.getBlockEntity(basePos) instanceof StargateBaseBlockEntity base) || base.hasBottomCamouflage()) {
            return;
        }

        findExpeditionCamouflageState(level, basePos, facing).ifPresent(base::setBottomCamouflage);
    }

    private static Optional<BlockState> findExpeditionCamouflageState(ServerLevel level, BlockPos basePos, Direction facing) {
        Direction right = facing.getClockWise();
        BlockPos[] preferred = {
                basePos.below(),
                basePos.relative(right, -1),
                basePos.relative(right, 1),
                basePos.relative(right, -3).below(),
                basePos.relative(right, 3).below(),
                basePos.relative(facing, 1),
                basePos.relative(facing.getOpposite(), 1),
                basePos.relative(facing, 4)
        };

        for (BlockPos pos : preferred) {
            BlockState state = level.getBlockState(pos);
            if (isValidExpeditionCamouflage(state)) {
                return Optional.of(state);
            }
        }

        for (BlockPos pos : BlockPos.betweenClosed(basePos.offset(-4, -1, -4), basePos.offset(4, 1, 4))) {
            BlockState state = level.getBlockState(pos);
            if (isValidExpeditionCamouflage(state)) {
                return Optional.of(state);
            }
        }

        return Optional.empty();
    }

    private static boolean isValidExpeditionCamouflage(BlockState state) {
        return StargateCamouflage.isValidCamouflage(state)
                && !state.is(Blocks.JIGSAW)
                && !state.is(Blocks.BLUE_GLAZED_TERRACOTTA)
                && !state.is(Blocks.CYAN_GLAZED_TERRACOTTA)
                && !state.is(Blocks.LIME_WOOL);
    }

    private static void placeDhd(ServerLevel level, BlockPos basePos, Direction facing, StargateVariant variant) {
        BlockPos dhdPos = basePos.relative(facing, 4).above(1);
        placeDhdAt(level, dhdPos, facing, variant);
    }

    private static void placeDhdAt(ServerLevel level, BlockPos dhdPos, Direction facing, StargateVariant variant) {
        level.setBlock(dhdPos.below(), ModBlocks.LANTEAN_PANEL.get().defaultBlockState(), Block.UPDATE_ALL);
        level.setBlock(dhdPos, dhdBlock(variant).defaultBlockState().setValue(DhdBlock.FACING, facing), Block.UPDATE_ALL);
        if (level.getBlockEntity(dhdPos) instanceof DhdBlockEntity dhd) {
            dhd.installChargedCrystal();
        }
    }

    private static void placeRewardChest(ServerLevel level, ExpeditionInstance expedition, RewardPlacement reward) {
        List<LootContainerMarker> markers = ExpeditionLootMarkers.takeRewardChests();
        if (!markers.isEmpty()) {
            for (LootContainerMarker marker : markers) {
                placeFilledRewardChest(level, expedition, marker.pos(), marker.facing());
            }
            return;
        }
        BlockPos chestPos = reward.entrance().relative(reward.facing(), 5).above();
        Direction chestFacing = reward.facing().getOpposite();
        placeFilledRewardChest(level, expedition, chestPos, chestFacing);
    }

    private static void placeFilledRewardChest(ServerLevel level, ExpeditionInstance expedition, BlockPos chestPos, Direction chestFacing) {
        level.setBlock(chestPos, Blocks.CHEST.defaultBlockState().setValue(ChestBlock.FACING, chestFacing), Block.UPDATE_ALL);
        if (level.getBlockEntity(chestPos) instanceof ChestBlockEntity chest) {
            chest.setLootTable(REWARD_LOOT_TABLE);
            chest.setLootTableSeed(moduleSeed(level, expedition) ^ chestPos.asLong() ^ 0x5245574152444C54L);
            chest.setChanged();
        }
    }

    private static void fillSideLoot(ServerLevel level, ExpeditionInstance expedition) {
        RandomSource random = RandomSource.create(moduleSeed(level, expedition) ^ 0x4C4F4F544A415253L);
        for (BlockPos pos : ExpeditionLootMarkers.takeSideContainers()) {
            if (level.getBlockEntity(pos) instanceof Container container) {
                fillSideContainer(container, random, expedition.tier());
                if (level.getBlockEntity(pos) instanceof BlockEntity blockEntity) {
                    blockEntity.setChanged();
                }
            }
        }

        for (BlockPos pos : ExpeditionLootMarkers.takeJars()) {
            if (level.getBlockEntity(pos) instanceof DecoratedPotBlockEntity pot) {
                pot.setTheItem(randomJarLoot(random, expedition.tier()));
                pot.setChanged();
            }
        }
    }

    private static void fillSideContainer(Container container, RandomSource random, int tier) {
        container.clearContent();
        int rolls = 2 + random.nextInt(2 + Math.max(1, tier));
        for (int i = 0; i < rolls; i++) {
            ItemStack stack = randomSideLoot(random, tier);
            if (stack.isEmpty()) {
                continue;
            }
            int slot = randomEmptySlot(container, random);
            if (slot < 0) {
                break;
            }
            container.setItem(slot, stack);
        }
        container.setChanged();
    }

    private static int randomEmptySlot(Container container, RandomSource random) {
        List<Integer> emptySlots = new ArrayList<>();
        for (int slot = 0; slot < container.getContainerSize(); slot++) {
            if (container.getItem(slot).isEmpty()) {
                emptySlots.add(slot);
            }
        }
        if (emptySlots.isEmpty()) {
            return -1;
        }
        return emptySlots.get(random.nextInt(emptySlots.size()));
    }

    private static ItemStack randomSideLoot(RandomSource random, int tier) {
        int roll = random.nextInt(100);
        if (roll < 22) {
            return new ItemStack(Items.BREAD, 1 + random.nextInt(3));
        }
        if (roll < 38) {
            return new ItemStack(Items.TORCH, 2 + random.nextInt(7));
        }
        if (roll < 53) {
            return new ItemStack(Items.ARROW, 3 + random.nextInt(8));
        }
        if (roll < 66) {
            return new ItemStack(Items.IRON_INGOT, 1 + random.nextInt(2));
        }
        if (roll < 78) {
            return new ItemStack(ModItems.NAQUADAH.get(), 1 + random.nextInt(2 + tier));
        }
        if (roll < 88) {
            return new ItemStack(ModItems.TRINIUM.get(), 1 + random.nextInt(Math.max(1, tier)));
        }
        if (roll < 96) {
            return new ItemStack(ModItems.BLANK_CRYSTAL.get());
        }
        return tier >= 2 ? new ItemStack(ModItems.CONTROL_CRYSTAL.get()) : new ItemStack(Items.GOLD_INGOT);
    }

    private static ItemStack randomJarLoot(RandomSource random, int tier) {
        int roll = random.nextInt(100);
        if (roll < 35) {
            return new ItemStack(Items.EMERALD);
        }
        if (roll < 55) {
            return new ItemStack(Items.GOLD_NUGGET, 2 + random.nextInt(5));
        }
        if (roll < 72) {
            return new ItemStack(ModItems.NAQUADAH.get());
        }
        if (roll < 86) {
            return new ItemStack(ModItems.TRINIUM.get());
        }
        if (roll < 96) {
            return new ItemStack(ModItems.BLANK_CRYSTAL.get());
        }
        return tier >= 3 ? new ItemStack(ModItems.CORE_CRYSTAL.get()) : ItemStack.EMPTY;
    }

    private static void spawnGuards(ServerLevel level, List<BlockPos> combatRoomCenters, int tier) {
        List<BlockPos> markedSpawns = ExpeditionSpawnMarkers.takePending();
        if (!markedSpawns.isEmpty()) {
            spawnMarkedGuards(level, markedSpawns, tier);
            return;
        }

        int countPerRoom = 2 + tier;
        for (BlockPos center : combatRoomCenters) {
            for (int i = 0; i < countPerRoom; i++) {
                int x = center.getX() + (i % 2 == 0 ? -2 : 2) + (i % 3);
                int z = center.getZ() + (i / 2);
                ModEntities.GOAULD_SOLDIER.get().spawn(level, new BlockPos(x, center.getY(), z), MobSpawnType.STRUCTURE);
            }
        }
    }

    private static void spawnMarkedGuards(ServerLevel level, List<BlockPos> spawns, int tier) {
        int maxPerMarker = Math.max(1, Math.min(3, tier));
        for (BlockPos spawn : spawns) {
            for (int i = 0; i < maxPerMarker; i++) {
                ModEntities.GOAULD_SOLDIER.get().spawn(level, spawn.offset(i % 2, 0, i / 2), MobSpawnType.STRUCTURE);
            }
        }
    }

    private static boolean isAssembledGateAt(ServerLevel level, BlockPos basePos) {
        BlockState state = level.getBlockState(basePos);
        return state.hasProperty(StargateBaseBlock.ASSEMBLED) && state.getValue(StargateBaseBlock.ASSEMBLED);
    }

    private static boolean isFramePosition(int x, int y) {
        return y == 0 || y == 6 || Math.abs(x) == 3;
    }

    private static boolean isChevronPosition(int x, int y) {
        return (x == 0 && y == 6)
                || (x == -2 && y == 6)
                || (x == 2 && y == 6)
                || (x == -3 && y == 4)
                || (x == 3 && y == 4)
                || (x == -3 && y == 1)
                || (x == 3 && y == 1)
                || (x == -2 && y == 0)
                || (x == 2 && y == 0);
    }

    private static Block baseBlock(StargateVariant variant) {
        return switch (variant) {
            case MILKY_WAY -> ModBlocks.STARGATE_BASE.get();
            case NOX -> ModBlocks.NOX_STARGATE_BASE.get();
            case WRAITH -> ModBlocks.WRAITH_STARGATE_BASE.get();
            case PEGASUS -> ModBlocks.PEGASUS_STARGATE_BASE.get();
        };
    }

    private static Block ringBlock(StargateVariant variant) {
        return switch (variant) {
            case MILKY_WAY -> ModBlocks.STARGATE_RING.get();
            case NOX -> ModBlocks.NOX_STARGATE_RING.get();
            case WRAITH -> ModBlocks.WRAITH_STARGATE_RING.get();
            case PEGASUS -> ModBlocks.PEGASUS_STARGATE_RING.get();
        };
    }

    private static Block chevronBlock(StargateVariant variant) {
        return switch (variant) {
            case MILKY_WAY -> ModBlocks.STARGATE_CHEVRON.get();
            case NOX -> ModBlocks.NOX_STARGATE_CHEVRON.get();
            case WRAITH -> ModBlocks.WRAITH_STARGATE_CHEVRON.get();
            case PEGASUS -> ModBlocks.PEGASUS_STARGATE_CHEVRON.get();
        };
    }

    private static Block dhdBlock(StargateVariant variant) {
        return switch (variant) {
            case MILKY_WAY -> ModBlocks.DHD.get();
            case NOX -> ModBlocks.NOX_DHD.get();
            case WRAITH -> ModBlocks.WRAITH_DHD.get();
            case PEGASUS -> ModBlocks.PEGASUS_DHD.get();
        };
    }

    private enum Turn {
        STRAIGHT,
        LEFT,
        RIGHT
    }

    private record ExpeditionModule(ResourceLocation template, int length, boolean combat, boolean reward, Turn turn) {
        private Direction exitFacing(Direction facing) {
            return switch (turn) {
                case LEFT -> facing.getCounterClockWise();
                case RIGHT -> facing.getClockWise();
                case STRAIGHT -> facing;
            };
        }
    }

    private record ExpeditionTemplates(
            List<ResourceLocation> halls,
            List<ResourceLocation> leftHalls,
            List<ResourceLocation> rightHalls,
            List<ResourceLocation> rooms,
            List<ResourceLocation> intersections,
            List<ResourceLocation> combatRooms) {
    }

    private record TemplateMarkers(List<BlockPos> spawnPositions, Optional<RewardPlacement> rewardPlacement) {
    }

    private record WorldMarkers(List<BlockPos> spawnPositions, List<BlockPos> combatRoomCenters, Optional<RewardPlacement> rewardPlacement, int sealedDoorways) {
    }

    private record DoorwayOpening(int width, int height) {
    }

    private record TemplateInfo(ResourceLocation id, BlockPos size, List<Connector> connectors, Optional<BlockPos> gateAnchor) {
    }

    private record Connector(BlockPos pos, Direction direction, String name, String target, String pool) {
        private boolean isOutgoing() {
            return !target.equals(EMPTY_JIGSAW) && !pool.equals(EMPTY_JIGSAW);
        }
    }

    private record PlacedConnector(BlockPos pos, Direction direction, String target, String pool) {
    }

    private record TemplatePlacement(BlockPos origin, Direction facing, Optional<Connector> entrance, IntBox footprint) {
    }

    private record PlacedPiece(TemplateInfo info, BlockPos origin, Direction facing, Connector entrance, IntBox footprint) {
    }

    private record IntBox(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        private boolean overlaps(IntBox other) {
            return minX <= other.maxX && maxX >= other.minX
                    && minY <= other.maxY && maxY >= other.minY
                    && minZ <= other.maxZ && maxZ >= other.minZ;
        }

        private boolean contains(BlockPos pos) {
            return pos.getX() >= minX && pos.getX() <= maxX
                    && pos.getY() >= minY && pos.getY() <= maxY
                    && pos.getZ() >= minZ && pos.getZ() <= maxZ;
        }
    }

    private record TemplateAnchor(int x, int y, int z) {
        private static final TemplateAnchor DEFAULT = new TemplateAnchor(RIGHT_RADIUS, 1, BACK_RADIUS);
    }

    private record RewardPlacement(BlockPos entrance, Direction facing) {
    }

    private record LootContainerMarker(BlockPos pos, Direction facing) {
    }

    public record DebugPlacementCheck(boolean clear, BlockPos layoutBasePos, int radius, int minYOffset, int maxYOffset, List<BlockPos> blockedSamples) {
    }

    private static final class ExpeditionTrialPlacements {
        private static Optional<RewardPlacement> reward = Optional.empty();

        private static void rememberReward(BlockPos entrance, Direction facing) {
            reward = Optional.of(new RewardPlacement(entrance.immutable(), facing));
        }

        private static Optional<RewardPlacement> takeReward() {
            Optional<RewardPlacement> placement = reward;
            reward = Optional.empty();
            return placement;
        }
    }

    private static final class ExpeditionSpawnMarkers {
        private static List<BlockPos> pending = List.of();

        private static void remember(String address, List<BlockPos> spawnPositions) {
            pending = List.copyOf(spawnPositions);
        }

        private static List<BlockPos> takePending() {
            List<BlockPos> spawns = pending;
            pending = List.of();
            return spawns;
        }
    }

    private static final class ExpeditionLootMarkers {
        private static List<LootContainerMarker> rewardChests = List.of();
        private static List<BlockPos> sideContainers = List.of();
        private static List<BlockPos> jars = List.of();

        private static void rememberRewardChest(BlockPos pos, Direction facing) {
            List<LootContainerMarker> updated = new ArrayList<>(rewardChests);
            updated.add(new LootContainerMarker(pos.immutable(), facing));
            rewardChests = List.copyOf(updated);
        }

        private static List<LootContainerMarker> takeRewardChests() {
            List<LootContainerMarker> markers = rewardChests;
            rewardChests = List.of();
            return markers;
        }

        private static void rememberSideContainer(BlockPos pos) {
            List<BlockPos> updated = new ArrayList<>(sideContainers);
            updated.add(pos.immutable());
            sideContainers = List.copyOf(updated);
        }

        private static List<BlockPos> takeSideContainers() {
            List<BlockPos> markers = sideContainers;
            sideContainers = List.of();
            return markers;
        }

        private static void rememberJar(BlockPos pos) {
            List<BlockPos> updated = new ArrayList<>(jars);
            updated.add(pos.immutable());
            jars = List.copyOf(updated);
        }

        private static List<BlockPos> takeJars() {
            List<BlockPos> markers = jars;
            jars = List.of();
            return markers;
        }
    }

    private record TemplatePlacementMarkers(Optional<BlockPos> gatePos, Optional<Direction> gateFacing, Optional<BlockPos> dhdPos, Optional<Direction> dhdFacing) {
        private static TemplatePlacementMarkers pending = empty();

        private static TemplatePlacementMarkers empty() {
            return new TemplatePlacementMarkers(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
        }

        private static void rememberGate(BlockPos pos, Direction facing) {
            pending = new TemplatePlacementMarkers(Optional.of(pos.immutable()), Optional.of(facing), pending.dhdPos(), pending.dhdFacing());
        }

        private static void rememberDhd(BlockPos pos, Direction facing) {
            pending = new TemplatePlacementMarkers(pending.gatePos(), pending.gateFacing(), Optional.of(pos.immutable()), Optional.of(facing));
        }

        private static TemplatePlacementMarkers takePending() {
            TemplatePlacementMarkers markers = pending;
            pending = empty();
            return markers;
        }
    }
}
