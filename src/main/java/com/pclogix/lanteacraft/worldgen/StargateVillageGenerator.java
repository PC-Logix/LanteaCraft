package com.pclogix.lanteacraft.worldgen;

import com.pclogix.lanteacraft.LanteaCraft;
import com.pclogix.lanteacraft.block.DhdBlock;
import com.pclogix.lanteacraft.block.entity.DhdBlockEntity;
import com.pclogix.lanteacraft.block.StargateBaseBlock;
import com.pclogix.lanteacraft.block.entity.StargateBaseBlockEntity;
import com.pclogix.lanteacraft.gate.StargateCamouflage;
import com.pclogix.lanteacraft.gate.StargateMultiblock;
import com.pclogix.lanteacraft.gate.StargateNetworkSavedData;
import com.pclogix.lanteacraft.gate.StargateVariant;
import com.pclogix.lanteacraft.registry.ModBlocks;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

public final class StargateVillageGenerator {
    private static final ResourceLocation PLATFORM_TEMPLATE = ResourceLocation.fromNamespaceAndPath(LanteaCraft.MODID, "village_stargate_platform");
    private static final BlockPos TEMPLATE_PIVOT = new BlockPos(5, 1, 2);
    private static final int TEMPLATE_Y_OFFSET = 1;
    private static final int MIN_VILLAGE_DISTANCE = 72;
    private static final int IDEAL_VILLAGE_DISTANCE = 96;
    private static final int MAX_VILLAGE_DISTANCE = 160;
    private static final int SEARCH_STEP = 4;
    private static final int PLATFORM_RIGHT_RADIUS = 6;
    private static final int PLATFORM_BACK_RADIUS = 4;
    private static final int PLATFORM_FRONT_RADIUS = 8;
    private static final int BUILDING_CLEARANCE = 12;

    private StargateVillageGenerator() {
    }

    public static boolean placeIfNeeded(ServerLevel level, PlannedStargate plan) {
        return placeIfNeeded(level, plan, true);
    }

    public static boolean placeAtPlannedBaseIfNeeded(ServerLevel level, PlannedStargate plan) {
        return placeIfNeeded(level, plan, false);
    }

    private static boolean placeIfNeeded(ServerLevel level, PlannedStargate plan, boolean searchForPlacement) {
        PlannedStargateSavedData data = PlannedStargateSavedData.get(level);
        if (data.isPlaced(plan.address())) {
            return false;
        }

        if (!searchForPlacement) {
            loadFastPlacementArea(level, fastPlacementAnchor(plan), plan.facing());
        }
        BlockPos basePos = searchForPlacement ? findPlacementBase(level, plan) : findFastPlacementBase(level, plan);
        clearPlacementSpace(level, basePos, plan.facing());
        placePlatformTemplate(level, basePos, plan.facing());
        clearFrameSpace(level, basePos, plan.facing());
        prepareFooting(level, basePos, plan.facing());
        placeFrame(level, basePos, plan.facing(), plan.variant());
        placeDhd(level, basePos, plan.facing(), plan.variant());
        StargateNetworkSavedData.get(level).registerOrUpdateActiveGate(plan.address(), level.dimension(), basePos, plan.facing(), plan.variant(), "planned");
        StargateMultiblock.tryAssembleAtBase(level, basePos);
        applyVillageCamouflage(level, basePos, plan.facing());
        TokraTraderSpawner.spawnForGate(level, plan, basePos);
        data.markPlaced(plan.address());
        return true;
    }

    public static boolean isPlacementChunk(ServerLevel level, PlannedStargate plan, ChunkPos chunkPos) {
        BlockPos basePos = findPlacementBase(level, plan);
        return new ChunkPos(basePos).equals(chunkPos);
    }

    private static BlockPos findPlacementBase(ServerLevel level, PlannedStargate plan) {
        BlockPos fallback = surfaceBase(level, plan.basePos());
        BlockPos best = null;
        int bestScore = Integer.MAX_VALUE;
        BlockPos bestRelaxed = null;
        int bestRelaxedScore = Integer.MAX_VALUE;

        int minX = plan.villagePos().getX() - MAX_VILLAGE_DISTANCE;
        int maxX = plan.villagePos().getX() + MAX_VILLAGE_DISTANCE;
        int minZ = plan.villagePos().getZ() - MAX_VILLAGE_DISTANCE;
        int maxZ = plan.villagePos().getZ() + MAX_VILLAGE_DISTANCE;
        loadSearchArea(level, minX - BUILDING_CLEARANCE, minZ - BUILDING_CLEARANCE, maxX + BUILDING_CLEARANCE, maxZ + BUILDING_CLEARANCE);
        for (int x = minX; x <= maxX; x += SEARCH_STEP) {
            for (int z = minZ; z <= maxZ; z += SEARCH_STEP) {
                int villageDistance = Math.abs(x - plan.villagePos().getX()) + Math.abs(z - plan.villagePos().getZ());
                if (villageDistance < MIN_VILLAGE_DISTANCE || villageDistance > MAX_VILLAGE_DISTANCE) {
                    continue;
                }

                Optional<BlockPos> candidate = naturalSurfaceBase(level, x, z);
                if (candidate.isEmpty()) {
                    continue;
                }

                int relaxedScore = placementScore(level, candidate.get(), plan, false);
                if (relaxedScore < bestRelaxedScore) {
                    bestRelaxed = candidate.get();
                    bestRelaxedScore = relaxedScore;
                }

                int score = placementScore(level, candidate.get(), plan, true);
                if (score == Integer.MAX_VALUE) {
                    continue;
                }

                if (score < bestScore) {
                    best = candidate.get();
                    bestScore = score;
                }
            }
        }

        if (best == null) {
            if (bestRelaxed != null) {
                LanteaCraft.LOGGER.warn("No perfect village Stargate placement found near {}; using least-obstructed moved base {} instead of planned base {}", plan.villagePos(), bestRelaxed, plan.basePos());
                return bestRelaxed;
            }

            LanteaCraft.LOGGER.warn("No village Stargate placement candidate found near {}; falling back to planned base {}", plan.villagePos(), plan.basePos());
            return fallback;
        }

        return best;
    }

    private static BlockPos findFastPlacementBase(ServerLevel level, PlannedStargate plan) {
        BlockPos anchor = fastPlacementAnchor(plan);
        return surfaceBase(level, anchor);
    }

    private static BlockPos fastPlacementAnchor(PlannedStargate plan) {
        if (plan.villagePos().equals(plan.basePos())) {
            return plan.basePos();
        }
        return plan.villagePos().relative(plan.facing(), IDEAL_VILLAGE_DISTANCE).atY(plan.basePos().getY());
    }

    private static void loadFastPlacementArea(ServerLevel level, BlockPos basePos, Direction facing) {
        Direction right = facing.getClockWise();
        int minX = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int minZ = Integer.MAX_VALUE;
        int maxZ = Integer.MIN_VALUE;
        for (int r = -PLATFORM_RIGHT_RADIUS; r <= PLATFORM_RIGHT_RADIUS; r++) {
            for (int f = -PLATFORM_BACK_RADIUS; f <= PLATFORM_FRONT_RADIUS; f++) {
                BlockPos pos = basePos.relative(right, r).relative(facing, f);
                minX = Math.min(minX, pos.getX());
                maxX = Math.max(maxX, pos.getX());
                minZ = Math.min(minZ, pos.getZ());
                maxZ = Math.max(maxZ, pos.getZ());
            }
        }
        loadSearchArea(level, minX, minZ, maxX, maxZ);
    }

    private static void loadSearchArea(ServerLevel level, int minX, int minZ, int maxX, int maxZ) {
        int minChunkX = SectionPos.blockToSectionCoord(minX);
        int maxChunkX = SectionPos.blockToSectionCoord(maxX);
        int minChunkZ = SectionPos.blockToSectionCoord(minZ);
        int maxChunkZ = SectionPos.blockToSectionCoord(maxZ);
        for (int chunkX = minChunkX; chunkX <= maxChunkX; chunkX++) {
            for (int chunkZ = minChunkZ; chunkZ <= maxChunkZ; chunkZ++) {
                level.getChunk(chunkX, chunkZ);
            }
        }
    }

    private static BlockPos surfaceBase(ServerLevel level, BlockPos plannedBase) {
        return naturalSurfaceBase(level, plannedBase.getX(), plannedBase.getZ())
                .orElseGet(() -> {
                    int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, plannedBase.getX(), plannedBase.getZ());
                    return new BlockPos(plannedBase.getX(), y, plannedBase.getZ());
                });
    }

    private static Optional<BlockPos> naturalSurfaceBase(ServerLevel level, int x, int z) {
        int topY = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z) - 1;
        int minY = level.getMinBuildHeight();
        for (int y = topY; y >= minY; y--) {
            BlockState support = level.getBlockState(new BlockPos(x, y, z));
            if (isPreferredGround(support)) {
                return Optional.of(new BlockPos(x, y + 1, z));
            }
        }

        return Optional.empty();
    }

    private static int placementScore(ServerLevel level, BlockPos basePos, PlannedStargate plan, boolean strict) {
        TerrainCheck terrain = terrainCheck(level, basePos, plan.facing(), strict);
        if (!terrain.usable()) {
            return Integer.MAX_VALUE;
        }

        int obstruction = 0;
        int building = 0;
        int water = 0;
        for (BlockPos pos : BlockPos.betweenClosed(basePos.offset(-BUILDING_CLEARANCE, -1, -BUILDING_CLEARANCE), basePos.offset(BUILDING_CLEARANCE, 8, BUILDING_CLEARANCE))) {
            BlockState state = level.getBlockState(pos);
            if (state.is(Blocks.WATER)) {
                water++;
            }
            if (!state.isAir() && !state.canBeReplaced() && !isPreferredGround(state)) {
                obstruction++;
                if (isVillageBuildingBlock(state)) {
                    building++;
                }
            }
        }

        if (strict && building > 0) {
            return Integer.MAX_VALUE;
        }

        int plannedDistance = Math.abs(basePos.getX() - plan.basePos().getX()) + Math.abs(basePos.getZ() - plan.basePos().getZ());
        int villageDistance = Math.abs(basePos.getX() - plan.villagePos().getX()) + Math.abs(basePos.getZ() - plan.villagePos().getZ());
        int villageDistancePenalty = Math.abs(villageDistance - IDEAL_VILLAGE_DISTANCE) * 20;

        return obstruction * 1500 + building * 20000 + water * 5000 + terrain.roughness() * 250 + plannedDistance + villageDistancePenalty;
    }

    private static TerrainCheck terrainCheck(ServerLevel level, BlockPos basePos, Direction facing, boolean strict) {
        Direction right = facing.getClockWise();
        int minY = Integer.MAX_VALUE;
        int maxY = Integer.MIN_VALUE;
        int water = 0;
        int building = 0;

        for (int r = -PLATFORM_RIGHT_RADIUS; r <= PLATFORM_RIGHT_RADIUS; r += 2) {
            for (int f = -PLATFORM_BACK_RADIUS; f <= PLATFORM_FRONT_RADIUS; f += 2) {
                BlockPos sample = basePos.relative(right, r).relative(facing, f);
                int surfaceY = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, sample.getX(), sample.getZ()) - 1;
                BlockState surface = level.getBlockState(new BlockPos(sample.getX(), surfaceY, sample.getZ()));
                if (surface.is(Blocks.WATER)) {
                    water++;
                }
                if (isVillageBuildingBlock(surface)) {
                    building++;
                }
                if (!isPreferredGround(surface) && !surface.canBeReplaced()) {
                    building++;
                }
                minY = Math.min(minY, surfaceY);
                maxY = Math.max(maxY, surfaceY);
            }
        }

        if (water > 0 || (strict && building > 0) || maxY - minY > 3) {
            return TerrainCheck.unusable();
        }

        return new TerrainCheck(true, maxY - minY);
    }

    private static boolean isPreferredGround(BlockState state) {
        return state.is(Blocks.GRASS_BLOCK)
                || state.is(Blocks.DIRT)
                || state.is(Blocks.COARSE_DIRT)
                || state.is(Blocks.PODZOL)
                || state.is(Blocks.DIRT_PATH)
                || state.is(Blocks.SAND)
                || state.is(Blocks.RED_SAND)
                || state.is(Blocks.STONE)
                || state.is(Blocks.SNOW_BLOCK);
    }

    private static boolean isVillageBuildingBlock(BlockState state) {
        return state.is(Blocks.OAK_PLANKS)
                || state.is(Blocks.SPRUCE_PLANKS)
                || state.is(Blocks.BIRCH_PLANKS)
                || state.is(Blocks.ACACIA_PLANKS)
                || state.is(Blocks.DARK_OAK_PLANKS)
                || state.is(Blocks.OAK_LOG)
                || state.is(Blocks.SPRUCE_LOG)
                || state.is(Blocks.BIRCH_LOG)
                || state.is(Blocks.ACACIA_LOG)
                || state.is(Blocks.DARK_OAK_LOG)
                || state.is(Blocks.STRIPPED_OAK_LOG)
                || state.is(Blocks.STRIPPED_SPRUCE_LOG)
                || state.is(Blocks.STRIPPED_BIRCH_LOG)
                || state.is(Blocks.STRIPPED_ACACIA_LOG)
                || state.is(Blocks.STRIPPED_DARK_OAK_LOG)
                || state.is(Blocks.ACACIA_STAIRS)
                || state.is(Blocks.BIRCH_STAIRS)
                || state.is(Blocks.DARK_OAK_STAIRS)
                || state.is(Blocks.JUNGLE_STAIRS)
                || state.is(Blocks.OAK_STAIRS)
                || state.is(Blocks.SPRUCE_STAIRS)
                || state.is(Blocks.ACACIA_SLAB)
                || state.is(Blocks.BIRCH_SLAB)
                || state.is(Blocks.DARK_OAK_SLAB)
                || state.is(Blocks.JUNGLE_SLAB)
                || state.is(Blocks.OAK_SLAB)
                || state.is(Blocks.SPRUCE_SLAB)
                || state.is(Blocks.COBBLESTONE)
                || state.is(Blocks.MOSSY_COBBLESTONE)
                || state.is(Blocks.COBBLESTONE_STAIRS)
                || state.is(Blocks.COBBLESTONE_SLAB)
                || state.is(Blocks.COBBLESTONE_WALL)
                || state.is(Blocks.MOSSY_COBBLESTONE_WALL)
                || state.is(Blocks.SANDSTONE)
                || state.is(Blocks.CUT_SANDSTONE)
                || state.is(Blocks.SMOOTH_SANDSTONE)
                || state.is(Blocks.SANDSTONE_STAIRS)
                || state.is(Blocks.SANDSTONE_SLAB)
                || state.is(Blocks.SANDSTONE_WALL)
                || state.is(Blocks.TERRACOTTA)
                || state.is(Blocks.WHITE_TERRACOTTA)
                || state.is(Blocks.ORANGE_TERRACOTTA)
                || state.is(Blocks.YELLOW_TERRACOTTA)
                || state.is(Blocks.LIGHT_GRAY_TERRACOTTA)
                || state.is(Blocks.BROWN_TERRACOTTA)
                || state.is(Blocks.HAY_BLOCK)
                || state.is(Blocks.GLASS)
                || state.is(Blocks.GLASS_PANE);
    }

    private static void clearPlacementSpace(ServerLevel level, BlockPos basePos, Direction facing) {
        Direction right = facing.getClockWise();
        for (int r = -PLATFORM_RIGHT_RADIUS; r <= PLATFORM_RIGHT_RADIUS; r++) {
            for (int f = -PLATFORM_BACK_RADIUS; f <= PLATFORM_FRONT_RADIUS; f++) {
                for (int y = 0; y <= 8; y++) {
                    BlockPos pos = basePos.relative(right, r).relative(facing, f).above(y);
                    if (!level.getBlockState(pos).is(Blocks.BEDROCK)) {
                        level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
                    }
                }
            }
        }
    }

    private static void placePlatformTemplate(ServerLevel level, BlockPos basePos, Direction facing) {
        Optional<StructureTemplate> template = level.getStructureManager().get(PLATFORM_TEMPLATE);
        if (template.isEmpty()) {
            LanteaCraft.LOGGER.warn("Missing Stargate village platform template {}; using generated footing only.", PLATFORM_TEMPLATE);
            return;
        }

        StructurePlaceSettings settings = new StructurePlaceSettings()
                .setRotation(rotationFor(facing))
                .setRotationPivot(TEMPLATE_PIVOT)
                .setIgnoreEntities(true)
                .setKnownShape(true);
        BlockPos origin = basePos.offset(
                -TEMPLATE_PIVOT.getX(),
                -TEMPLATE_PIVOT.getY() + TEMPLATE_Y_OFFSET,
                -TEMPLATE_PIVOT.getZ()
        );
        template.get().placeInWorld(level, origin, origin, settings, RandomSource.create(level.getSeed() ^ basePos.asLong()), Block.UPDATE_ALL);
    }

    private static Rotation rotationFor(Direction facing) {
        return switch (facing) {
            case EAST -> Rotation.COUNTERCLOCKWISE_90;
            case NORTH -> Rotation.CLOCKWISE_180;
            case WEST -> Rotation.CLOCKWISE_90;
            default -> Rotation.NONE;
        };
    }

    private static void clearFrameSpace(ServerLevel level, BlockPos basePos, Direction facing) {
        Direction right = facing.getClockWise();
        for (int y = 0; y <= 6; y++) {
            for (int x = -3; x <= 3; x++) {
                BlockPos pos = basePos.relative(right, x).above(y);
                if (!level.getBlockState(pos).is(Blocks.BEDROCK)) {
                    level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
                }
            }
        }
        BlockPos dhdPos = basePos.relative(facing, 4).above(TEMPLATE_Y_OFFSET);
        if (!level.getBlockState(dhdPos).is(Blocks.BEDROCK)) {
            level.setBlock(dhdPos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
        }
    }

    private static void prepareFooting(ServerLevel level, BlockPos basePos, Direction facing) {
        Direction right = facing.getClockWise();
        for (int x = -3; x <= 3; x++) {
            BlockPos footing = basePos.relative(right, x).below();
            if (level.getBlockState(footing).isAir()) {
                level.setBlock(footing, Blocks.SMOOTH_STONE.defaultBlockState(), Block.UPDATE_ALL);
            }
        }
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

    private static void placeDhd(ServerLevel level, BlockPos basePos, Direction facing, StargateVariant variant) {
        BlockPos dhdPos = basePos.relative(facing, 4).above(TEMPLATE_Y_OFFSET);
        BlockState support = level.getBlockState(dhdPos.below());
        if (support.isAir()) {
            level.setBlock(dhdPos.below(), Blocks.SMOOTH_STONE.defaultBlockState(), Block.UPDATE_ALL);
        }
        level.setBlock(dhdPos, dhdBlock(variant).defaultBlockState().setValue(DhdBlock.FACING, facing), Block.UPDATE_ALL);
        if (level.getBlockEntity(dhdPos) instanceof DhdBlockEntity dhd) {
            dhd.installChargedCrystal();
        }
    }

    private static void applyVillageCamouflage(ServerLevel level, BlockPos basePos, Direction facing) {
        if (!(level.getBlockEntity(basePos) instanceof StargateBaseBlockEntity base) || base.hasBottomCamouflage()) {
            return;
        }

        BlockState platformState = Blocks.STONE_BRICKS.defaultBlockState();
        if (StargateCamouflage.isValidCamouflage(platformState)) {
            base.setBottomCamouflage(platformState);
            return;
        }

        findCamouflageState(level, basePos, facing).ifPresent(base::setBottomCamouflage);
    }

    private static void applyDefaultCamouflage(ServerLevel level, BlockPos basePos, Direction facing) {
        if (!(level.getBlockEntity(basePos) instanceof StargateBaseBlockEntity base) || base.hasBottomCamouflage()) {
            return;
        }

        findCamouflageState(level, basePos, facing).ifPresent(base::setBottomCamouflage);
    }

    private static Optional<BlockState> findCamouflageState(ServerLevel level, BlockPos basePos, Direction facing) {
        Direction right = facing.getClockWise();
        BlockPos[] preferred = {
                basePos.below(),
                basePos.relative(right, -3).below(),
                basePos.relative(right, 3).below(),
                basePos.relative(facing, 4).above(TEMPLATE_Y_OFFSET).below(),
                basePos.relative(facing, 5).above(TEMPLATE_Y_OFFSET).below(),
                basePos.relative(facing.getOpposite(), 1).below()
        };

        for (BlockPos pos : preferred) {
            BlockState state = level.getBlockState(pos);
            if (StargateCamouflage.isValidCamouflage(state)) {
                return Optional.of(state);
            }
        }

        for (BlockPos pos : BlockPos.betweenClosed(basePos.offset(-5, -2, -5), basePos.offset(5, 1, 5))) {
            BlockState state = level.getBlockState(pos);
            if (StargateCamouflage.isValidCamouflage(state)) {
                return Optional.of(state);
            }
        }

        return Optional.empty();
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

    private record TerrainCheck(boolean usable, int roughness) {
        private static TerrainCheck unusable() {
            return new TerrainCheck(false, Integer.MAX_VALUE);
        }
    }
}
