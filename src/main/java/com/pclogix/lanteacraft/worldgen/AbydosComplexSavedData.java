package com.pclogix.lanteacraft.worldgen;

import com.pclogix.lanteacraft.LanteaCraft;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.saveddata.SavedData;

public final class AbydosComplexSavedData extends SavedData {
    private static final String DATA_NAME = LanteaCraft.MODID + "_abydos_complex";
    private static final int COMPLEX_VERSION = 1;
    private static final SavedData.Factory<AbydosComplexSavedData> FACTORY = new SavedData.Factory<>(
            AbydosComplexSavedData::new,
            AbydosComplexSavedData::load);

    private final Set<Long> generatedChunks = new HashSet<>();
    private BlockPos gateBase;
    private Direction facing = Direction.SOUTH;

    public static AbydosComplexSavedData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(FACTORY, DATA_NAME);
    }

    public static AbydosComplexSavedData load(CompoundTag tag, HolderLookup.Provider registries) {
        AbydosComplexSavedData data = new AbydosComplexSavedData();
        if (tag.getBoolean("initialized")) {
            data.gateBase = new BlockPos(tag.getInt("gateX"), tag.getInt("gateY"), tag.getInt("gateZ"));
            Direction loadedFacing = Direction.byName(tag.getString("facing"));
            if (loadedFacing != null && loadedFacing.getAxis().isHorizontal()) {
                data.facing = loadedFacing;
            }
        }
        if (tag.getInt("complexVersion") == COMPLEX_VERSION) {
            for (long chunk : tag.getLongArray("generatedChunks")) {
                data.generatedChunks.add(chunk);
            }
        } else {
            data.setDirty();
        }
        return data;
    }

    public boolean initialize(BlockPos basePos, Direction gateFacing) {
        BlockPos immutableBase = basePos.immutable();
        if (immutableBase.equals(gateBase) && gateFacing == facing) {
            return false;
        }

        gateBase = immutableBase;
        facing = gateFacing;
        generatedChunks.clear();
        setDirty();
        return true;
    }

    public boolean isInitialized() {
        return gateBase != null;
    }

    public BlockPos gateBase() {
        return gateBase;
    }

    public Direction facing() {
        return facing;
    }

    public boolean hasGenerated(ChunkPos chunkPos) {
        return generatedChunks.contains(chunkPos.toLong());
    }

    public void markGenerated(ChunkPos chunkPos) {
        if (generatedChunks.add(chunkPos.toLong())) {
            setDirty();
        }
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        tag.putInt("complexVersion", COMPLEX_VERSION);
        tag.putBoolean("initialized", isInitialized());
        if (gateBase != null) {
            tag.putInt("gateX", gateBase.getX());
            tag.putInt("gateY", gateBase.getY());
            tag.putInt("gateZ", gateBase.getZ());
            tag.putString("facing", facing.getName());
        }
        tag.putLongArray("generatedChunks", generatedChunks.stream().mapToLong(Long::longValue).toArray());
        return tag;
    }
}
