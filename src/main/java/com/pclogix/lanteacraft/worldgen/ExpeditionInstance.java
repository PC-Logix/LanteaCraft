package com.pclogix.lanteacraft.worldgen;

import com.pclogix.lanteacraft.gate.StargateVariant;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

public record ExpeditionInstance(
        String address,
        int slot,
        int tier,
        BlockPos basePos,
        Direction facing,
        StargateVariant variant,
        boolean generated,
        boolean rewardClaimed,
        String returnAddress,
        List<BlockPos> combatRoomCenters,
        boolean rewardUnlocked,
        long layoutSeed,
        BlockPos rewardDoorPos,
        Direction rewardDoorFacing) {
    public ExpeditionInstance withGenerated(boolean generated) {
        return new ExpeditionInstance(address, slot, tier, basePos, facing, variant, generated, rewardClaimed, returnAddress, combatRoomCenters, rewardUnlocked, layoutSeed, rewardDoorPos, rewardDoorFacing);
    }

    public ExpeditionInstance withRewardClaimed(boolean rewardClaimed) {
        return new ExpeditionInstance(address, slot, tier, basePos, facing, variant, generated, rewardClaimed, returnAddress, combatRoomCenters, rewardUnlocked, layoutSeed, rewardDoorPos, rewardDoorFacing);
    }

    public ExpeditionInstance withReturnAddress(String returnAddress) {
        return new ExpeditionInstance(address, slot, tier, basePos, facing, variant, generated, rewardClaimed, normalize(returnAddress), combatRoomCenters, rewardUnlocked, layoutSeed, rewardDoorPos, rewardDoorFacing);
    }

    public ExpeditionInstance withTrialState(List<BlockPos> combatRoomCenters, boolean rewardUnlocked) {
        return new ExpeditionInstance(address, slot, tier, basePos, facing, variant, generated, rewardClaimed, returnAddress, List.copyOf(combatRoomCenters), rewardUnlocked, layoutSeed, rewardDoorPos, rewardDoorFacing);
    }

    public ExpeditionInstance withRewardDoor(BlockPos rewardDoorPos, Direction rewardDoorFacing) {
        return new ExpeditionInstance(address, slot, tier, basePos, facing, variant, generated, rewardClaimed, returnAddress, combatRoomCenters, rewardUnlocked, layoutSeed, rewardDoorPos == null ? null : rewardDoorPos.immutable(), rewardDoorFacing == null ? Direction.SOUTH : rewardDoorFacing);
    }

    private static String normalize(String address) {
        return address == null ? "" : address.trim().toUpperCase();
    }
}
