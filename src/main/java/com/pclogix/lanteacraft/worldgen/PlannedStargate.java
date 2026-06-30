package com.pclogix.lanteacraft.worldgen;

import com.pclogix.lanteacraft.gate.StargateVariant;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;

public record PlannedStargate(
        String address,
        ResourceLocation dimension,
        BlockPos villagePos,
        BlockPos basePos,
        Direction facing,
        StargateVariant variant) {
}
