package com.pclogix.lanteacraft.worldgen;

import com.pclogix.lanteacraft.gate.StargateAddress;
import com.pclogix.lanteacraft.gate.StargateVariant;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;

public final class StargateVillagePlanner {
    private static final int RARITY_ONE_IN = 6;
    private static final int VILLAGE_OFFSET_BLOCKS = 48;
    private static final int PLANNED_GATE_Y = 64;
    private static final Direction[] HORIZONTAL_DIRECTIONS = {
            Direction.NORTH,
            Direction.EAST,
            Direction.SOUTH,
            Direction.WEST
    };

    private StargateVillagePlanner() {
    }

    public static boolean shouldHaveGate(ServerLevel level, BlockPos villagePos) {
        return Math.floorMod(mix(level.getSeed(), villagePos.getX(), villagePos.getZ()), RARITY_ONE_IN) == 0;
    }

    public static PlannedStargate plan(ServerLevel level, BlockPos villagePos) {
        long state = mix(level.getSeed() ^ 0x535447415445L, villagePos.getX(), villagePos.getZ());
        Direction facing = HORIZONTAL_DIRECTIONS[Math.floorMod((int)state, HORIZONTAL_DIRECTIONS.length)];
        StargateVariant[] variants = StargateVariant.values();
        StargateVariant variant = variants[Math.floorMod((int)(state >>> 8), variants.length)];
        BlockPos basePos = villagePos.relative(facing, VILLAGE_OFFSET_BLOCKS).atY(PLANNED_GATE_Y);
        String address = StargateAddress.forGate(level, basePos, StargateAddress.OVERWORLD_GLYPH);
        return new PlannedStargate(address, level.dimension().location(), villagePos.immutable(), basePos.immutable(), facing, variant);
    }

    public static ChunkPos placementChunk(PlannedStargate plan) {
        return new ChunkPos(plan.basePos());
    }

    private static int mix(long seed, int x, int z) {
        long state = seed;
        state ^= (long)x * 0x9E3779B97F4A7C15L;
        state = Long.rotateLeft(state, 27);
        state ^= (long)z * 0xC2B2AE3D27D4EB4FL;
        state ^= state >>> 33;
        state *= 0xff51afd7ed558ccdL;
        state ^= state >>> 33;
        return (int)state;
    }
}
