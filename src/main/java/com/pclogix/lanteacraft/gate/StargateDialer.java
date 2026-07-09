package com.pclogix.lanteacraft.gate;

import com.pclogix.lanteacraft.block.entity.StargateBaseBlockEntity;
import com.pclogix.lanteacraft.power.StargatePower;
import com.pclogix.lanteacraft.worldgen.ExpeditionInstance;
import com.pclogix.lanteacraft.worldgen.ExpeditionSavedData;
import com.pclogix.lanteacraft.worldgen.LanteaDimensions;
import com.pclogix.lanteacraft.worldgen.PlannedStargateResolver;
import java.util.Optional;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

public final class StargateDialer {
    private StargateDialer() {
    }

    public static DialResult dial(ServerLevel level, StargateEntry local, String address) {
        String targetAddress = normalize(address);
        if (targetAddress.isBlank()) {
            Optional<String> returnAddress = expeditionReturnAddress(level, local);
            if (returnAddress.isPresent()) {
                closeIncomingExpeditionConnection(level, local);
                return dial(level, local, returnAddress.get());
            }
            return disconnect(level, local);
        }

        if (!isValidAddress(targetAddress)) {
            return DialResult.fail("invalid_address", "Invalid Stargate address: " + targetAddress);
        }

        StargateNetworkSavedData network = StargateNetworkSavedData.get(level);
        Optional<StargateRecord> targetRecord = network.findByAddress(targetAddress);
        if (targetRecord.isPresent() && targetRecord.get().status() == StargateStatus.DORMANT) {
            return DialResult.fail("target_unavailable", "Stargate target is unavailable: " + targetAddress);
        }

        StargateBaseBlockEntity localBase = StargatePower.baseEntity(level.getServer(), local);
        if (targetAddress.length() > StargateAddress.ADDRESS_LENGTH
                && (localBase == null || !localBase.hasEighthChevronUnlocked())) {
            return DialResult.fail("eighth_chevron_locked", "Stargate requires an installed Eighth Chevron Crystal to dial extended addresses.");
        }

        Optional<StargateEntry> destination = PlannedStargateResolver.resolve(level, targetAddress);
        if (destination.isEmpty()) {
            return DialResult.fail("unknown_address", "Unknown Stargate address: " + targetAddress);
        }

        StargateEntry target = destination.get();
        rememberExpeditionReturnAddress(level, local, target);
        if (local.address().equals(target.address())) {
            return DialResult.fail("local_address", "Cannot dial the local Stargate.");
        }

        if (network.findIncomingSource(local.address()).isPresent()) {
            return DialResult.fail("incoming_active", "Incoming wormholes cannot be redirected from this gate.");
        }

        if (StargatePower.requiresPowerToDial(localBase, target)) {
            long dialCost = StargatePower.calculateDialCost(local, target);
            if (!StargatePower.consumeDialPower(localBase, target, dialCost)) {
                return DialResult.fail("insufficient_power", "Stargate lacks " + dialCost + " FE to dial.");
            }
        }

        StargateChunkLoading.forceConnection(level, local, target, true);
        network.connect(level, local, target);
        startDialingAnimation(level, local, target);
        StargateEventDispatcher.wormholeDialing(local, target);
        return DialResult.success("dialing", "Dialing " + target.address() + ".");
    }

    public static DialResult disconnect(ServerLevel level, StargateEntry local) {
        StargateNetworkSavedData network = StargateNetworkSavedData.get(level);
        Optional<StargateEntry> destination = network.findConnectedDestination(local.address());
        if (destination.isEmpty()) {
            if (network.findIncomingSource(local.address()).isPresent()) {
                return DialResult.fail("incoming_active", "Incoming wormholes must be closed from the dialing gate.");
            }

            return DialResult.fail("not_connected", "No outgoing Stargate connection to disconnect.");
        }

        StargateEntry target = destination.get();
        StargateChunkLoading.forceConnection(level, local, target, false);
        network.disconnect(local);

        if (level.getBlockEntity(local.basePos()) instanceof StargateBaseBlockEntity localBase) {
            localBase.clearConnection();
        }

        ResourceKey<Level> targetDimension = ResourceKey.create(Registries.DIMENSION, target.dimension());
        ServerLevel targetLevel = level.getServer().getLevel(targetDimension);
        if (targetLevel != null && targetLevel.getBlockEntity(target.basePos()) instanceof StargateBaseBlockEntity targetBase) {
            targetBase.clearConnection();
        }

        StargateEventDispatcher.wormholeClosed(local, target);
        return DialResult.success("disconnected", "Stargate disconnected.");
    }

    public static String normalize(String address) {
        return address == null ? "" : address.trim().toUpperCase();
    }

    public static boolean isValidAddress(String address) {
        if (address.length() < StargateAddress.ADDRESS_LENGTH || address.length() > StargateAddress.MAX_ADDRESS_LENGTH) {
            return false;
        }

        for (int i = 0; i < address.length(); i++) {
            if (!StargateAddress.isAddressGlyph(address.charAt(i))) {
                return false;
            }
        }

        return true;
    }

    private static void startDialingAnimation(ServerLevel level, StargateEntry local, StargateEntry target) {
        if (level.getBlockEntity(local.basePos()) instanceof StargateBaseBlockEntity localBase) {
            localBase.startDialing(target.address());
            localBase.setConnectedAddress(target.address());
        }

        ResourceKey<Level> targetDimension = ResourceKey.create(Registries.DIMENSION, target.dimension());
        ServerLevel targetLevel = level.getServer().getLevel(targetDimension);
        if (targetLevel != null && targetLevel.getBlockEntity(target.basePos()) instanceof StargateBaseBlockEntity targetBase) {
            targetBase.startDialing(local.address());
            targetBase.setConnectedAddress(local.address());
        }
    }

    private static Optional<String> expeditionReturnAddress(ServerLevel level, StargateEntry local) {
        if (!local.dimension().equals(LanteaDimensions.EXPEDITIONS.location())) {
            return Optional.empty();
        }

        return ExpeditionSavedData.get(level).findByAddress(local.address())
                .map(ExpeditionInstance::returnAddress)
                .filter(address -> !address.isBlank());
    }

    private static void closeIncomingExpeditionConnection(ServerLevel level, StargateEntry local) {
        StargateNetworkSavedData network = StargateNetworkSavedData.get(level);
        network.findIncomingSource(local.address()).ifPresent(source -> {
            ServerLevel sourceLevel = level.getServer().getLevel(ResourceKey.create(Registries.DIMENSION, source.dimension()));
            if (sourceLevel != null) {
                disconnect(sourceLevel, source);
            }
        });
    }

    private static void rememberExpeditionReturnAddress(ServerLevel level, StargateEntry local, StargateEntry target) {
        if (target.dimension().equals(LanteaDimensions.EXPEDITIONS.location())) {
            ExpeditionSavedData.get(level).rememberReturnAddress(target.address(), local.address());
        }
    }

    public record DialResult(boolean success, String code, String message) {
        public static DialResult success(String code, String message) {
            return new DialResult(true, code, message);
        }

        public static DialResult fail(String code, String message) {
            return new DialResult(false, code, message);
        }
    }
}
