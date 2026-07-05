package com.pclogix.lanteacraft.worldgen;

import com.pclogix.lanteacraft.entity.TokraTraderEntity;
import com.pclogix.lanteacraft.registry.ModEntities;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.event.level.ChunkEvent;

public final class TokraTraderSpawner {
    private static final int ANY_VILLAGE_CHANCE_ONE_IN = 4;
    private static final int VILLAGE_SCAN_RADIUS_CHUNKS = 3;
    private static final int MAX_VILLAGE_CENTER_DISTANCE = 80;

    private TokraTraderSpawner() {
    }

    public static void spawnForGate(ServerLevel level, PlannedStargate plan, BlockPos gateBasePos) {
        TokraTraderSpawnSavedData data = TokraTraderSpawnSavedData.get(level);
        if (data.hasGateTrader(level, plan.villagePos())) {
            return;
        }

        BlockPos spawnPos = surfacePos(level, gateBasePos.relative(plan.facing(), 6));
        if (spawn(level, spawnPos, plan.villagePos(), MobSpawnType.STRUCTURE)) {
            data.markGateTrader(level, plan.villagePos());
        }
    }

    public static void onChunkLoad(ChunkEvent.Load event) {
        LevelAccessor levelAccessor = event.getLevel();
        if (!event.isNewChunk() || !(levelAccessor instanceof ServerLevel level) || !(event.getChunk() instanceof LevelChunk chunk)) {
            return;
        }

        ChunkPos chunkPos = chunk.getPos();
        BlockPos origin = chunkPos.getWorldPosition().offset(8, 0, 8);
        Optional<BlockPos> village = StargateVillageLocator.nearestVillage(level, origin, VILLAGE_SCAN_RADIUS_CHUNKS, false);
        if (village.isEmpty() || village.get().distSqr(origin) > MAX_VILLAGE_CENTER_DISTANCE * MAX_VILLAGE_CENTER_DISTANCE) {
            return;
        }

        TokraTraderSpawnSavedData data = TokraTraderSpawnSavedData.get(level);
        BlockPos villagePos = village.get();
        if (data.hasChanceTrader(level, villagePos)) {
            return;
        }

        data.markChanceTrader(level, villagePos);
        if (Math.floorMod(mix(level.getSeed(), villagePos.getX(), villagePos.getZ()), ANY_VILLAGE_CHANCE_ONE_IN) != 0) {
            return;
        }

        spawn(level, surfacePos(level, villagePos), villagePos, MobSpawnType.STRUCTURE);
    }

    private static boolean spawn(ServerLevel level, BlockPos spawnPos, BlockPos villagePos, MobSpawnType spawnType) {
        if (!level.getEntitiesOfClass(TokraTraderEntity.class, new AABB(spawnPos).inflate(24.0D)).isEmpty()) {
            return false;
        }

        TokraTraderEntity trader = ModEntities.TOKRA_TRADER.get().create(level);
        if (trader == null) {
            return false;
        }

        trader.moveTo(spawnPos.getX() + 0.5D, spawnPos.getY(), spawnPos.getZ() + 0.5D, level.random.nextFloat() * 360.0F, 0.0F);
        trader.finalizeSpawn(level, level.getCurrentDifficultyAt(spawnPos), spawnType, null);
        trader.restrictTo(villagePos, 48);
        trader.setPersistenceRequired();
        return level.addFreshEntity(trader);
    }

    private static BlockPos surfacePos(ServerLevel level, BlockPos pos) {
        int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, pos.getX(), pos.getZ());
        BlockPos spawnPos = new BlockPos(pos.getX(), y, pos.getZ());
        if (!level.getBlockState(spawnPos.below()).isAir()) {
            return spawnPos;
        }

        return new BlockPos(pos.getX(), Math.max(level.getMinBuildHeight() + 1, pos.getY()), pos.getZ());
    }

    private static int mix(long seed, int x, int z) {
        long state = seed ^ 0x544F4B524154524CL;
        state ^= (long)x * 0x9E3779B97F4A7C15L;
        state = Long.rotateLeft(state, 27);
        state ^= (long)z * 0xC2B2AE3D27D4EB4FL;
        state ^= state >>> 33;
        state *= 0xff51afd7ed558ccdL;
        state ^= state >>> 33;
        return (int)state;
    }
}
