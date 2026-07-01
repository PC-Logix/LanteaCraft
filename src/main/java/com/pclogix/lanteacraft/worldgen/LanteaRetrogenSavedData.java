package com.pclogix.lanteacraft.worldgen;

import com.pclogix.lanteacraft.LanteaCraft;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.saveddata.SavedData;

public class LanteaRetrogenSavedData extends SavedData {
    private static final String DATA_NAME = LanteaCraft.MODID + "_retrogen";
    private static final SavedData.Factory<LanteaRetrogenSavedData> FACTORY = new SavedData.Factory<>(
            LanteaRetrogenSavedData::new,
            LanteaRetrogenSavedData::load);

    private final Set<Long> oreRetrogenChunks = new HashSet<>();

    public static LanteaRetrogenSavedData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(FACTORY, DATA_NAME);
    }

    public static LanteaRetrogenSavedData load(CompoundTag tag, HolderLookup.Provider registries) {
        LanteaRetrogenSavedData data = new LanteaRetrogenSavedData();
        for (long chunk : tag.getLongArray("oreRetrogenChunks")) {
            data.oreRetrogenChunks.add(chunk);
        }
        return data;
    }

    public boolean hasProcessedOres(ChunkPos chunkPos) {
        return oreRetrogenChunks.contains(chunkPos.toLong());
    }

    public void markOresProcessed(ChunkPos chunkPos) {
        if (oreRetrogenChunks.add(chunkPos.toLong())) {
            setDirty();
        }
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        tag.putLongArray("oreRetrogenChunks", oreRetrogenChunks.stream().mapToLong(Long::longValue).toArray());
        return tag;
    }
}
