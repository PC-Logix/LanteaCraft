package com.pclogix.lanteacraft.worldgen;

import com.pclogix.lanteacraft.LanteaCraft;
import com.pclogix.lanteacraft.block.DhdBlock;
import com.pclogix.lanteacraft.block.StargateBaseBlock;
import com.pclogix.lanteacraft.block.StargateComponentBlock;
import com.pclogix.lanteacraft.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.neoforge.event.level.ChunkEvent;

/** Builds the monumental, movie-inspired arrival complex around the fixed Abydos gate. */
public final class AbydosPyramidGenerator {
    public static final int GATE_PLATFORM_HEIGHT = 8;

    private static final int PYRAMID_HALF_WIDTH = 96;
    private static final int PYRAMID_HEIGHT = 96;
    private static final int PYRAMID_CENTER_F = -42;
    private static final int TERRAIN_BLEND_RADIUS = 32;
    private static final int MIN_R = -PYRAMID_HALF_WIDTH - TERRAIN_BLEND_RADIUS;
    private static final int MAX_R = PYRAMID_HALF_WIDTH + TERRAIN_BLEND_RADIUS;
    private static final int MIN_F = PYRAMID_CENTER_F - PYRAMID_HALF_WIDTH - TERRAIN_BLEND_RADIUS;
    private static final int MAX_F = 150 + TERRAIN_BLEND_RADIUS;
    private static final int UPDATE_FLAGS = Block.UPDATE_CLIENTS | Block.UPDATE_KNOWN_SHAPE;

    private AbydosPyramidGenerator() {
    }

    public static void ensureInitialized(ServerLevel level, BlockPos gateBase, Direction facing, int gatePlatformHeight) {
        AbydosComplexSavedData data = AbydosComplexSavedData.get(level);
        boolean newAnchor = data.initialize(gateBase, facing, gatePlatformHeight);
        if (newAnchor) {
            clearLegacyPyramid(level, gateBase, facing);
            LanteaCraft.LOGGER.info("Preparing the Abydos pyramid complex around gate {} facing {}.", gateBase, facing);
        }

        int gateChunkX = gateBase.getX() >> 4;
        int gateChunkZ = gateBase.getZ() >> 4;
        for (int chunkX = gateChunkX - 12; chunkX <= gateChunkX + 12; chunkX++) {
            for (int chunkZ = gateChunkZ - 12; chunkZ <= gateChunkZ + 12; chunkZ++) {
                LevelChunk chunk = level.getChunkSource().getChunkNow(chunkX, chunkZ);
                if (chunk != null) {
                    generateChunkIfNeeded(level, chunk.getPos(), data);
                }
            }
        }
    }

    public static void onChunkLoad(ChunkEvent.Load event) {
        LevelAccessor accessor = event.getLevel();
        if (!(accessor instanceof ServerLevel level)
                || !(event.getChunk() instanceof LevelChunk chunk)
                || !level.dimension().equals(LanteaDimensions.ABYDOS)) {
            return;
        }

        AbydosComplexSavedData data = AbydosComplexSavedData.get(level);
        if (data.isInitialized()) {
            generateChunkIfNeeded(level, chunk.getPos(), data);
        }
    }

    private static void generateChunkIfNeeded(ServerLevel level, ChunkPos chunkPos, AbydosComplexSavedData data) {
        if (data.hasGenerated(chunkPos) || !intersectsComplex(chunkPos, data.gateBase(), data.facing())) {
            return;
        }

        generateChunk(level, chunkPos, data.gateBase(), data.facing(), -data.gatePlatformHeight());
        data.markGenerated(chunkPos);
    }

    private static boolean intersectsComplex(ChunkPos chunkPos, BlockPos gateBase, Direction facing) {
        Direction right = facing.getClockWise();
        int[] cornersX = {chunkPos.getMinBlockX(), chunkPos.getMaxBlockX()};
        int[] cornersZ = {chunkPos.getMinBlockZ(), chunkPos.getMaxBlockZ()};
        int minR = Integer.MAX_VALUE;
        int maxR = Integer.MIN_VALUE;
        int minF = Integer.MAX_VALUE;
        int maxF = Integer.MIN_VALUE;
        for (int x : cornersX) {
            for (int z : cornersZ) {
                int dx = x - gateBase.getX();
                int dz = z - gateBase.getZ();
                int r = dx * right.getStepX() + dz * right.getStepZ();
                int f = dx * facing.getStepX() + dz * facing.getStepZ();
                minR = Math.min(minR, r);
                maxR = Math.max(maxR, r);
                minF = Math.min(minF, f);
                maxF = Math.max(maxF, f);
            }
        }
        return maxR >= MIN_R && minR <= MAX_R && maxF >= MIN_F && minF <= MAX_F;
    }

    private static void generateChunk(ServerLevel level, ChunkPos chunkPos, BlockPos gateBase, Direction facing, int baseY) {
        Direction right = facing.getClockWise();
        for (int x = chunkPos.getMinBlockX(); x <= chunkPos.getMaxBlockX(); x++) {
            for (int z = chunkPos.getMinBlockZ(); z <= chunkPos.getMaxBlockZ(); z++) {
                int dx = x - gateBase.getX();
                int dz = z - gateBase.getZ();
                int r = dx * right.getStepX() + dz * right.getStepZ();
                int f = dx * facing.getStepX() + dz * facing.getStepZ();
                generateDesertApronColumn(level, gateBase, x, z, r, f, baseY);
                generatePyramidColumn(level, gateBase, x, z, r, f, baseY);
                generateGateChamberColumn(level, gateBase, x, z, r, f);
                generateGalleryColumn(level, gateBase, x, z, r, f);
                generateEntranceColumn(level, gateBase, x, z, r, f, baseY);
                generateCourtyardColumn(level, gateBase, x, z, r, f, baseY);
                generateCausewayColumn(level, gateBase, x, z, r, f, baseY);
                generateMastabaColumn(level, gateBase, x, z, r, f, -43, 108, baseY);
                generateMastabaColumn(level, gateBase, x, z, r, f, 43, 126, baseY);
                generateObeliskColumn(level, gateBase, x, z, r, f, -16, 82, baseY);
                generateObeliskColumn(level, gateBase, x, z, r, f, 16, 82, baseY);
            }
        }
    }

    private static void generateDesertApronColumn(
            ServerLevel level, BlockPos gateBase, int x, int z, int r, int f, int baseY) {
        int pyramidDistance = Math.max(Math.abs(r), Math.abs(f - PYRAMID_CENTER_F)) - PYRAMID_HALF_WIDTH;
        if (pyramidDistance < -8) {
            pyramidDistance = Integer.MAX_VALUE;
        } else {
            pyramidDistance = Math.max(0, pyramidDistance);
        }

        int frontDistanceR = Math.max(0, Math.abs(r) - 58);
        int frontDistanceF = Math.max(0, Math.max(40 - f, f - 160));
        int frontDistance = Math.max(frontDistanceR, frontDistanceF);
        int distance = Math.min(pyramidDistance, frontDistance);
        if (distance >= TERRAIN_BLEND_RADIUS) {
            return;
        }

        int structureSurfaceY = gateBase.getY() + baseY;
        int naturalGroundY = level.getHeight(Heightmap.Types.OCEAN_FLOOR, x, z) - 1;
        int naturalTopY = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z) - 1;
        BlockPos naturalTop = new BlockPos(x, naturalTopY, z);
        boolean wasWater = !level.getFluidState(naturalTop).isEmpty();
        double blend = distance / (double)TERRAIN_BLEND_RADIUS;
        int desiredY = (int)Math.round(structureSurfaceY + (naturalGroundY - structureSurfaceY) * blend);
        if (wasWater) {
            desiredY = Math.max(desiredY, naturalTopY + 1);
        }

        int clearTop = Math.max(naturalTopY, desiredY);
        for (int y = desiredY + 1; y <= clearTop; y++) {
            clearTerrain(level, new BlockPos(x, y, z));
        }

        int fillBottom = Math.min(naturalGroundY + 1, desiredY - 12);
        for (int y = fillBottom; y <= desiredY; y++) {
            BlockPos pos = new BlockPos(x, y, z);
            BlockState current = level.getBlockState(pos);
            if (y == desiredY) {
                setTerrain(level, pos, Blocks.SAND.defaultBlockState());
            } else if (current.isAir() || !current.getFluidState().isEmpty()) {
                setTerrain(level, pos, Blocks.SANDSTONE.defaultBlockState());
            }
        }
    }

    private static void generatePyramidColumn(ServerLevel level, BlockPos gateBase, int x, int z, int r, int f, int baseY) {
        int distance = Math.max(Math.abs(r), Math.abs(f - PYRAMID_CENTER_F));
        if (distance > PYRAMID_HALF_WIDTH) {
            return;
        }

        int surfaceY = baseY + PYRAMID_HEIGHT - distance;
        int worldSurfaceY = gateBase.getY() + surfaceY;
        int terrainTopY = Math.min(
                level.getMaxBuildHeight() - 1,
                level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z) - 1);
        for (int y = terrainTopY; y > worldSurfaceY; y--) {
            clearTerrain(level, new BlockPos(x, y, z));
        }

        set(level, gateBase, x, z, surfaceY, exteriorPalette(r, f, surfaceY, baseY));
        if (surfaceY > baseY) {
            set(level, gateBase, x, z, surfaceY - 1, exteriorPalette(r, f, surfaceY - 1, baseY));
        }
        if (distance == PYRAMID_HALF_WIDTH) {
            for (int y = baseY - 2; y < surfaceY; y++) {
                set(level, gateBase, x, z, y, Blocks.SANDSTONE.defaultBlockState());
            }
        }
    }

    private static void generateGateChamberColumn(ServerLevel level, BlockPos gateBase, int x, int z, int r, int f) {
        if (Math.abs(r) > 16 || f < -18 || f > 16) {
            return;
        }

        setFloor(level, gateBase, x, z, 0, floorPalette(r, f));
        boolean wall = Math.abs(r) == 16 || f <= -12 || f == 16;
        boolean frontOpening = f == 16 && Math.abs(r) <= 5;
        for (int y = 1; y < 20; y++) {
            if (wall && !(frontOpening && y <= 10)) {
                set(level, gateBase, x, z, y, interiorWall(y));
            } else {
                clearInterior(level, gateBase, x, z, y);
            }
        }
        set(level, gateBase, x, z, 20, interiorWall(20));

        boolean column = (near(r, -12, 1) || near(r, 12, 1))
                && (near(f, -11, 1) || near(f, -3, 1) || near(f, 6, 1) || near(f, 13, 1));
        if (column) {
            for (int y = 0; y <= 12; y++) {
                set(level, gateBase, x, z, y, y == 0 || y >= 10
                        ? Blocks.CHISELED_SANDSTONE.defaultBlockState()
                        : Blocks.SMOOTH_SANDSTONE.defaultBlockState());
            }
        }

        if ((r == -9 || r == 9) && (f == -6 || f == 8)) {
            set(level, gateBase, x, z, 1, ModBlocks.GOAULD_BRAZIER.get().defaultBlockState());
        }
    }

    private static void generateGalleryColumn(ServerLevel level, BlockPos gateBase, int x, int z, int r, int f) {
        if (f < 16 || f > 45 || Math.abs(r) > 7) {
            return;
        }

        set(level, gateBase, x, z, 0, floorPalette(r, f));
        boolean wall = Math.abs(r) == 7;
        for (int y = 1; y < 11; y++) {
            if (wall) {
                set(level, gateBase, x, z, y, interiorWall(y));
            } else {
                clearInterior(level, gateBase, x, z, y);
            }
        }
        set(level, gateBase, x, z, 11, interiorWall(11));

        if ((r == -5 || r == 5) && (f - 20) % 8 == 0) {
            for (int y = 0; y <= 8; y++) {
                set(level, gateBase, x, z, y, y == 0 || y == 8
                        ? Blocks.CHISELED_SANDSTONE.defaultBlockState()
                        : Blocks.SMOOTH_SANDSTONE.defaultBlockState());
            }
        }
        if ((r == -4 || r == 4) && (f == 24 || f == 40)) {
            set(level, gateBase, x, z, 1, ModBlocks.GOAULD_BRAZIER.get().defaultBlockState());
        }
    }

    private static void generateEntranceColumn(ServerLevel level, BlockPos gateBase, int x, int z, int r, int f, int baseY) {
        if (f < 44 || f > 72 || Math.abs(r) > 20) {
            return;
        }

        if (Math.abs(r) <= 10) {
            set(level, gateBase, x, z, 0, floorPalette(r, f));
            for (int y = 1; y < 14; y++) {
                clearInterior(level, gateBase, x, z, y);
            }
            if (Math.abs(r) == 10) {
                for (int y = 0; y <= 13; y++) {
                    set(level, gateBase, x, z, y, interiorWall(y));
                }
            }
            set(level, gateBase, x, z, 14, interiorWall(14));
        }

        boolean pylon = Math.abs(r) >= 11 && Math.abs(r) <= 20 && f >= 50 && f <= 65;
        if (pylon) {
            int height = 16 - Math.max(0, f - 50) / 3;
            for (int y = baseY; y <= height; y++) {
                set(level, gateBase, x, z, y, exteriorPalette(r, f, y, baseY));
            }
        }

        boolean porticoColumn = (near(r, -15, 1) || near(r, -8, 1) || near(r, 8, 1) || near(r, 15, 1))
                && f >= 65 && f <= 67;
        if (porticoColumn) {
            for (int y = baseY; y <= 10; y++) {
                set(level, gateBase, x, z, y, y <= baseY + 1 || y >= 9
                        ? Blocks.CHISELED_SANDSTONE.defaultBlockState()
                        : Blocks.SMOOTH_SANDSTONE.defaultBlockState());
            }
        }
        if (f >= 65 && f <= 68 && Math.abs(r) <= 18) {
            for (int y = 11; y <= 13; y++) {
                set(level, gateBase, x, z, y, interiorWall(y));
            }
        }
    }

    private static void generateCourtyardColumn(ServerLevel level, BlockPos gateBase, int x, int z, int r, int f, int baseY) {
        if (f < 55 || f > 100 || Math.abs(r) > 24) {
            return;
        }

        set(level, gateBase, x, z, baseY, floorPalette(r, f));
        if (Math.abs(r) <= 7) {
            int rampFloor = Math.max(baseY, -Math.max(0, f - 56) / 4);
            set(level, gateBase, x, z, rampFloor, Blocks.SMOOTH_SANDSTONE.defaultBlockState());
            for (int y = rampFloor + 1; y <= rampFloor + 8; y++) {
                clearInterior(level, gateBase, x, z, y);
            }
        }
        if (Math.abs(r) == 8 && f >= 58 && f <= 96) {
            set(level, gateBase, x, z, baseY + 1, Blocks.CUT_SANDSTONE.defaultBlockState());
        }
        if (Math.abs(r) == 24 && f >= 72 && f <= 100 && (f - 72) % 7 <= 1) {
            for (int y = baseY + 1; y <= baseY + 7; y++) {
                set(level, gateBase, x, z, y, Blocks.SMOOTH_SANDSTONE.defaultBlockState());
            }
        }
    }

    private static void generateCausewayColumn(ServerLevel level, BlockPos gateBase, int x, int z, int r, int f, int baseY) {
        if (f < 100 || f > 150 || Math.abs(r) > 8) {
            return;
        }
        set(level, gateBase, x, z, baseY, Math.abs(r) == 8
                ? Blocks.CHISELED_SANDSTONE.defaultBlockState()
                : floorPalette(r, f));
        clearInterior(level, gateBase, x, z, baseY + 1);
        clearInterior(level, gateBase, x, z, baseY + 2);
    }

    private static void generateMastabaColumn(
            ServerLevel level, BlockPos gateBase, int x, int z, int r, int f, int centerR, int centerF, int baseY) {
        int dr = Math.abs(r - centerR);
        int df = Math.abs(f - centerF);
        if (dr > 10 || df > 14) {
            return;
        }
        int inset = Math.max(0, Math.max(dr - 7, df - 11));
        int top = baseY + 7 - inset;
        boolean shell = dr >= 7 - inset || df >= 11 - inset;
        set(level, gateBase, x, z, baseY, Blocks.CUT_SANDSTONE.defaultBlockState());
        for (int y = baseY + 1; y <= top; y++) {
            if (shell || y == top) {
                set(level, gateBase, x, z, y, exteriorPalette(r, f, y, baseY));
            } else {
                clearInterior(level, gateBase, x, z, y);
            }
        }
        if (df == 14 && dr <= 2) {
            for (int y = baseY + 1; y <= baseY + 4; y++) {
                clearInterior(level, gateBase, x, z, y);
            }
        }
    }

    private static void generateObeliskColumn(
            ServerLevel level, BlockPos gateBase, int x, int z, int r, int f, int centerR, int centerF, int baseY) {
        int dr = Math.abs(r - centerR);
        int df = Math.abs(f - centerF);
        int distance = Math.max(dr, df);
        if (distance > 3) {
            return;
        }

        for (int y = baseY + 1; y <= baseY + 21; y++) {
            int relativeY = y - baseY;
            int radius = relativeY <= 3 ? 3 : relativeY <= 17 ? 1 : Math.max(0, 20 - relativeY);
            if (distance <= radius) {
                set(level, gateBase, x, z, y, relativeY == 4 || relativeY == 17
                        ? Blocks.CHISELED_SANDSTONE.defaultBlockState()
                        : Blocks.SMOOTH_SANDSTONE.defaultBlockState());
            }
        }
    }

    private static BlockState exteriorPalette(int r, int f, int y, int baseY) {
        int hash = Math.floorMod(r * 73428767 ^ f * 912931 ^ y * 19349663, 23);
        if (hash == 0 || (y - baseY) % 12 == 0 && hash < 4) {
            return Blocks.CHISELED_SANDSTONE.defaultBlockState();
        }
        if (hash < 7) {
            return Blocks.SANDSTONE.defaultBlockState();
        }
        return hash < 16 ? Blocks.CUT_SANDSTONE.defaultBlockState() : Blocks.SMOOTH_SANDSTONE.defaultBlockState();
    }

    private static BlockState floorPalette(int r, int f) {
        return Math.floorMod(r * 31 + f * 17, 9) == 0
                ? Blocks.CHISELED_SANDSTONE.defaultBlockState()
                : Blocks.SMOOTH_SANDSTONE.defaultBlockState();
    }

    private static BlockState interiorWall(int y) {
        return y % 6 == 0 ? Blocks.CHISELED_SANDSTONE.defaultBlockState() : Blocks.CUT_SANDSTONE.defaultBlockState();
    }

    private static boolean near(int value, int target, int radius) {
        return Math.abs(value - target) <= radius;
    }

    private static void set(ServerLevel level, BlockPos gateBase, int x, int z, int localY, BlockState state) {
        BlockPos pos = new BlockPos(x, gateBase.getY() + localY, z);
        if (!level.getBlockState(pos).equals(state)) {
            level.setBlock(pos, state, UPDATE_FLAGS);
        }
    }

    private static void setFloor(ServerLevel level, BlockPos gateBase, int x, int z, int localY, BlockState state) {
        BlockPos pos = new BlockPos(x, gateBase.getY() + localY, z);
        if (!isGateInstallation(level.getBlockState(pos))) {
            setTerrain(level, pos, state);
        }
    }

    private static void setTerrain(ServerLevel level, BlockPos pos, BlockState state) {
        if (!level.getBlockState(pos).equals(state)) {
            level.setBlock(pos, state, UPDATE_FLAGS);
        }
    }

    private static void clearTerrain(ServerLevel level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (!state.isAir() && !isGateInstallation(state) && !state.is(Blocks.BEDROCK)) {
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), UPDATE_FLAGS);
        }
    }

    private static void clearInterior(ServerLevel level, BlockPos gateBase, int x, int z, int localY) {
        BlockPos pos = new BlockPos(x, gateBase.getY() + localY, z);
        BlockState state = level.getBlockState(pos);
        if (!state.isAir() && !isGateInstallation(state) && !state.is(Blocks.BEDROCK)) {
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), UPDATE_FLAGS);
        }
    }

    private static boolean isGateInstallation(BlockState state) {
        return state.getBlock() instanceof StargateBaseBlock
                || state.getBlock() instanceof StargateComponentBlock
                || state.getBlock() instanceof DhdBlock;
    }

    private static void clearLegacyPyramid(ServerLevel level, BlockPos gateBase, Direction facing) {
        Direction right = facing.getClockWise();
        BlockPos oldCenter = gateBase.relative(facing.getOpposite(), 18);
        for (int layer = 0; layer < 5; layer++) {
            int radius = 5 - layer;
            for (int r = -radius; r <= radius; r++) {
                for (int f = -radius; f <= radius; f++) {
                    if (Math.abs(r) != radius && Math.abs(f) != radius && layer != 4) {
                        continue;
                    }
                    BlockPos pos = oldCenter.relative(right, r).relative(facing, f).above(layer);
                    BlockState state = level.getBlockState(pos);
                    if (state.is(Blocks.CUT_SANDSTONE)) {
                        level.setBlock(pos, Blocks.AIR.defaultBlockState(), UPDATE_FLAGS);
                    }
                }
            }
        }
    }
}
