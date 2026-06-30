package com.pclogix.lanteacraft.gate;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public final class StargateEventDispatcher {
    private static final List<Listener> LISTENERS = new CopyOnWriteArrayList<>();

    private StargateEventDispatcher() {
    }

    public static void register(Listener listener) {
        LISTENERS.add(listener);
    }

    public static void wormholeDialing(StargateEntry source, StargateEntry destination) {
        publish(GateEvent.simple("dialing", source, destination, "outgoing"));
        publish(GateEvent.simple("incoming", destination, source, "incoming"));
        publish(GateEvent.simple("dialing", destination, source, "incoming"));
    }

    public static void wormholeOpened(ServerLevel level, StargateEntry gate) {
        StargateNetworkSavedData network = StargateNetworkSavedData.get(level);
        Optional<StargateEntry> destination = network.findConnectedDestination(gate.address());
        if (destination.isPresent()) {
            publish(GateEvent.simple("opened", gate, destination.get(), "outgoing"));
            return;
        }

        network.findIncomingSource(gate.address()).ifPresent(source -> publish(GateEvent.simple("opened", gate, source, "incoming")));
    }

    public static void wormholeClosed(StargateEntry source, StargateEntry destination) {
        publish(GateEvent.simple("closed", source, destination, "outgoing"));
        publish(GateEvent.simple("closed", destination, source, "incoming"));
    }

    public static void entityTransit(Entity entity, StargateEntry source, StargateEntry destination) {
        publish(GateEvent.entity("entity_transit", source, destination, "outgoing", entity));
        publish(GateEvent.entity("entity_transit", destination, source, "incoming", entity));
    }

    public static void localEntityEvent(Entity entity, StargateEntry gate, String direction) {
        publish(GateEvent.entity("entity_transit", gate, null, direction, entity));
    }

    public static void gdoSignal(StargateEntry receiver, StargateEntry transmitter, Entity entity, String code, boolean accepted) {
        publish(GateEvent.data("gdo_signal", receiver, transmitter, "incoming", entity, Map.of(
                "code", code,
                "accepted", accepted,
                "transmitterAddress", transmitter.address())));
    }

    public static void timeout(ServerLevel sourceLevel, StargateEntry source, StargateEntry destination) {
        StargateChunkLoading.forceConnection(sourceLevel, source, destination, false);
        StargateNetworkSavedData.get(sourceLevel).disconnect(source);

        if (sourceLevel.getBlockEntity(source.basePos()) instanceof com.pclogix.lanteacraft.block.entity.StargateBaseBlockEntity sourceBase) {
            sourceBase.clearConnection();
        }

        ServerLevel destinationLevel = sourceLevel.getServer().getLevel(ResourceKey.create(Registries.DIMENSION, destination.dimension()));
        if (destinationLevel != null && destinationLevel.getBlockEntity(destination.basePos()) instanceof com.pclogix.lanteacraft.block.entity.StargateBaseBlockEntity targetBase) {
            targetBase.clearConnection();
        }

        wormholeClosed(source, destination);
    }

    private static void publish(GateEvent event) {
        for (Listener listener : LISTENERS) {
            listener.handle(event);
        }
    }

    public interface Listener {
        void handle(GateEvent event);
    }

    public record GateEvent(String type, StargateEntry local, StargateEntry remote, String direction, Entity entity, Map<String, Object> data) {
        public static GateEvent simple(String type, StargateEntry local, StargateEntry remote, String direction) {
            return new GateEvent(type, local, remote, direction, null, Map.of());
        }

        public static GateEvent entity(String type, StargateEntry local, StargateEntry remote, String direction, Entity entity) {
            return new GateEvent(type, local, remote, direction, entity, Map.of());
        }

        public static GateEvent data(String type, StargateEntry local, StargateEntry remote, String direction, Entity entity, Map<String, Object> data) {
            return new GateEvent(type, local, remote, direction, entity, data == null ? Map.of() : data);
        }
    }
}
