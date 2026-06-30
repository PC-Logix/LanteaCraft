package com.pclogix.lanteacraft.gate;

import java.util.Optional;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;

public record StargateRecord(
        UUID gateId,
        String address,
        ResourceLocation dimension,
        BlockPos basePos,
        Direction facing,
        StargateVariant variant,
        StargateStatus status,
        String origin) {

    public Optional<StargateEntry> activeEntry() {
        return status == StargateStatus.ACTIVE ? Optional.of(entry()) : Optional.empty();
    }

    public StargateEntry entry() {
        return new StargateEntry(address, dimension, basePos, facing);
    }

    public StargateRecord withStatus(StargateStatus newStatus) {
        return new StargateRecord(gateId, address, dimension, basePos, facing, variant, newStatus, origin);
    }

    public StargateRecord withEndpoint(ResourceLocation newDimension, BlockPos newBasePos, Direction newFacing, StargateVariant newVariant, StargateStatus newStatus, String newOrigin) {
        return new StargateRecord(
                gateId,
                address,
                newDimension,
                newBasePos.immutable(),
                newFacing,
                newVariant == null ? variant : newVariant,
                newStatus,
                newOrigin == null || newOrigin.isBlank() ? origin : newOrigin);
    }
}
