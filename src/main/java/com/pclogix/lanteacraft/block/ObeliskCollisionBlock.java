package com.pclogix.lanteacraft.block;

import com.mojang.serialization.MapCodec;
import com.pclogix.lanteacraft.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ObeliskCollisionBlock extends Block {
    public static final MapCodec<ObeliskCollisionBlock> CODEC = simpleCodec(ObeliskCollisionBlock::new);
    public static final IntegerProperty OFFSET = IntegerProperty.create("offset", 1, 4);
    private static final VoxelShape SHAPE = box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);

    public ObeliskCollisionBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(OFFSET, 1));
    }

    @Override
    protected MapCodec<? extends Block> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(OFFSET);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock())) {
            BlockPos basePos = pos.below(state.getValue(OFFSET));
            if (level.getBlockState(basePos).is(ModBlocks.OBELISK.get())) {
                level.destroyBlock(basePos, true);
            }
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    public static boolean isReplaceableForObelisk(LevelAccessor level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        return state.canBeReplaced() || state.is(ModBlocks.OBELISK_COLLISION.get());
    }
}
