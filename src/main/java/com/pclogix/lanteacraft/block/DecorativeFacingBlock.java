package com.pclogix.lanteacraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import com.pclogix.lanteacraft.registry.ModBlocks;

public class DecorativeFacingBlock extends HorizontalDirectionalBlock {
    public static final MapCodec<DecorativeFacingBlock> CODEC = simpleCodec(DecorativeFacingBlock::new);

    public DecorativeFacingBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, net.minecraft.core.BlockPos pos, CollisionContext context) {
        if (state.is(ModBlocks.CANOPIC_JAR.get())) return box(4, 0, 4, 12, 14, 12);
        if (state.is(ModBlocks.SCARAB_IDOL.get())) {
            return state.getValue(FACING).getAxis() == Direction.Axis.X
                    ? box(5, 0, 1, 11, 10, 15)
                    : box(1, 0, 5, 15, 10, 11);
        }
        if (state.is(ModBlocks.OFFERING_ALTAR.get())) return box(1, 0, 1, 15, 13, 15);
        if (state.is(ModBlocks.BROKEN_COLUMN.get())) return box(2, 0, 2, 14, 15, 14);
        if (state.is(ModBlocks.SPHINX_HEAD.get())) return box(1, 0, 0, 15, 14, 15);
        if (state.is(ModBlocks.SITTING_CAT_STATUE.get())) {
            return state.getValue(FACING).getAxis() == Direction.Axis.X
                    ? box(1, 0, 2, 14, 20, 14)
                    : box(2, 0, 1, 14, 20, 14);
        }
        if (state.is(ModBlocks.BRONZE_SITTING_CAT_STATUE.get())) {
            return state.getValue(FACING).getAxis() == Direction.Axis.X
                    ? box(1, 0, 2, 14, 18, 14)
                    : box(2, 0, 1, 14, 18, 14);
        }
        if (state.is(ModBlocks.RECLINING_CAT_STATUE.get())) {
            return state.getValue(FACING).getAxis() == Direction.Axis.X
                    ? box(1, 0, 0, 14, 13, 16)
                    : box(0, 0, 1, 16, 13, 14);
        }
        return super.getShape(state, level, pos, context);
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, net.minecraft.core.BlockPos pos, CollisionContext context) {
        return getShape(state, level, pos, context);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<net.minecraft.world.level.block.Block, BlockState> builder) {
        builder.add(FACING);
    }
}
