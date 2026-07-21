package com.pclogix.lanteacraft.worldgen;

import com.pclogix.lanteacraft.Config;
import com.pclogix.lanteacraft.LanteaCraft;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.neoforged.neoforge.event.level.ChunkEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

public final class LanteaRetrogen {
    private static final List<ResourceKey<PlacedFeature>> ORE_FEATURES = List.of(
            placedFeature("ore_naquadah"),
            placedFeature("ore_trinium"));

    private static final Map<ResourceKey<Level>, ArrayDeque<Long>> PENDING_CHUNKS = new HashMap<>();
    private static final Map<ResourceKey<Level>, Set<Long>> QUEUED_CHUNKS = new HashMap<>();

    private LanteaRetrogen() {
    }

    public static void onChunkLoad(ChunkEvent.Load event) {
        if (!Config.ENABLE_ORE_RETROGEN.getAsBoolean() || event.isNewChunk() || !(event.getLevel() instanceof ServerLevel level)) {
            return;
        }

        ChunkPos chunkPos = event.getChunk().getPos();
        if (LanteaRetrogenSavedData.get(level).hasProcessedOres(chunkPos)) {
            return;
        }

        enqueue(level.dimension(), chunkPos.toLong());
    }

    public static void onLevelTick(LevelTickEvent.Post event) {
        if (!Config.ENABLE_ORE_RETROGEN.getAsBoolean() || !(event.getLevel() instanceof ServerLevel level)) {
            return;
        }

        ArrayDeque<Long> pending = PENDING_CHUNKS.get(level.dimension());
        if (pending == null || pending.isEmpty()) {
            return;
        }

        Set<Long> queued = QUEUED_CHUNKS.computeIfAbsent(level.dimension(), key -> new HashSet<>());
        LanteaRetrogenSavedData data = LanteaRetrogenSavedData.get(level);
        int budget = Config.ORE_RETROGEN_CHUNKS_PER_TICK.get();
        while (budget-- > 0 && !pending.isEmpty()) {
            long chunkLong = pending.removeFirst();
            queued.remove(chunkLong);

            ChunkPos chunkPos = new ChunkPos(chunkLong);
            if (data.hasProcessedOres(chunkPos)) {
                continue;
            }

            LevelChunk chunk = level.getChunkSource().getChunkNow(chunkPos.x, chunkPos.z);
            if (chunk == null) {
                enqueue(level.dimension(), chunkLong);
                continue;
            }

            boolean placedOre = runOreRetrogen(level, chunkPos);
            if (placedOre) {
                chunk.setUnsaved(true);
            }
            data.markOresProcessed(chunkPos);
        }
    }

    private static boolean runOreRetrogen(ServerLevel level, ChunkPos chunkPos) {
        BlockPos origin = chunkPos.getWorldPosition();
        RandomSource random = RandomSource.create(retrogenSeed(level, chunkPos));
        boolean placedOre = false;

        for (ResourceKey<PlacedFeature> featureKey : ORE_FEATURES) {
            Optional<Holder.Reference<PlacedFeature>> feature = level.registryAccess()
                    .lookupOrThrow(Registries.PLACED_FEATURE)
                    .get(featureKey);
            if (feature.isEmpty()) {
                LanteaCraft.LOGGER.warn("Skipping missing LanteaCraft retrogen feature {}.", featureKey.location());
                continue;
            }

            boolean featurePlaced = feature.get().value().placeWithBiomeCheck(
                    level, level.getChunkSource().getGenerator(), random, origin);
            placedOre |= featurePlaced;
            if (Config.DEBUG_LOGGING.getAsBoolean()) {
                LanteaCraft.LOGGER.info("LanteaCraft retrogen feature {} in chunk {}: placed={}",
                        featureKey.location(), chunkPos, featurePlaced);
            }
        }

        if (Config.DEBUG_LOGGING.getAsBoolean()) {
            LanteaCraft.LOGGER.info("Finished LanteaCraft ore retrogen for chunk {} in {}: placedAny={}",
                    chunkPos, level.dimension().location(), placedOre);
        }
        return placedOre;
    }

    private static void enqueue(ResourceKey<Level> dimension, long chunk) {
        Set<Long> queued = QUEUED_CHUNKS.computeIfAbsent(dimension, key -> new HashSet<>());
        if (queued.add(chunk)) {
            PENDING_CHUNKS.computeIfAbsent(dimension, key -> new ArrayDeque<>()).addLast(chunk);
        }
    }

    private static long retrogenSeed(ServerLevel level, ChunkPos chunkPos) {
        long seed = level.getSeed() ^ 0x4C414E5445415247L;
        seed ^= (long)chunkPos.x * 341873128712L;
        seed ^= (long)chunkPos.z * 132897987541L;
        return seed;
    }

    private static ResourceKey<PlacedFeature> placedFeature(String path) {
        return ResourceKey.create(Registries.PLACED_FEATURE, ResourceLocation.fromNamespaceAndPath(LanteaCraft.MODID, path));
    }
}
