package com.pclogix.lanteacraft.gate;

import com.pclogix.lanteacraft.LanteaCraft;
import com.pclogix.lanteacraft.Config;
import com.pclogix.lanteacraft.block.entity.StargateBaseBlockEntity;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.RelativeMovement;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

public class StargateTeleportHandler {
    private static final double HALF_INTERIOR_WIDTH = 2.35D;
    private static final double MIN_INTERIOR_Y = 0.1D;
    private static final double MAX_INTERIOR_Y = 5.9D;
    private static final double HALF_INTERIOR_DEPTH = 0.7D;
    private static final double EXIT_DISTANCE = 1.65D;
    private static final double REJECTION_DISTANCE = 1.85D;
    private static final double IRIS_BARRIER_DISTANCE = 0.92D;
    private static final double MIN_REJECTION_SPEED = 0.75D;
    private static final long KAWOOSH_TICKS = 18L;
    private static final double KAWOOSH_DEPTH = 3.25D;
    private static final double KAWOOSH_RADIUS = 2.75D;
    private static final double KAWOOSH_CENTER_Y = 3.0D;

    private final Map<UUID, Long> cooldowns = new HashMap<>();
    private final Map<UUID, Long> kawooshHits = new HashMap<>();

    public static void register(IEventBus gameEventBus) {
        gameEventBus.register(new StargateTeleportHandler());
    }

    @SubscribeEvent
    public void onLevelTick(LevelTickEvent.Post event) {
        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }

        StargateNetworkSavedData network = StargateNetworkSavedData.get(level);
        tickIrisBarriers(level, network);
        for (String sourceAddress : network.activeSourceAddresses()) {
            Optional<StargateEntry> source = network.findActiveEntryByAddress(sourceAddress);
            Optional<StargateEntry> destination = network.findConnectedDestination(sourceAddress);
            if (source.isEmpty() || destination.isEmpty() || !source.get().dimension().equals(level.dimension().location())) {
                continue;
            }

            long startedAt = network.connectionStartedAt(sourceAddress);
            long timeoutTicks = wormholeTimeoutTicks();
            if (timeoutTicks > 0L && startedAt > 0L && level.getGameTime() - startedAt >= timeoutTicks) {
                timeoutConnection(level, source.get(), destination.get());
                continue;
            }

            tickKawoosh(level, source.get());
            if (!isGateOpen(level, source.get()) || !isDestinationGateOpen(level.getServer(), destination.get())) {
                continue;
            }

            tickGate(level, source.get(), destination.get());
        }

        for (String destinationAddress : network.activeDestinationAddresses()) {
            Optional<StargateEntry> destination = network.findActiveEntryByAddress(destinationAddress);
            if (destination.isEmpty() || !destination.get().dimension().equals(level.dimension().location())) {
                continue;
            }

            tickKawoosh(level, destination.get());
            if (!isGateOpen(level, destination.get())) {
                continue;
            }

            tickIncomingGate(level, destination.get(), network.findIncomingSource(destinationAddress));
        }
    }

    private void tickKawoosh(ServerLevel level, StargateEntry gate) {
        if (!Config.KAWOOSH_DEATH.getAsBoolean()) {
            return;
        }

        if (!isKawooshing(level, gate)) {
            return;
        }

        long now = level.getGameTime();
        for (Entity entity : List.copyOf(level.getEntities((Entity)null, gateScanBounds(gate), this::canGateAffect))) {
            Long hitUntil = kawooshHits.get(entity.getUUID());
            if (hitUntil != null && hitUntil > now) {
                continue;
            }

            if (!isInsideKawoosh(entity.getBoundingBox().getCenter(), gate)) {
                continue;
            }

            LanteaCraft.LOGGER.info("Kawoosh from gate {} consumed {}", gate.address(), entity.getName().getString());
            entity.hurt(level.damageSources().genericKill(), Float.MAX_VALUE);
            if (!entity.isRemoved()) {
                entity.kill();
            }
            kawooshHits.put(entity.getUUID(), now + teleportCooldownTicks());
        }
    }

    private void tickGate(ServerLevel level, StargateEntry source, StargateEntry destination) {
        long now = level.getGameTime();
        for (Entity entity : List.copyOf(level.getEntities((Entity)null, gateScanBounds(source), this::canGateAffect))) {
            Long cooldownUntil = cooldowns.get(entity.getUUID());
            if (cooldownUntil != null && cooldownUntil > now) {
                continue;
            }

            Optional<GateLocalPosition> localPosition = localPositionInGate(entity.position(), source);
            if (localPosition.isEmpty()) {
                continue;
            }

            if (isIrisObstructed(level, source)) {
                pushOutOfIris(entity, source, localPosition.get());
                cooldowns.put(entity.getUUID(), now + teleportCooldownTicks());
                continue;
            }

            if (isMovingOutSourceFront(entity, source)) {
                passThroughBackOfSource(entity, source, localPosition.get());
            } else {
                teleport(entity, source, destination, localPosition.get());
            }
            cooldowns.put(entity.getUUID(), now + teleportCooldownTicks());
        }
    }

    private void tickIncomingGate(ServerLevel level, StargateEntry destination, Optional<StargateEntry> source) {
        long now = level.getGameTime();
        for (Entity entity : List.copyOf(level.getEntities((Entity)null, gateScanBounds(destination), this::canGateAffect))) {
            Long cooldownUntil = cooldowns.get(entity.getUUID());
            if (cooldownUntil != null && cooldownUntil > now) {
                continue;
            }

            Optional<GateLocalPosition> localPosition = localPositionInGate(entity.position(), destination);
            if (localPosition.isEmpty()) {
                continue;
            }

            if (Config.ALLOW_INCOMING_WORMHOLE_TRAVEL.getAsBoolean() && source.isPresent()) {
                teleport(entity, destination, source.get(), localPosition.get());
            } else {
                rejectIncomingTraveler(entity, destination, localPosition.get());
            }
            cooldowns.put(entity.getUUID(), now + teleportCooldownTicks());
        }
    }

    private void timeoutConnection(ServerLevel sourceLevel, StargateEntry source, StargateEntry destination) {
        LanteaCraft.LOGGER.info("Stargate wormhole from {} to {} timed out", source.address(), destination.address());
        StargateEventDispatcher.timeout(sourceLevel, source, destination);
    }

    private void tickIrisBarriers(ServerLevel level, StargateNetworkSavedData network) {
        for (StargateEntry gate : network.entries()) {
            if (!gate.dimension().equals(level.dimension().location()) || !isIrisObstructed(level, gate)) {
                continue;
            }

            for (Entity entity : List.copyOf(level.getEntities((Entity)null, gateScanBounds(gate), this::canGateAffect))) {
                Optional<GateLocalPosition> localPosition = localPositionInGate(entity.position(), gate);
                localPosition.ifPresent(position -> pushOutOfIris(entity, gate, position));
            }
        }
    }

    private Optional<GateLocalPosition> localPositionInGate(Vec3 entityPos, StargateEntry gate) {
        Vec3 baseCenter = Vec3.atBottomCenterOf(gate.basePos());
        Vec3 offset = entityPos.subtract(baseCenter);
        Vec3 right = step(gate.facing().getClockWise());
        Vec3 forward = step(gate.facing());

        double localX = offset.dot(right);
        double localY = entityPos.y - gate.basePos().getY();
        double localDepth = offset.dot(forward);

        if (Math.abs(localX) > HALF_INTERIOR_WIDTH || localY < MIN_INTERIOR_Y || localY > MAX_INTERIOR_Y || Math.abs(localDepth) > HALF_INTERIOR_DEPTH) {
            return Optional.empty();
        }

        return Optional.of(new GateLocalPosition(localX, localY, localDepth));
    }

    private boolean isInsideKawoosh(Vec3 entityPos, StargateEntry gate) {
        Vec3 baseCenter = Vec3.atBottomCenterOf(gate.basePos());
        Vec3 offset = entityPos.subtract(baseCenter);
        Vec3 right = step(gate.facing().getClockWise());
        Vec3 forward = step(gate.facing());

        double localX = offset.dot(right);
        double localY = entityPos.y - gate.basePos().getY();
        double localDepth = offset.dot(forward);
        double radialDistance = Math.sqrt(localX * localX + Math.pow(localY - KAWOOSH_CENTER_Y, 2.0D));

        return localDepth >= -HALF_INTERIOR_DEPTH
                && localDepth <= KAWOOSH_DEPTH
                && radialDistance <= KAWOOSH_RADIUS;
    }

    private void teleport(Entity entity, StargateEntry source, StargateEntry destination, GateLocalPosition localPosition) {
        MinecraftServer server = entity.getServer();
        if (server == null) {
            return;
        }

        ServerLevel destinationLevel = server.getLevel(dimensionKey(destination.dimension()));
        if (destinationLevel == null) {
            if (entity instanceof ServerPlayer player) {
                player.displayClientMessage(net.minecraft.network.chat.Component.literal("Destination Stargate dimension is not loaded."), false);
            }
            return;
        }

        if (isIrisObstructed(destinationLevel, destination)) {
            handleIrisCollision(entity, destination);
            return;
        }

        if (entity.level() != destinationLevel && !entity.canChangeDimensions(entity.level(), destinationLevel)) {
            return;
        }

        Vec3 target = Vec3.atBottomCenterOf(destination.basePos())
                .add(step(destination.facing().getClockWise()).scale(localPosition.x()))
                .add(step(destination.facing()).scale(EXIT_DISTANCE))
                .add(0.0D, localPosition.y(), 0.0D);
        Vec3 entryVelocity = entity.getDeltaMovement();
        Vec3 exitLook = transformSourceToDestination(entity.getLookAngle(), source, destination);
        Vec3 exitVelocity = transformSourceToDestination(entryVelocity, source, destination)
                .add(0.0D, entryVelocity.y, 0.0D);
        float exitYaw = yawFromVector(exitLook, destination.facing().toYRot());

        LanteaCraft.LOGGER.info("Teleporting {} from gate {} to gate {} at {}", entity.getName().getString(), source.address(), destination.address(), target);
        if (entity.teleportTo(destinationLevel, target.x, target.y, target.z, Set.<RelativeMovement>of(), exitYaw, entity.getXRot())) {
            entity.setDeltaMovement(exitVelocity);
            entity.hurtMarked = true;
            entity.hasImpulse = true;
            cooldowns.put(entity.getUUID(), destinationLevel.getGameTime() + teleportCooldownTicks());
            StargateEventDispatcher.entityTransit(entity, source, destination);
        }
    }

    private void passThroughBackOfSource(Entity entity, StargateEntry source, GateLocalPosition localPosition) {
        Vec3 target = frontOf(source, localPosition, EXIT_DISTANCE);
        Vec3 forward = step(source.facing());
        Vec3 velocity = entity.getDeltaMovement();
        double outwardSpeed = Math.max(Math.abs(velocity.dot(forward)), 0.35D);

        LanteaCraft.LOGGER.info("Passing {} through the back of outgoing gate {} to its front side", entity.getName().getString(), source.address());
        entity.teleportTo((ServerLevel)entity.level(), target.x, target.y, target.z, Set.<RelativeMovement>of(), entity.getYRot(), entity.getXRot());
        entity.setDeltaMovement(velocity.subtract(forward.scale(velocity.dot(forward))).add(forward.scale(outwardSpeed)));
        entity.hurtMarked = true;
        entity.hasImpulse = true;
        StargateEventDispatcher.localEntityEvent(entity, source, "outgoing_back");
    }

    private void rejectIncomingTraveler(Entity entity, StargateEntry destination, GateLocalPosition localPosition) {
        Vec3 target = frontOf(destination, localPosition, REJECTION_DISTANCE);
        Vec3 forward = step(destination.facing());
        Vec3 velocity = entity.getDeltaMovement();
        Vec3 tangentialVelocity = velocity.subtract(forward.scale(velocity.dot(forward)));
        double rejectionSpeed = Math.max(Math.abs(velocity.dot(forward)) * 1.35D, MIN_REJECTION_SPEED);

        LanteaCraft.LOGGER.info("Rejecting {} from incoming gate {} with speed {}", entity.getName().getString(), destination.address(), rejectionSpeed);
        entity.teleportTo((ServerLevel)entity.level(), target.x, target.y, target.z, Set.<RelativeMovement>of(), entity.getYRot(), entity.getXRot());
        entity.setDeltaMovement(tangentialVelocity.add(forward.scale(rejectionSpeed)).add(0.0D, 0.08D, 0.0D));
        entity.hurtMarked = true;
        entity.hasImpulse = true;
        StargateEventDispatcher.localEntityEvent(entity, destination, "incoming_rejected");
    }

    private Vec3 frontOf(StargateEntry gate, GateLocalPosition localPosition, double distance) {
        return Vec3.atBottomCenterOf(gate.basePos())
                .add(step(gate.facing().getClockWise()).scale(localPosition.x()))
                .add(step(gate.facing()).scale(distance))
                .add(0.0D, localPosition.y(), 0.0D);
    }

    private static ResourceKey<Level> dimensionKey(ResourceLocation dimension) {
        return ResourceKey.create(Registries.DIMENSION, dimension);
    }

    private static Vec3 step(Direction direction) {
        return new Vec3(direction.getStepX(), direction.getStepY(), direction.getStepZ());
    }

    private boolean isMovingOutSourceFront(Entity entity, StargateEntry source) {
        Vec3 forward = step(source.facing());
        double forwardVelocity = entity.getDeltaMovement().dot(forward);
        if (Math.abs(forwardVelocity) > 0.01D) {
            return forwardVelocity > 0.0D;
        }

        return localPositionInGate(entity.position(), source)
                .map(localPosition -> localPosition.depth() < 0.0D)
                .orElse(false);
    }

    private boolean canGateAffect(Entity entity) {
        return !entity.isRemoved() && !entity.isPassenger();
    }

    private AABB gateScanBounds(StargateEntry gate) {
        return new AABB(gate.basePos()).inflate(4.0D, 1.0D, 4.0D).expandTowards(0.0D, 7.0D, 0.0D);
    }

    private boolean isGateOpen(ServerLevel level, StargateEntry gate) {
        return level.getBlockEntity(gate.basePos()) instanceof StargateBaseBlockEntity base
                && base.isConnected()
                && !base.isDialing(level.getGameTime());
    }

    private boolean isDestinationGateOpen(MinecraftServer server, StargateEntry destination) {
        ServerLevel destinationLevel = server.getLevel(dimensionKey(destination.dimension()));
        return destinationLevel != null && isGateOpen(destinationLevel, destination);
    }

    private boolean isIrisObstructed(ServerLevel level, StargateEntry gate) {
        return level.getBlockEntity(gate.basePos()) instanceof StargateBaseBlockEntity base
                && base.isIrisObstructing();
    }

    private void handleIrisCollision(Entity entity, StargateEntry destination) {
        LanteaCraft.LOGGER.info("{} collided with the iris at gate {}", entity.getName().getString(), destination.address());
        if (entity instanceof ServerPlayer player && (player.getAbilities().instabuild || player.isInvulnerable())) {
            player.displayClientMessage(net.minecraft.network.chat.Component.translatable("message.lanteacraft.stargate_obstructed"), false);
            return;
        }

        entity.hurt(entity.level().damageSources().genericKill(), Float.MAX_VALUE);
        if (!entity.isRemoved()) {
            entity.kill();
        }
    }

    private void pushOutOfIris(Entity entity, StargateEntry gate, GateLocalPosition localPosition) {
        Vec3 forward = step(gate.facing());
        Vec3 target = Vec3.atBottomCenterOf(gate.basePos())
                .add(step(gate.facing().getClockWise()).scale(localPosition.x()))
                .add(forward.scale(localPosition.depth() >= 0.0D ? IRIS_BARRIER_DISTANCE : -IRIS_BARRIER_DISTANCE))
                .add(0.0D, localPosition.y(), 0.0D);
        Vec3 velocity = entity.getDeltaMovement();
        double forwardVelocity = velocity.dot(forward);
        Vec3 tangentialVelocity = velocity.subtract(forward.scale(forwardVelocity));
        double pushDirection = localPosition.depth() >= 0.0D ? 1.0D : -1.0D;

        entity.teleportTo((ServerLevel)entity.level(), target.x, target.y, target.z, Set.<RelativeMovement>of(), entity.getYRot(), entity.getXRot());
        entity.setDeltaMovement(tangentialVelocity.add(forward.scale(Math.max(Math.abs(forwardVelocity), 0.18D) * pushDirection)));
        entity.hurtMarked = true;
        entity.hasImpulse = true;
    }

    private boolean isKawooshing(ServerLevel level, StargateEntry gate) {
        if (!(level.getBlockEntity(gate.basePos()) instanceof StargateBaseBlockEntity base) || !base.isConnected()) {
            return false;
        }

        long dialingStart = base.dialingStartGameTime();
        if (dialingStart < 0L) {
            return false;
        }

        long openElapsed = level.getGameTime() - (dialingStart + base.dialingDurationTicks());
        return openElapsed >= 0L && openElapsed <= KAWOOSH_TICKS;
    }

    private static long wormholeTimeoutTicks() {
        return Config.GATE_TIMEOUT_SECONDS.get() * 20L;
    }

    private static int teleportCooldownTicks() {
        return Config.GATE_TELEPORT_COOLDOWN_TICKS.get();
    }

    private static Vec3 transformSourceToDestination(Vec3 vector, StargateEntry source, StargateEntry destination) {
        Vec3 sourceRight = step(source.facing().getClockWise());
        Vec3 sourceForward = step(source.facing());
        Vec3 destinationRight = step(destination.facing().getClockWise());
        Vec3 destinationForward = step(destination.facing());

        double right = vector.dot(sourceRight);
        double forward = vector.dot(sourceForward);
        return destinationRight.scale(right).subtract(destinationForward.scale(forward));
    }

    private static float yawFromVector(Vec3 vector, float fallbackYaw) {
        Vec3 horizontal = new Vec3(vector.x, 0.0D, vector.z);
        if (horizontal.lengthSqr() < 1.0E-6D) {
            return fallbackYaw;
        }

        return (float)Math.toDegrees(Math.atan2(-horizontal.x, horizontal.z));
    }

    private record GateLocalPosition(double x, double y, double depth) {
    }
}
