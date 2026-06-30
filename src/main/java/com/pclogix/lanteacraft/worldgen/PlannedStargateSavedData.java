package com.pclogix.lanteacraft.worldgen;

import com.pclogix.lanteacraft.LanteaCraft;
import com.pclogix.lanteacraft.gate.StargateVariant;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

public class PlannedStargateSavedData extends SavedData {
    private static final String DATA_NAME = LanteaCraft.MODID + "_planned_stargates";
    private static final SavedData.Factory<PlannedStargateSavedData> FACTORY = new SavedData.Factory<>(
            PlannedStargateSavedData::new,
            PlannedStargateSavedData::load);

    private final Map<String, PlannedStargate> plansByAddress = new LinkedHashMap<>();
    private final Map<String, String> addressesByVillage = new LinkedHashMap<>();
    private final Map<String, Boolean> placedByAddress = new LinkedHashMap<>();

    public static PlannedStargateSavedData get(ServerLevel level) {
        return level.getServer().overworld().getDataStorage().computeIfAbsent(FACTORY, DATA_NAME);
    }

    public static PlannedStargateSavedData load(CompoundTag tag, HolderLookup.Provider registries) {
        PlannedStargateSavedData data = new PlannedStargateSavedData();
        ListTag plans = tag.getList("plans", Tag.TAG_COMPOUND);
        for (int i = 0; i < plans.size(); i++) {
            CompoundTag planTag = plans.getCompound(i);
            ResourceLocation dimension = ResourceLocation.parse(planTag.getString("dimension"));
            BlockPos villagePos = new BlockPos(planTag.getInt("villageX"), planTag.getInt("villageY"), planTag.getInt("villageZ"));
            BlockPos basePos = new BlockPos(planTag.getInt("baseX"), planTag.getInt("baseY"), planTag.getInt("baseZ"));
            Direction facing = Direction.byName(planTag.getString("facing"));
            if (facing == null) {
                facing = Direction.NORTH;
            }
            StargateVariant variant = StargateVariant.valueOf(planTag.getString("variant"));
            PlannedStargate plan = new PlannedStargate(
                    planTag.getString("address"),
                    dimension,
                    villagePos,
                    basePos,
                    facing,
                    variant);
            data.put(plan);
            data.placedByAddress.put(plan.address(), planTag.getBoolean("placed"));
        }
        return data;
    }

    public PlannedStargate remember(PlannedStargate plan) {
        PlannedStargate existing = plansByAddress.get(plan.address());
        if (existing != null) {
            return existing;
        }

        put(plan);
        placedByAddress.putIfAbsent(plan.address(), false);
        setDirty();
        return plan;
    }

    public Optional<PlannedStargate> findByAddress(String address) {
        return Optional.ofNullable(plansByAddress.get(address == null ? "" : address.trim().toUpperCase()));
    }

    public Optional<PlannedStargate> findByVillage(ServerLevel level, BlockPos villagePos) {
        String address = addressesByVillage.get(villageKey(level.dimension().location(), villagePos));
        return address == null ? Optional.empty() : findByAddress(address);
    }

    public Optional<PlannedStargate> nearest(ServerLevel level, BlockPos origin) {
        return plansByAddress.values().stream()
                .filter(plan -> plan.dimension().equals(level.dimension().location()))
                .min(Comparator.comparingDouble(plan -> plan.basePos().distSqr(origin)));
    }

    public List<PlannedStargate> plans(ServerLevel level) {
        return plansByAddress.values().stream()
                .filter(plan -> plan.dimension().equals(level.dimension().location()))
                .sorted(Comparator.comparing(PlannedStargate::address))
                .toList();
    }

    public boolean isPlaced(String address) {
        return placedByAddress.getOrDefault(address, false);
    }

    public void markPlaced(String address) {
        placedByAddress.put(address, true);
        setDirty();
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        ListTag plans = new ListTag();
        for (PlannedStargate plan : plansByAddress.values()) {
            CompoundTag planTag = new CompoundTag();
            planTag.putString("address", plan.address());
            planTag.putString("dimension", plan.dimension().toString());
            planTag.putInt("villageX", plan.villagePos().getX());
            planTag.putInt("villageY", plan.villagePos().getY());
            planTag.putInt("villageZ", plan.villagePos().getZ());
            planTag.putInt("baseX", plan.basePos().getX());
            planTag.putInt("baseY", plan.basePos().getY());
            planTag.putInt("baseZ", plan.basePos().getZ());
            planTag.putString("facing", plan.facing().getName());
            planTag.putString("variant", plan.variant().name());
            planTag.putBoolean("placed", isPlaced(plan.address()));
            plans.add(planTag);
        }
        tag.put("plans", plans);
        return tag;
    }

    private void put(PlannedStargate plan) {
        plansByAddress.put(plan.address(), plan);
        addressesByVillage.put(villageKey(plan.dimension(), plan.villagePos()), plan.address());
    }

    private static String villageKey(ResourceLocation dimension, BlockPos villagePos) {
        return dimension + "@" + villagePos.getX() + "," + villagePos.getZ();
    }
}
