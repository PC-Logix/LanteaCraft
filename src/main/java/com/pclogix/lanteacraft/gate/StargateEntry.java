package com.pclogix.lanteacraft.gate;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;

public record StargateEntry(String address, ResourceLocation dimension, BlockPos basePos, Direction facing) {
}
