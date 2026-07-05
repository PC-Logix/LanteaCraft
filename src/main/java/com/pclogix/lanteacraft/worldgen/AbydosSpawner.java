package com.pclogix.lanteacraft.worldgen;

import com.pclogix.lanteacraft.entity.GoauldSoldierEntity;
import com.pclogix.lanteacraft.registry.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.event.level.ChunkEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

public final class AbydosSpawner {
    private static final int CHECK_INTERVAL_TICKS = 40;
    private static final int SPAWN_CHANCE_ONE_IN = 2;
    private static final int MIN_PLAYER_DISTANCE = 32;
    private static final int MAX_PLAYER_DISTANCE = 72;
    private static final int LOCAL_CAP_RADIUS = 128;
    private static final int LOCAL_CAP = 14;
    private static final int PATROL_MIN_SIZE = 1;
    private static final int PATROL_MAX_SIZE = 3;
    private static final int CHUNK_SPAWN_CHANCE_ONE_IN = 5;
    private static final int CHUNK_LOCAL_CAP_RADIUS = 64;
    private static final int CHUNK_LOCAL_CAP = 6;

    private AbydosSpawner() {
    }

    public static void onLevelTick(LevelTickEvent.Post event) {
        Level tickLevel = event.getLevel();
        if (!(tickLevel instanceof ServerLevel level)
                || !level.dimension().equals(LanteaDimensions.ABYDOS)
                || level.getDifficulty() == Difficulty.PEACEFUL
                || level.getGameTime() % CHECK_INTERVAL_TICKS != 0L) {
            return;
        }

        for (ServerPlayer player : level.players()) {
            trySpawnNear(level, player);
        }
    }

    public static void onChunkLoad(ChunkEvent.Load event) {
        LevelAccessor levelAccessor = event.getLevel();
        if (!event.isNewChunk()
                || !(levelAccessor instanceof ServerLevel level)
                || !(event.getChunk() instanceof LevelChunk chunk)
                || !level.dimension().equals(LanteaDimensions.ABYDOS)
                || level.getDifficulty() == Difficulty.PEACEFUL
                || level.random.nextInt(CHUNK_SPAWN_CHANCE_ONE_IN) != 0) {
            return;
        }

        ChunkPos chunkPos = chunk.getPos();
        BlockPos center = chunkPos.getMiddleBlockPosition(0);
        if (level.getEntitiesOfClass(GoauldSoldierEntity.class, new AABB(center).inflate(CHUNK_LOCAL_CAP_RADIUS)).size() >= CHUNK_LOCAL_CAP) {
            return;
        }

        int groupSize = 1 + level.random.nextInt(2);
        for (int i = 0; i < groupSize; i++) {
            int x = chunkPos.getMinBlockX() + level.random.nextInt(16);
            int z = chunkPos.getMinBlockZ() + level.random.nextInt(16);
            int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
            spawn(level, new BlockPos(x, y, z), MobSpawnType.NATURAL);
        }
    }

    private static void trySpawnNear(ServerLevel level, ServerPlayer player) {
        if (level.random.nextInt(SPAWN_CHANCE_ONE_IN) != 0) {
            return;
        }
        if (level.getEntitiesOfClass(GoauldSoldierEntity.class, new AABB(player.blockPosition()).inflate(LOCAL_CAP_RADIUS)).size() >= LOCAL_CAP) {
            return;
        }

        int groupSize = PATROL_MIN_SIZE + level.random.nextInt(PATROL_MAX_SIZE - PATROL_MIN_SIZE + 1);
        int spawned = 0;
        for (int attempt = 0; attempt < 16 && spawned < groupSize; attempt++) {
            double angle = level.random.nextDouble() * Math.TAU;
            int distance = MIN_PLAYER_DISTANCE + level.random.nextInt(MAX_PLAYER_DISTANCE - MIN_PLAYER_DISTANCE + 1);
            int x = player.getBlockX() + (int)Math.round(Math.cos(angle) * distance);
            int z = player.getBlockZ() + (int)Math.round(Math.sin(angle) * distance);
            int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
            BlockPos pos = new BlockPos(x, y, z);
            if (spawn(level, pos, MobSpawnType.PATROL)) {
                spawned++;
            }
        }
    }

    private static boolean spawn(ServerLevel level, BlockPos pos, MobSpawnType spawnType) {
        if (!canStandAt(level, pos)) {
            return false;
        }

        GoauldSoldierEntity soldier = ModEntities.GOAULD_SOLDIER.get().create(level);
        if (soldier == null) {
            return false;
        }

        soldier.moveTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, level.random.nextFloat() * 360.0F, 0.0F);
        soldier.finalizeSpawn(level, level.getCurrentDifficultyAt(pos), spawnType, null);
        return level.addFreshEntity(soldier);
    }

    private static boolean canStandAt(ServerLevel level, BlockPos pos) {
        BlockState below = level.getBlockState(pos.below());
        return below.isFaceSturdy(level, pos.below(), net.minecraft.core.Direction.UP)
                && level.getBlockState(pos).isAir()
                && level.getBlockState(pos.above()).isAir()
                && level.getWorldBorder().isWithinBounds(pos);
    }
}
