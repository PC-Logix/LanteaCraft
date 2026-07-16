package com.pclogix.lanteacraft.worldgen;

import com.pclogix.lanteacraft.gate.StargateVariant;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;

public record ExpeditionInstance(
        String address,
        int slot,
        int tier,
        BlockPos basePos,
        ResourceLocation dimension,
        Direction facing,
        StargateVariant variant,
        boolean generated,
        boolean rewardClaimed,
        String returnAddress,
        List<BlockPos> combatRoomCenters,
        boolean rewardUnlocked,
        long layoutSeed,
        List<ExpeditionRewardDoor> rewardDoors,
        BlockPos rewardDoorPos,
        Direction rewardDoorFacing) {
    public ExpeditionInstance withGenerated(boolean generated) {
        return new ExpeditionInstance(address, slot, tier, basePos, dimension, facing, variant, generated, rewardClaimed, returnAddress, combatRoomCenters, rewardUnlocked, layoutSeed, rewardDoors, rewardDoorPos, rewardDoorFacing);
    }

    public ExpeditionInstance withRewardClaimed(boolean rewardClaimed) {
        return new ExpeditionInstance(address, slot, tier, basePos, dimension, facing, variant, generated, rewardClaimed, returnAddress, combatRoomCenters, rewardUnlocked, layoutSeed, rewardDoors, rewardDoorPos, rewardDoorFacing);
    }

    public ExpeditionInstance withReturnAddress(String returnAddress) {
        return new ExpeditionInstance(address, slot, tier, basePos, dimension, facing, variant, generated, rewardClaimed, normalize(returnAddress), combatRoomCenters, rewardUnlocked, layoutSeed, rewardDoors, rewardDoorPos, rewardDoorFacing);
    }

    public ExpeditionInstance withTrialState(List<BlockPos> combatRoomCenters, boolean rewardUnlocked) {
        return new ExpeditionInstance(address, slot, tier, basePos, dimension, facing, variant, generated, rewardClaimed, returnAddress, List.copyOf(combatRoomCenters), rewardUnlocked, layoutSeed, rewardDoors, rewardDoorPos, rewardDoorFacing);
    }

    public ExpeditionInstance withRewardDoor(BlockPos rewardDoorPos, Direction rewardDoorFacing) {
        ExpeditionRewardDoor door = new ExpeditionRewardDoor(rewardDoorPos, rewardDoorFacing);
        return new ExpeditionInstance(address, slot, tier, basePos, dimension, facing, variant, generated, rewardClaimed, returnAddress, combatRoomCenters, rewardUnlocked, layoutSeed, List.of(door), door.pos(), door.facing());
    }

    public ExpeditionInstance withRewardDoors(List<ExpeditionRewardDoor> rewardDoors) {
        List<ExpeditionRewardDoor> doors = List.copyOf(rewardDoors);
        ExpeditionRewardDoor primary = doors.isEmpty() ? null : doors.getFirst();
        return new ExpeditionInstance(address, slot, tier, basePos, dimension, facing, variant, generated, rewardClaimed, returnAddress, combatRoomCenters, rewardUnlocked, layoutSeed, doors, primary == null ? null : primary.pos(), primary == null ? Direction.SOUTH : primary.facing());
    }

    private static String normalize(String address) {
        return address == null ? "" : address.trim().toUpperCase();
    }
}
