package com.pclogix.lanteacraft.block;

import com.mojang.serialization.MapCodec;
import com.pclogix.lanteacraft.block.entity.ObeliskBlockEntity;
import com.pclogix.lanteacraft.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class ObeliskBlock extends Block implements EntityBlock {
    public static final MapCodec<ObeliskBlock> CODEC = simpleCodec(ObeliskBlock::new);
    private static final VoxelShape SHAPE = box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);

    public ObeliskBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends Block> codec() {
        return CODEC;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        for (int offset = 1; offset <= 4; offset++) {
            if (!ObeliskCollisionBlock.isReplaceableForObelisk(level, pos.above(offset))) {
                return null;
            }
        }
        return defaultBlockState();
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (!level.isClientSide) {
            for (int offset = 1; offset <= 4; offset++) {
                level.setBlock(pos.above(offset), ModBlocks.OBELISK_COLLISION.get().defaultBlockState()
                        .setValue(ObeliskCollisionBlock.OFFSET, offset), UPDATE_ALL);
            }
        }
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock())) {
            for (int offset = 1; offset <= 4; offset++) {
                BlockPos markerPos = pos.above(offset);
                BlockState markerState = level.getBlockState(markerPos);
                if (markerState.is(ModBlocks.OBELISK_COLLISION.get())) {
                    level.removeBlock(markerPos, false);
                }
            }
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ObeliskBlockEntity(pos, state);
    }
}
