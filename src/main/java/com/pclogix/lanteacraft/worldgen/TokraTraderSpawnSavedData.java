package com.pclogix.lanteacraft.worldgen;

import com.pclogix.lanteacraft.LanteaCraft;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

public class TokraTraderSpawnSavedData extends SavedData {
    private static final String DATA_NAME = LanteaCraft.MODID + "_tokra_trader_spawns";
    private static final SavedData.Factory<TokraTraderSpawnSavedData> FACTORY = new SavedData.Factory<>(
            TokraTraderSpawnSavedData::new,
            TokraTraderSpawnSavedData::load);

    private final Set<String> gateTraderVillages = new HashSet<>();
    private final Set<String> chanceTraderVillages = new HashSet<>();

    public static TokraTraderSpawnSavedData get(ServerLevel level) {
        return level.getServer().overworld().getDataStorage().computeIfAbsent(FACTORY, DATA_NAME);
    }

    public static TokraTraderSpawnSavedData load(CompoundTag tag, HolderLookup.Provider registries) {
        TokraTraderSpawnSavedData data = new TokraTraderSpawnSavedData();
        loadSet(tag.getList("gateTraderVillages", Tag.TAG_STRING), data.gateTraderVillages);
        loadSet(tag.getList("chanceTraderVillages", Tag.TAG_STRING), data.chanceTraderVillages);
        return data;
    }

    public boolean hasGateTrader(ServerLevel level, BlockPos villagePos) {
        return gateTraderVillages.contains(villageKey(level.dimension().location(), villagePos));
    }

    public boolean hasChanceTrader(ServerLevel level, BlockPos villagePos) {
        return chanceTraderVillages.contains(villageKey(level.dimension().location(), villagePos));
    }

    public void markGateTrader(ServerLevel level, BlockPos villagePos) {
        if (gateTraderVillages.add(villageKey(level.dimension().location(), villagePos))) {
            setDirty();
        }
    }

    public void markChanceTrader(ServerLevel level, BlockPos villagePos) {
        if (chanceTraderVillages.add(villageKey(level.dimension().location(), villagePos))) {
            setDirty();
        }
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        tag.put("gateTraderVillages", saveSet(gateTraderVillages));
        tag.put("chanceTraderVillages", saveSet(chanceTraderVillages));
        return tag;
    }

    private static void loadSet(ListTag list, Set<String> values) {
        for (int i = 0; i < list.size(); i++) {
            values.add(list.getString(i));
        }
    }

    private static ListTag saveSet(Set<String> values) {
        ListTag list = new ListTag();
        values.stream().sorted().map(StringTag::valueOf).forEach(list::add);
        return list;
    }

    private static String villageKey(ResourceLocation dimension, BlockPos villagePos) {
        return dimension + "@" + villagePos.getX() + "," + villagePos.getZ();
    }
}
