package com.pclogix.lanteacraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BrokenColumnBlock extends DecorativeFacingBlock {
    public static final MapCodec<BrokenColumnBlock> CODEC = simpleCodec(BrokenColumnBlock::new);
    public static final BooleanProperty UPSIDE_DOWN = BooleanProperty.create("upside_down");

    public BrokenColumnBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(UPSIDE_DOWN, false));
    }

    @Override
    protected MapCodec<? extends BrokenColumnBlock> codec() {
        return CODEC;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection().getOpposite())
                .setValue(UPSIDE_DOWN, context.getClickedFace() == Direction.DOWN);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(UPSIDE_DOWN)
                ? box(2, 1, 2, 14, 16, 14)
                : box(2, 0, 2, 14, 15, 14);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<net.minecraft.world.level.block.Block, BlockState> builder) {
        builder.add(FACING, UPSIDE_DOWN);
    }
}
