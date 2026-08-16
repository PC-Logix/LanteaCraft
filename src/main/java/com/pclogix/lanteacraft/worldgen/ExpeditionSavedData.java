package com.pclogix.lanteacraft.worldgen;

import com.pclogix.lanteacraft.LanteaCraft;
import com.pclogix.lanteacraft.gate.StargateAddress;
import com.pclogix.lanteacraft.gate.StargateVariant;
import java.util.Comparator;
import java.util.ArrayList;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;

public class ExpeditionSavedData extends SavedData {
    private static final String DATA_NAME = LanteaCraft.MODID + "_expeditions";
    private static final SavedData.Factory<ExpeditionSavedData> FACTORY = new SavedData.Factory<>(
            ExpeditionSavedData::new,
            ExpeditionSavedData::load);
    private static final int SLOT_SPACING = 4096;
    private static final int BASE_Y = 80;

    private final Map<String, ExpeditionInstance> byAddress = new LinkedHashMap<>();

    public static ExpeditionSavedData get(ServerLevel level) {
        return level.getServer().overworld().getDataStorage().computeIfAbsent(FACTORY, DATA_NAME);
    }

    public static ExpeditionSavedData load(CompoundTag tag, HolderLookup.Provider registries) {
        ExpeditionSavedData data = new ExpeditionSavedData();
        boolean migratedVariant = false;
        ListTag expeditions = tag.getList("expeditions", Tag.TAG_COMPOUND);
        for (int i = 0; i < expeditions.size(); i++) {
            CompoundTag expeditionTag = expeditions.getCompound(i);
            Direction facing = Direction.byName(expeditionTag.getString("facing"));
            if (facing == null) {
                facing = Direction.SOUTH;
            }
            // Expeditions are always Milky Way gates.  Keep reading old records, but
            // normalize their persisted variant so the original Pegasus default is
            // migrated when the world is loaded.
            if (!expeditionTag.contains("variant") || !"MILKY_WAY".equals(expeditionTag.getString("variant"))) {
                migratedVariant = true;
            }
            StargateVariant variant = StargateVariant.MILKY_WAY;
            ExpeditionInstance expedition = new ExpeditionInstance(
                    normalize(expeditionTag.getString("address")),
                    expeditionTag.getInt("slot"),
                    expeditionTag.getInt("tier"),
                    new BlockPos(expeditionTag.getInt("x"), expeditionTag.getInt("y"), expeditionTag.getInt("z")),
                    expeditionTag.contains("dimension")
                            ? ResourceLocation.parse(expeditionTag.getString("dimension"))
                            : legacyDimension(expeditionTag),
                    facing,
                    variant,
                    expeditionTag.getBoolean("generated"),
                    expeditionTag.getBoolean("rewardClaimed"),
                    normalize(expeditionTag.getString("returnAddress")),
                    loadCombatRoomCenters(expeditionTag),
                    loadRewardUnlocked(expeditionTag),
                    expeditionTag.contains("layoutSeed") ? expeditionTag.getLong("layoutSeed") : expeditionTag.getInt("slot"),
                    loadRewardDoors(expeditionTag),
                    loadRewardDoorPos(expeditionTag),
                    loadRewardDoorFacing(expeditionTag));
            if (!expedition.address().isBlank()) {
                data.byAddress.put(expedition.address(), expedition);
            }
        }
        if (migratedVariant) {
            data.setDirty();
        }
        return data;
    }

    public Optional<ExpeditionInstance> findByAddress(String address) {
        return Optional.ofNullable(byAddress.get(normalize(address)));
    }

    public ExpeditionInstance discover(long seed, BlockPos origin) {
        int slot = nextSlot();
        int tier = 1 + Math.floorMod(mix(seed ^ origin.asLong(), slot), 3);
        String address = uniqueAddress(addressFor(seed, origin, slot));
        ExpeditionInstance expedition = new ExpeditionInstance(
                address,
                slot,
                tier,
                basePosForSlot(slot),
                LanteaDimensions.EXPEDITIONS.location(),
                Direction.SOUTH,
                StargateVariant.MILKY_WAY,
                false,
                false,
                "",
                List.of(),
                false,
                seed ^ origin.asLong() ^ slot,
                List.of(),
                null,
                Direction.SOUTH);
        byAddress.put(expedition.address(), expedition);
        setDirty();
        return expedition;
    }

    public List<ExpeditionInstance> expeditions() {
        return byAddress.values().stream()
                .sorted(Comparator.comparing(ExpeditionInstance::address))
                .toList();
    }

    public void rememberForPlacement(ExpeditionInstance expedition) {
        byAddress.put(expedition.address(), expedition);
        setDirty();
    }

    public void forget(String expeditionAddress) {
        if (byAddress.remove(normalize(expeditionAddress)) != null) {
            setDirty();
        }
    }

    public void markGenerated(ExpeditionInstance expedition, BlockPos basePos) {
        markGenerated(expedition, basePos, expedition.facing());
    }

    public void markGenerated(ExpeditionInstance expedition, BlockPos basePos, Direction facing) {
        ExpeditionInstance current = byAddress.getOrDefault(expedition.address(), expedition);
        ExpeditionInstance updated = new ExpeditionInstance(
                current.address(),
                current.slot(),
                current.tier(),
                basePos.immutable(),
                current.dimension(),
                facing,
                current.variant(),
                true,
                current.rewardClaimed(),
                current.returnAddress(),
                current.combatRoomCenters(),
                current.rewardUnlocked(),
                current.layoutSeed(),
                current.rewardDoors(),
                current.rewardDoorPos(),
                current.rewardDoorFacing());
        byAddress.put(updated.address(), updated);
        setDirty();
    }

    public void rememberTrialState(String expeditionAddress, List<BlockPos> combatRoomCenters, boolean rewardUnlocked) {
        findByAddress(expeditionAddress).ifPresent(expedition -> {
            byAddress.put(expedition.address(), expedition.withTrialState(combatRoomCenters, rewardUnlocked));
            setDirty();
        });
    }

    public void markRewardUnlocked(String expeditionAddress) {
        findByAddress(expeditionAddress).ifPresent(expedition -> {
            byAddress.put(expedition.address(), expedition.withTrialState(expedition.combatRoomCenters(), true));
            setDirty();
        });
    }

    public void markRewardLocked(String expeditionAddress) {
        findByAddress(expeditionAddress).ifPresent(expedition -> {
            byAddress.put(expedition.address(), expedition.withTrialState(expedition.combatRoomCenters(), false));
            setDirty();
        });
    }

    public void rememberRewardDoor(String expeditionAddress, BlockPos rewardDoorPos, Direction rewardDoorFacing) {
        findByAddress(expeditionAddress).ifPresent(expedition -> {
            List<ExpeditionRewardDoor> doors = new ArrayList<>(expedition.rewardDoors());
            ExpeditionRewardDoor door = new ExpeditionRewardDoor(rewardDoorPos, rewardDoorFacing);
            if (!doors.contains(door)) {
                doors.add(door);
            }
            byAddress.put(expedition.address(), expedition.withRewardDoors(doors));
            setDirty();
        });
    }

    public void rememberReturnAddress(String expeditionAddress, String returnAddress) {
        findByAddress(expeditionAddress).ifPresent(expedition -> {
            byAddress.put(expedition.address(), expedition.withReturnAddress(returnAddress));
            setDirty();
        });
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        ListTag expeditions = new ListTag();
        for (ExpeditionInstance expedition : byAddress.values()) {
            CompoundTag expeditionTag = new CompoundTag();
            expeditionTag.putString("address", expedition.address());
            expeditionTag.putInt("slot", expedition.slot());
            expeditionTag.putInt("tier", expedition.tier());
            expeditionTag.putInt("x", expedition.basePos().getX());
            expeditionTag.putInt("y", expedition.basePos().getY());
            expeditionTag.putInt("z", expedition.basePos().getZ());
            expeditionTag.putString("dimension", expedition.dimension().toString());
            expeditionTag.putString("facing", expedition.facing().getName());
            expeditionTag.putString("variant", expedition.variant().name());
            expeditionTag.putBoolean("generated", expedition.generated());
            expeditionTag.putBoolean("rewardClaimed", expedition.rewardClaimed());
            expeditionTag.putString("returnAddress", expedition.returnAddress());
            expeditionTag.putBoolean("rewardUnlocked", expedition.rewardUnlocked());
            expeditionTag.putLong("layoutSeed", expedition.layoutSeed());
            ListTag rewardDoors = new ListTag();
            for (ExpeditionRewardDoor door : expedition.rewardDoors()) {
                CompoundTag doorTag = new CompoundTag();
                doorTag.putInt("x", door.pos().getX());
                doorTag.putInt("y", door.pos().getY());
                doorTag.putInt("z", door.pos().getZ());
                doorTag.putString("facing", door.facing().getName());
                rewardDoors.add(doorTag);
            }
            expeditionTag.put("rewardDoors", rewardDoors);
            if (expedition.rewardDoorPos() != null) {
                expeditionTag.putInt("rewardDoorX", expedition.rewardDoorPos().getX());
                expeditionTag.putInt("rewardDoorY", expedition.rewardDoorPos().getY());
                expeditionTag.putInt("rewardDoorZ", expedition.rewardDoorPos().getZ());
                expeditionTag.putString("rewardDoorFacing", expedition.rewardDoorFacing().getName());
            }
            ListTag combatRooms = new ListTag();
            for (BlockPos pos : expedition.combatRoomCenters()) {
                CompoundTag roomTag = new CompoundTag();
                roomTag.putInt("x", pos.getX());
                roomTag.putInt("y", pos.getY());
                roomTag.putInt("z", pos.getZ());
                combatRooms.add(roomTag);
            }
            expeditionTag.put("combatRooms", combatRooms);
            expeditions.add(expeditionTag);
        }
        tag.put("expeditions", expeditions);
        return tag;
    }

    private static List<BlockPos> loadCombatRoomCenters(CompoundTag expeditionTag) {
        List<BlockPos> rooms = new ArrayList<>();
        ListTag combatRooms = expeditionTag.getList("combatRooms", Tag.TAG_COMPOUND);
        for (int i = 0; i < combatRooms.size(); i++) {
            CompoundTag roomTag = combatRooms.getCompound(i);
            rooms.add(new BlockPos(roomTag.getInt("x"), roomTag.getInt("y"), roomTag.getInt("z")));
        }
        return List.copyOf(rooms);
    }

    private static ResourceLocation legacyDimension(CompoundTag expeditionTag) {
        int slot = expeditionTag.getInt("slot");
        return slot < 0 || slot >= 4096 ? Level.OVERWORLD.location() : LanteaDimensions.EXPEDITIONS.location();
    }

    private static List<ExpeditionRewardDoor> loadRewardDoors(CompoundTag expeditionTag) {
        List<ExpeditionRewardDoor> doors = new ArrayList<>();
        ListTag tags = expeditionTag.getList("rewardDoors", Tag.TAG_COMPOUND);
        for (int i = 0; i < tags.size(); i++) {
            CompoundTag doorTag = tags.getCompound(i);
            Direction facing = Direction.byName(doorTag.getString("facing"));
            doors.add(new ExpeditionRewardDoor(
                    new BlockPos(doorTag.getInt("x"), doorTag.getInt("y"), doorTag.getInt("z")), facing));
        }
        if (doors.isEmpty() && expeditionTag.contains("rewardDoorX")) {
            doors.add(new ExpeditionRewardDoor(loadRewardDoorPos(expeditionTag), loadRewardDoorFacing(expeditionTag)));
        }
        return List.copyOf(doors);
    }

    private static boolean loadRewardUnlocked(CompoundTag expeditionTag) {
        int slot = expeditionTag.getInt("slot");
        boolean legacyDebugTrial = !expeditionTag.contains("dimension") && (slot < 0 || slot >= 4096);
        return !legacyDebugTrial && expeditionTag.getBoolean("rewardUnlocked");
    }

    private static BlockPos loadRewardDoorPos(CompoundTag expeditionTag) {
        if (!expeditionTag.contains("rewardDoorX") || !expeditionTag.contains("rewardDoorY") || !expeditionTag.contains("rewardDoorZ")) {
            return null;
        }
        return new BlockPos(expeditionTag.getInt("rewardDoorX"), expeditionTag.getInt("rewardDoorY"), expeditionTag.getInt("rewardDoorZ"));
    }

    private static Direction loadRewardDoorFacing(CompoundTag expeditionTag) {
        Direction facing = Direction.byName(expeditionTag.getString("rewardDoorFacing"));
        return facing == null ? Direction.SOUTH : facing;
    }

    private int nextSlot() {
        int slot = 0;
        while (slotInUse(slot)) {
            slot++;
        }
        return slot;
    }

    private boolean slotInUse(int slot) {
        for (ExpeditionInstance expedition : byAddress.values()) {
            if (expedition.slot() == slot) {
                return true;
            }
        }
        return false;
    }

    private String uniqueAddress(String baseAddress) {
        String address = baseAddress;
        int salt = 1;
        while (byAddress.containsKey(address)) {
            address = StargateAddress.withSalt(baseAddress, salt++);
        }
        return address;
    }

    private static BlockPos basePosForSlot(int slot) {
        int gridX = slot % 64;
        int gridZ = slot / 64;
        return new BlockPos(gridX * SLOT_SPACING, BASE_Y, gridZ * SLOT_SPACING);
    }

    private static String addressFor(long seed, BlockPos origin, int slot) {
        long state = seed ^ origin.asLong() ^ ((long)slot * 0x9E3779B97F4A7C15L);
        StringBuilder address = new StringBuilder(StargateAddress.ADDRESS_LENGTH);
        for (int i = 0; i < StargateAddress.GATE_ID_LENGTH; i++) {
            state = mix(state, i + 17);
            address.append(StargateAddress.LEGACY_GLYPHS.charAt(Math.floorMod(state, StargateAddress.LEGACY_GLYPHS.length())));
        }
        address.append('X');
        return address.toString();
    }

    private static int mix(long seed, int value) {
        long state = seed ^ ((long)value * 0xBF58476D1CE4E5B9L);
        state ^= state >>> 33;
        state *= 0xff51afd7ed558ccdL;
        state ^= state >>> 33;
        state *= 0xc4ceb9fe1a85ec53L;
        state ^= state >>> 33;
        return (int)state;
    }

    private static String normalize(String address) {
        return address == null ? "" : address.trim().toUpperCase();
    }
}
