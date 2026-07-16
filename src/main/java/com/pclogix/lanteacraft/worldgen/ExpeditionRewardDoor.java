package com.pclogix.lanteacraft.worldgen;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

public record ExpeditionRewardDoor(BlockPos pos, Direction facing) {
    public ExpeditionRewardDoor {
        pos = pos.immutable();
        facing = facing == null ? Direction.SOUTH : facing;
    }
}
