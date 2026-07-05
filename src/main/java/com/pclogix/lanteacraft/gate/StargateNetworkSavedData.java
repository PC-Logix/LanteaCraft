package com.pclogix.lanteacraft.gate;

import com.pclogix.lanteacraft.LanteaCraft;
import com.pclogix.lanteacraft.worldgen.FixedDimensionGates;
import com.pclogix.lanteacraft.worldgen.PlannedStargate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;

public class StargateNetworkSavedData extends SavedData {
    private static final String DATA_NAME = LanteaCraft.MODID + "_stargate_network";
    private static final SavedData.Factory<StargateNetworkSavedData> FACTORY = new SavedData.Factory<>(
            StargateNetworkSavedData::new,
            StargateNetworkSavedData::load);

    private final Map<String, StargateRecord> recordsByAddress = new HashMap<>();
    private final Map<UUID, StargateRecord> recordsByGateId = new HashMap<>();
    private final Map<String, UUID> gateIdsByLocation = new HashMap<>();
    private final Map<String, String> activeConnections = new HashMap<>();
    private final Map<String, Long> connectionStartTimes = new HashMap<>();
    private final Map<ResourceLocation, Character> dimensionGlyphs = new HashMap<>();

    public static StargateNetworkSavedData get(ServerLevel level) {
        return level.getServer().overworld().getDataStorage().computeIfAbsent(FACTORY, DATA_NAME);
    }

    public static StargateNetworkSavedData load(CompoundTag tag, HolderLookup.Provider registries) {
        StargateNetworkSavedData data = new StargateNetworkSavedData();
        ListTag gates = tag.getList("gates", Tag.TAG_COMPOUND);
        int upgraded = 0;

        for (int i = 0; i < gates.size(); i++) {
            CompoundTag gateTag = gates.getCompound(i);
            String address = gateTag.getString("address");
            if (address.isBlank()) {
                LanteaCraft.LOGGER.warn("Skipping Stargate registry record with blank address at index {}.", i);
                continue;
            }

            UUID gateId;
            if (gateTag.hasUUID("gateId")) {
                gateId = gateTag.getUUID("gateId");
            } else {
                gateId = UUID.randomUUID();
                upgraded++;
                LanteaCraft.LOGGER.info("Upgraded Stargate address {} with generated gateId {}.", address, gateId);
            }

            ResourceLocation dimension = ResourceLocation.parse(gateTag.getString("dimension"));
            BlockPos basePos = new BlockPos(gateTag.getInt("x"), gateTag.getInt("y"), gateTag.getInt("z"));
            Direction facing = Direction.byName(gateTag.getString("facing"));
            if (facing == null) {
                facing = Direction.NORTH;
            }

            StargateVariant variant = StargateVariant.MILKY_WAY;
            if (gateTag.contains("variant")) {
                try {
                    variant = StargateVariant.valueOf(gateTag.getString("variant"));
                } catch (IllegalArgumentException ignored) {
                    variant = StargateVariant.MILKY_WAY;
                }
            }

            StargateStatus status = gateTag.contains("status") ? statusByName(gateTag.getString("status")) : StargateStatus.ACTIVE;
            String origin = gateTag.contains("origin") ? gateTag.getString("origin") : "legacy";
            data.put(new StargateRecord(gateId, address, dimension, basePos.immutable(), facing, variant, status, origin), true);
        }

        if (upgraded > 0) {
            LanteaCraft.LOGGER.info("Migrated {} legacy Stargate registry record(s) to gateId-backed records.", upgraded);
            data.setDirty();
        }

        ListTag dimensions = tag.getList("dimensions", Tag.TAG_COMPOUND);
        for (int i = 0; i < dimensions.size(); i++) {
            CompoundTag dimensionTag = dimensions.getCompound(i);
            ResourceLocation dimension = ResourceLocation.parse(dimensionTag.getString("dimension"));
            String glyph = dimensionTag.getString("glyph");
            if (!glyph.isEmpty() && StargateAddress.isAddressGlyph(glyph.charAt(0))) {
                data.dimensionGlyphs.put(dimension, StargateAddress.normalizeGlyph(glyph.charAt(0)));
            }
        }

        ListTag connections = tag.getList("connections", Tag.TAG_COMPOUND);
        for (int i = 0; i < connections.size(); i++) {
            CompoundTag connectionTag = connections.getCompound(i);
            String source = connectionTag.getString("from");
            data.activeConnections.put(source, connectionTag.getString("to"));
            data.connectionStartTimes.put(source, connectionTag.getLong("startedAt"));
        }

        return data;
    }

    public StargateEntry register(ServerLevel level, BlockPos basePos, Direction facing) {
        return register(level, basePos, facing, StargateVariant.MILKY_WAY);
    }

    public StargateEntry register(ServerLevel level, BlockPos basePos, Direction facing, StargateVariant variant) {
        String locationKey = locationKey(level, basePos);
        UUID existingGateId = gateIdsByLocation.get(locationKey);
        if (existingGateId != null) {
            return updateLocation(existingGateId, level.dimension().location(), basePos, facing, variant).entry();
        }

        String address = uniqueAddress(StargateAddress.forGate(level, basePos, dimensionGlyph(level)), locationKey);
        return registerOrUpdateActiveGate(address, level.dimension(), basePos, facing, variant, "assembled").entry();
    }

    public StargateRecord registerOrUpdateActiveGate(String address, ResourceKey<Level> dimension, BlockPos basePos, Direction facing, StargateVariant variant, String origin) {
        return registerOrUpdateActiveGate(address, dimension.location(), basePos, facing, variant, origin);
    }

    public StargateRecord registerOrUpdateActiveGate(String address, ResourceLocation dimension, BlockPos basePos, Direction facing, StargateVariant variant, String origin) {
        if (FixedDimensionGates.isFixedDimension(dimension)) {
            address = FixedDimensionGates.forDimension(dimension).map(PlannedStargate::address).orElse(address);
        }
        String normalizedAddress = normalizeAddress(address);
        StargateRecord existing = recordsByAddress.get(normalizedAddress);
        StargateRecord record = existing == null
                ? new StargateRecord(UUID.randomUUID(), normalizedAddress, dimension, basePos.immutable(), facing, variant, StargateStatus.ACTIVE, origin)
                : existing.withEndpoint(dimension, basePos, facing, variant, StargateStatus.ACTIVE, origin);
        put(record, false);
        setDirty();
        return record;
    }

    public StargateRecord reserveGate(String address, ResourceKey<Level> dimension, BlockPos plannedPos, Direction facing, StargateVariant variant, String origin) {
        return reserveGate(address, dimension.location(), plannedPos, facing, variant, origin);
    }

    public StargateRecord reserveGate(String address, ResourceLocation dimension, BlockPos plannedPos, Direction facing, StargateVariant variant, String origin) {
        if (FixedDimensionGates.isFixedDimension(dimension)) {
            address = FixedDimensionGates.forDimension(dimension).map(PlannedStargate::address).orElse(address);
        }
        String normalizedAddress = normalizeAddress(address);
        StargateRecord existing = recordsByAddress.get(normalizedAddress);
        StargateRecord record = existing == null
                ? new StargateRecord(UUID.randomUUID(), normalizedAddress, dimension, plannedPos.immutable(), facing, variant, StargateStatus.RESERVED, origin)
                : existing.status() == StargateStatus.ACTIVE ? existing : existing.withEndpoint(dimension, plannedPos, facing, variant, StargateStatus.RESERVED, origin);
        put(record, false);
        setDirty();
        return record;
    }

    public Optional<StargateRecord> findByAddress(String address) {
        return Optional.ofNullable(recordsByAddress.get(normalizeAddress(address)));
    }

    public Optional<StargateRecord> findByGateId(UUID gateId) {
        return Optional.ofNullable(recordsByGateId.get(gateId));
    }

    public Optional<StargateRecord> findByLocation(ResourceKey<Level> dimension, BlockPos basePos) {
        return findByLocation(dimension.location(), basePos);
    }

    public Optional<StargateRecord> findByLocation(ResourceLocation dimension, BlockPos basePos) {
        UUID gateId = gateIdsByLocation.get(locationKey(dimension, basePos));
        return gateId == null ? Optional.empty() : findByGateId(gateId);
    }

    public Optional<StargateEntry> findByBase(ServerLevel level, BlockPos basePos) {
        return findByLocation(level.dimension(), basePos).flatMap(StargateRecord::activeEntry);
    }

    public Optional<StargateEntry> findActiveEntryByAddress(String address) {
        return findByAddress(address).flatMap(StargateRecord::activeEntry);
    }

    public Optional<StargateEntry> findConnectedDestination(String sourceAddress) {
        String destinationAddress = activeConnections.get(sourceAddress);
        return destinationAddress == null ? Optional.empty() : findActiveEntryByAddress(destinationAddress);
    }

    public long connectionStartedAt(String sourceAddress) {
        return connectionStartTimes.getOrDefault(sourceAddress, 0L);
    }

    public Optional<StargateEntry> findIncomingSource(String destinationAddress) {
        String normalizedDestination = normalizeAddress(destinationAddress);
        for (Map.Entry<String, String> connection : activeConnections.entrySet()) {
            if (connection.getValue().equals(normalizedDestination)) {
                return findActiveEntryByAddress(connection.getKey());
            }
        }

        return Optional.empty();
    }

    public Set<String> activeSourceAddresses() {
        return Set.copyOf(activeConnections.keySet());
    }

    public Set<String> activeDestinationAddresses() {
        return Set.copyOf(activeConnections.values());
    }

    public Iterable<StargateEntry> entries() {
        return recordsByAddress.values().stream()
                .flatMap(record -> record.activeEntry().stream())
                .toList();
    }

    public Collection<StargateRecord> getAllRecords() {
        return Set.copyOf(recordsByAddress.values());
    }

    public void connect(ServerLevel level, StargateEntry first, StargateEntry second) {
        activeConnections.put(first.address(), second.address());
        connectionStartTimes.put(first.address(), level.getGameTime());
        setDirty();
    }

    public void disconnect(StargateEntry gate) {
        String destination = activeConnections.remove(gate.address());
        connectionStartTimes.remove(gate.address());
        if (destination != null) {
            setDirty();
        }
    }

    public void markDormant(UUID gateId) {
        StargateRecord record = recordsByGateId.get(gateId);
        if (record == null || record.status() == StargateStatus.DORMANT) {
            return;
        }
        put(record.withStatus(StargateStatus.DORMANT), false);
        activeConnections.remove(record.address());
        connectionStartTimes.remove(record.address());
        Iterator<Map.Entry<String, String>> connections = activeConnections.entrySet().iterator();
        while (connections.hasNext()) {
            Map.Entry<String, String> connection = connections.next();
            if (record.address().equals(connection.getValue())) {
                connectionStartTimes.remove(connection.getKey());
                connections.remove();
            }
        }
        setDirty();
    }

    public StargateRecord updateLocation(UUID gateId, ResourceLocation dimension, BlockPos basePos, Direction facing) {
        return updateLocation(gateId, dimension, basePos, facing, null);
    }

    public StargateRecord updateLocation(UUID gateId, ResourceLocation dimension, BlockPos basePos, Direction facing, StargateVariant variant) {
        StargateRecord record = recordsByGateId.get(gateId);
        if (record == null) {
            throw new IllegalArgumentException("Unknown Stargate gateId " + gateId);
        }
        StargateRecord updated = record.withEndpoint(dimension, basePos, facing, variant, StargateStatus.ACTIVE, record.origin());
        put(updated, false);
        setDirty();
        return updated;
    }

    public void remove(ServerLevel level, BlockPos basePos) {
        findByLocation(level.dimension(), basePos).ifPresent(record -> markDormant(record.gateId()));
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        ListTag gates = new ListTag();
        for (StargateRecord record : recordsByAddress.values()) {
            CompoundTag gateTag = new CompoundTag();
            gateTag.putUUID("gateId", record.gateId());
            gateTag.putString("address", record.address());
            gateTag.putString("dimension", record.dimension().toString());
            gateTag.putInt("x", record.basePos().getX());
            gateTag.putInt("y", record.basePos().getY());
            gateTag.putInt("z", record.basePos().getZ());
            gateTag.putString("facing", record.facing().getName());
            gateTag.putString("variant", record.variant().name());
            gateTag.putString("status", record.status().name());
            gateTag.putString("origin", record.origin());
            gates.add(gateTag);
        }

        tag.put("gates", gates);

        ListTag dimensions = new ListTag();
        for (Map.Entry<ResourceLocation, Character> entry : dimensionGlyphs.entrySet()) {
            CompoundTag dimensionTag = new CompoundTag();
            dimensionTag.putString("dimension", entry.getKey().toString());
            dimensionTag.putString("glyph", String.valueOf(entry.getValue()));
            dimensions.add(dimensionTag);
        }

        tag.put("dimensions", dimensions);

        ListTag connections = new ListTag();
        for (Map.Entry<String, String> connection : activeConnections.entrySet()) {
            CompoundTag connectionTag = new CompoundTag();
            connectionTag.putString("from", connection.getKey());
            connectionTag.putString("to", connection.getValue());
            connectionTag.putLong("startedAt", connectionStartTimes.getOrDefault(connection.getKey(), 0L));
            connections.add(connectionTag);
        }

        tag.put("connections", connections);
        return tag;
    }

    private void put(StargateRecord record, boolean loading) {
        StargateRecord existingAddress = recordsByAddress.get(record.address());
        if (existingAddress != null && !existingAddress.gateId().equals(record.gateId())) {
            LanteaCraft.LOGGER.warn("Stargate address collision for {}; preserving gateId {} and ignoring conflicting gateId {}.", record.address(), existingAddress.gateId(), record.gateId());
            return;
        }

        StargateRecord previous = recordsByGateId.get(record.gateId());
        if (previous != null) {
            gateIdsByLocation.remove(locationKey(previous.dimension(), previous.basePos()));
        }

        recordsByAddress.put(record.address(), record);
        recordsByGateId.put(record.gateId(), record);
        if (record.status() == StargateStatus.ACTIVE) {
            gateIdsByLocation.put(locationKey(record.dimension(), record.basePos()), record.gateId());
        }
    }

    private String uniqueAddress(String baseAddress, String locationKey) {
        String address = baseAddress;
        int salt = 1;
        while (recordsByAddress.containsKey(address) && !locationKey.equals(locationKey(recordsByAddress.get(address).dimension(), recordsByAddress.get(address).basePos()))) {
            address = StargateAddress.withSalt(baseAddress, salt++);
        }

        return address;
    }

    private char dimensionGlyph(ServerLevel level) {
        ResourceLocation dimension = level.dimension().location();
        Character glyph = dimensionGlyphs.get(dimension);
        if (glyph != null) {
            return glyph;
        }

        char assigned = preferredDimensionGlyph(dimension);
        if (glyphInUse(assigned)) {
            assigned = firstFreeDimensionGlyph();
        }

        dimensionGlyphs.put(dimension, assigned);
        setDirty();
        return assigned;
    }

    private char preferredDimensionGlyph(ResourceLocation dimension) {
        if (dimension.equals(ResourceLocation.fromNamespaceAndPath("minecraft", "overworld"))) {
            return StargateAddress.OVERWORLD_GLYPH;
        }
        if (dimension.equals(ResourceLocation.fromNamespaceAndPath("minecraft", "the_nether"))) {
            return StargateAddress.NETHER_GLYPH;
        }
        if (dimension.equals(ResourceLocation.fromNamespaceAndPath("minecraft", "the_end"))) {
            return StargateAddress.END_GLYPH;
        }

        for (int i = 0; i < StargateAddress.LEGACY_GLYPHS.length(); i++) {
            char glyph = StargateAddress.LEGACY_GLYPHS.charAt(i);
            if (glyph != StargateAddress.OVERWORLD_GLYPH && glyph != StargateAddress.NETHER_GLYPH && glyph != StargateAddress.END_GLYPH && !glyphInUse(glyph)) {
                return glyph;
            }
        }

        return firstFreeDimensionGlyph();
    }

    private char firstFreeDimensionGlyph() {
        for (int i = 0; i < StargateAddress.LEGACY_GLYPHS.length(); i++) {
            char glyph = StargateAddress.LEGACY_GLYPHS.charAt(i);
            if (!glyphInUse(glyph)) {
                return glyph;
            }
        }

        LanteaCraft.LOGGER.warn("All Stargate dimension glyphs are assigned; reusing + for additional dimensions.");
        return '+';
    }

    private boolean glyphInUse(char glyph) {
        return dimensionGlyphs.containsValue(glyph);
    }

    private static StargateStatus statusByName(String name) {
        try {
            return StargateStatus.valueOf(name);
        } catch (IllegalArgumentException ignored) {
            return StargateStatus.ACTIVE;
        }
    }

    private static String normalizeAddress(String address) {
        return address == null ? "" : address.trim().toUpperCase();
    }

    private static String locationKey(ServerLevel level, BlockPos basePos) {
        return locationKey(level.dimension().location(), basePos);
    }

    private static String locationKey(ResourceLocation dimension, BlockPos basePos) {
        return dimension + "@" + basePos.getX() + "," + basePos.getY() + "," + basePos.getZ();
    }
}
