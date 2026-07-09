package com.pclogix.lanteacraft.block.entity;

import com.pclogix.lanteacraft.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ObeliskBlockEntity extends BlockEntity {
    public ObeliskBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.OBELISK.get(), pos, blockState);
    }
}
