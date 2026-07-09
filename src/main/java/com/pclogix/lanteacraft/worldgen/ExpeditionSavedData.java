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
import net.minecraft.server.level.ServerLevel;
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
        ListTag expeditions = tag.getList("expeditions", Tag.TAG_COMPOUND);
        for (int i = 0; i < expeditions.size(); i++) {
            CompoundTag expeditionTag = expeditions.getCompound(i);
            Direction facing = Direction.byName(expeditionTag.getString("facing"));
            if (facing == null) {
                facing = Direction.SOUTH;
            }
            StargateVariant variant = StargateVariant.PEGASUS;
            if (expeditionTag.contains("variant")) {
                try {
                    variant = StargateVariant.valueOf(expeditionTag.getString("variant"));
                } catch (IllegalArgumentException ignored) {
                    variant = StargateVariant.PEGASUS;
                }
            }
            ExpeditionInstance expedition = new ExpeditionInstance(
                    normalize(expeditionTag.getString("address")),
                    expeditionTag.getInt("slot"),
                    expeditionTag.getInt("tier"),
                    new BlockPos(expeditionTag.getInt("x"), expeditionTag.getInt("y"), expeditionTag.getInt("z")),
                    facing,
                    variant,
                    expeditionTag.getBoolean("generated"),
                    expeditionTag.getBoolean("rewardClaimed"),
                    normalize(expeditionTag.getString("returnAddress")),
                    loadCombatRoomCenters(expeditionTag),
                    expeditionTag.getBoolean("rewardUnlocked"),
                    expeditionTag.contains("layoutSeed") ? expeditionTag.getLong("layoutSeed") : expeditionTag.getInt("slot"),
                    loadRewardDoorPos(expeditionTag),
                    loadRewardDoorFacing(expeditionTag));
            if (!expedition.address().isBlank()) {
                data.byAddress.put(expedition.address(), expedition);
            }
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
                Direction.SOUTH,
                StargateVariant.PEGASUS,
                false,
                false,
                "",
                List.of(),
                false,
                seed ^ origin.asLong() ^ slot,
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

    public void markGenerated(ExpeditionInstance expedition, BlockPos basePos) {
        markGenerated(expedition, basePos, expedition.facing());
    }

    public void markGenerated(ExpeditionInstance expedition, BlockPos basePos, Direction facing) {
        ExpeditionInstance updated = new ExpeditionInstance(
                expedition.address(),
                expedition.slot(),
                expedition.tier(),
                basePos.immutable(),
                facing,
                expedition.variant(),
                true,
                expedition.rewardClaimed(),
                expedition.returnAddress(),
                expedition.combatRoomCenters(),
                expedition.rewardUnlocked(),
                expedition.layoutSeed(),
                expedition.rewardDoorPos(),
                expedition.rewardDoorFacing());
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

    public void rememberRewardDoor(String expeditionAddress, BlockPos rewardDoorPos, Direction rewardDoorFacing) {
        findByAddress(expeditionAddress).ifPresent(expedition -> {
            byAddress.put(expedition.address(), expedition.withRewardDoor(rewardDoorPos, rewardDoorFacing));
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
            expeditionTag.putString("facing", expedition.facing().getName());
            expeditionTag.putString("variant", expedition.variant().name());
            expeditionTag.putBoolean("generated", expedition.generated());
            expeditionTag.putBoolean("rewardClaimed", expedition.rewardClaimed());
            expeditionTag.putString("returnAddress", expedition.returnAddress());
            expeditionTag.putBoolean("rewardUnlocked", expedition.rewardUnlocked());
            expeditionTag.putLong("layoutSeed", expedition.layoutSeed());
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
