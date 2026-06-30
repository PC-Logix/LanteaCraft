package com.pclogix.lanteacraft.block.entity;

import com.pclogix.lanteacraft.LanteaCraft;
import com.pclogix.lanteacraft.registry.ModBlockEntities;
import com.pclogix.lanteacraft.registry.ModBlocks;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class TransportRingBlockEntity extends BlockEntity {
    private static final int ACTIVATION_COOLDOWN_TICKS = 120;
    private static final int TRANSPORT_AT_TICK = 50;
    private static final double HALF_PLATFORM_WIDTH = 2.5D;
    private static final double BEAM_HEIGHT = 3.5D;

    private long cooldownUntil;
    private long animationStartGameTime = -1L;
    private BlockPos animationDestination;
    private boolean transported;
    private boolean dispatchOnTransport;

    public TransportRingBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.TRANSPORT_RING.get(), pos, blockState);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, TransportRingBlockEntity blockEntity) {
        if (!level.isClientSide && level instanceof ServerLevel serverLevel) {
            blockEntity.serverTick(serverLevel);
        }
    }

    public boolean activate(Player activator) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return false;
        }

        long now = serverLevel.getGameTime();
        if (cooldownUntil > now) {
            return true;
        }

        RingPlatform source = new RingPlatform(worldPosition);
        Optional<RingPlatform> destination = findDestination(serverLevel, source);
        if (destination.isEmpty()) {
            return false;
        }

        beginAnimation(serverLevel, destination.get().base(), true);
        if (serverLevel.getBlockEntity(destination.get().base()) instanceof TransportRingBlockEntity destinationRing) {
            destinationRing.beginAnimation(serverLevel, source.base(), false);
        }
        serverLevel.playSound(null, source.base(), SoundEvents.BEACON_ACTIVATE, SoundSource.BLOCKS, 1.0F, 1.35F);
        armCooldown(serverLevel, source, destination.get());
        return true;
    }

    private void serverTick(ServerLevel level) {
        if (!isAnimating()) {
            return;
        }

        long elapsed = level.getGameTime() - animationStartGameTime;
        if (!transported && elapsed >= TRANSPORT_AT_TICK) {
            transported = true;
            if (dispatchOnTransport && animationDestination != null && level.getBlockEntity(animationDestination) instanceof TransportRingBlockEntity) {
                int moved = teleportEntities(level, new RingPlatform(worldPosition), new RingPlatform(animationDestination));
                LanteaCraft.LOGGER.info("Transport rings moved {} entities from {} to {}", moved, worldPosition, animationDestination);
                level.playSound(null, worldPosition, SoundEvents.ENDERMAN_TELEPORT, SoundSource.BLOCKS, 1.0F, 1.0F);
                level.playSound(null, animationDestination, SoundEvents.ENDERMAN_TELEPORT, SoundSource.BLOCKS, 1.0F, 1.0F);
            }
            sync();
        }

        if (elapsed >= ACTIVATION_COOLDOWN_TICKS) {
            animationStartGameTime = -1L;
            animationDestination = null;
            transported = false;
            dispatchOnTransport = false;
            sync();
        }
    }

    private void beginAnimation(ServerLevel level, BlockPos destination, boolean dispatchOnTransport) {
        animationStartGameTime = level.getGameTime();
        animationDestination = destination.immutable();
        transported = false;
        this.dispatchOnTransport = dispatchOnTransport;
        sync();
    }

    private Optional<RingPlatform> findDestination(ServerLevel level, RingPlatform source) {
        int chunkMinX = Math.floorDiv(source.base().getX(), 16) * 16;
        int chunkMinZ = Math.floorDiv(source.base().getZ(), 16) * 16;
        BlockPos searchMin = new BlockPos(chunkMinX, level.getMinBuildHeight(), chunkMinZ);
        BlockPos searchMax = new BlockPos(chunkMinX + 15, level.getMaxBuildHeight() - 1, chunkMinZ + 15);
        return BlockPos.betweenClosedStream(searchMin, searchMax)
                .filter(pos -> level.getBlockState(pos).is(ModBlocks.TRANSPORT_RING.get()))
                .map(BlockPos::immutable)
                .filter(pos -> !pos.equals(source.base()))
                .map(RingPlatform::new)
                .filter(platform -> !isBusy(level, platform))
                .min(Comparator.comparingDouble(platform -> platform.base().distSqr(source.base())));
    }

    private int teleportEntities(ServerLevel level, RingPlatform source, RingPlatform destination) {
        AABB beam = beamBounds(source);
        Vec3 sourceCenter = beamCenter(source);
        Vec3 destinationCenter = beamCenter(destination);
        List<Entity> travelers = level.getEntities((Entity)null, beam, this::canTransport);

        int moved = 0;
        for (Entity entity : travelers) {
            Vec3 offset = entity.position().subtract(sourceCenter);
            Vec3 target = destinationCenter.add(offset);
            Vec3 velocity = entity.getDeltaMovement();
            entity.teleportTo(target.x, target.y, target.z);
            entity.setDeltaMovement(velocity);
            entity.hurtMarked = true;
            moved++;
        }

        return moved;
    }

    private boolean canTransport(Entity entity) {
        return !entity.isRemoved() && !entity.isPassenger();
    }

    private void armCooldown(ServerLevel level, RingPlatform source, RingPlatform destination) {
        long until = level.getGameTime() + ACTIVATION_COOLDOWN_TICKS;
        setPlatformCooldown(level, source, until);
        setPlatformCooldown(level, destination, until);
    }

    private void setPlatformCooldown(ServerLevel level, RingPlatform platform, long until) {
        if (level.getBlockEntity(platform.base()) instanceof TransportRingBlockEntity ring) {
            ring.cooldownUntil = until;
            ring.setChanged();
        }
    }

    private boolean isBusy(ServerLevel level, RingPlatform platform) {
        if (level.getBlockEntity(platform.base()) instanceof TransportRingBlockEntity ring) {
            return ring.cooldownUntil > level.getGameTime() || ring.isAnimating();
        }

        return false;
    }

    public boolean isAnimating() {
        return animationStartGameTime >= 0L;
    }

    public long animationStartGameTime() {
        return animationStartGameTime;
    }

    public BlockPos animationDestination() {
        return animationDestination;
    }

    public boolean hasTransported() {
        return transported;
    }

    public boolean dispatchesOnTransport() {
        return dispatchOnTransport;
    }

    private static AABB beamBounds(RingPlatform platform) {
        BlockPos base = platform.base();
        return new AABB(
                base.getX() + 0.5D - HALF_PLATFORM_WIDTH,
                base.getY(),
                base.getZ() + 0.5D - HALF_PLATFORM_WIDTH,
                base.getX() + 0.5D + HALF_PLATFORM_WIDTH,
                base.getY() + BEAM_HEIGHT,
                base.getZ() + 0.5D + HALF_PLATFORM_WIDTH);
    }

    private static Vec3 beamCenter(RingPlatform platform) {
        return new Vec3(platform.base().getX() + 0.5D, platform.base().getY(), platform.base().getZ() + 0.5D);
    }

    private void sync() {
        setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        saveAnimation(tag);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        loadAnimation(tag);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        saveAnimation(tag);
        return tag;
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    private void saveAnimation(CompoundTag tag) {
        tag.putLong("cooldownUntil", cooldownUntil);
        tag.putLong("animationStartGameTime", animationStartGameTime);
        tag.putBoolean("transported", transported);
        tag.putBoolean("dispatchOnTransport", dispatchOnTransport);
        if (animationDestination != null) {
            tag.putInt("destinationX", animationDestination.getX());
            tag.putInt("destinationY", animationDestination.getY());
            tag.putInt("destinationZ", animationDestination.getZ());
        }
    }

    private void loadAnimation(CompoundTag tag) {
        cooldownUntil = tag.getLong("cooldownUntil");
        animationStartGameTime = tag.getLong("animationStartGameTime");
        transported = tag.getBoolean("transported");
        dispatchOnTransport = tag.getBoolean("dispatchOnTransport");
        animationDestination = null;
        if (tag.contains("destinationX") && tag.contains("destinationY") && tag.contains("destinationZ")) {
            animationDestination = new BlockPos(tag.getInt("destinationX"), tag.getInt("destinationY"), tag.getInt("destinationZ"));
        }
    }

    private record RingPlatform(BlockPos base) {
    }
}
