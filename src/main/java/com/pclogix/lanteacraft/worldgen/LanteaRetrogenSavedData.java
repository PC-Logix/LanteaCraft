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
    private static final int DATA_VERSION = 1;
    private static final SavedData.Factory<LanteaRetrogenSavedData> FACTORY = new SavedData.Factory<>(
            LanteaRetrogenSavedData::new,
            LanteaRetrogenSavedData::load);

    private final Set<Long> oreRetrogenChunks = new HashSet<>();

    public static LanteaRetrogenSavedData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(FACTORY, DATA_NAME);
    }

    public static LanteaRetrogenSavedData load(CompoundTag tag, HolderLookup.Provider registries) {
        LanteaRetrogenSavedData data = new LanteaRetrogenSavedData();
        if (tag.getInt("dataVersion") < DATA_VERSION) {
            // Version 0 marked chunks processed even though the misplaced biome modifier
            // caused every biome-checked ore placement to fail. Clear those stale markers
            // once so corrected retrogen can revisit the chunks on their next load.
            data.setDirty();
            return data;
        }
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
        tag.putInt("dataVersion", DATA_VERSION);
        tag.putLongArray("oreRetrogenChunks", oreRetrogenChunks.stream().mapToLong(Long::longValue).toArray());
        return tag;
    }
}
