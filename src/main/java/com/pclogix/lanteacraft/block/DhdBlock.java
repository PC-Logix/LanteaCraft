package com.pclogix.lanteacraft.block;

import com.pclogix.lanteacraft.client.DhdClientHooks;
import com.pclogix.lanteacraft.gate.StargateVariant;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class DhdBlock extends HorizontalDirectionalBlock {
    public static final MapCodec<DhdBlock> CODEC = simpleCodec(DhdBlock::new);
    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");
    private static final int ACTIVE_TICKS = 20;
    private static final VoxelShape NO_SNOW_COLLISION_SHAPE = box(0.0D, 0.0D, 0.0D, 16.0D, 15.0D, 16.0D);
    private final StargateVariant variant;

    public DhdBlock(Properties properties) {
        this(properties, StargateVariant.MILKY_WAY);
    }

    public DhdBlock(Properties properties, StargateVariant variant) {
        super(properties);
        this.variant = variant;
        registerDefaultState(stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(ACTIVE, false));
    }

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<net.minecraft.world.level.block.Block, BlockState> builder) {
        builder.add(FACING, ACTIVE);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    public StargateVariant variant() {
        return variant;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (level.isClientSide) {
            DhdClientHooks.open(pos, variant);
        } else {
            pulse(level, pos);
        }

        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    public static void pulse(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (!(state.getBlock() instanceof DhdBlock) || !state.hasProperty(ACTIVE)) {
            return;
        }

        level.setBlock(pos, state.setValue(ACTIVE, true), Block.UPDATE_ALL);
        level.scheduleTick(pos, state.getBlock(), ACTIVE_TICKS);
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (state.getValue(ACTIVE)) {
            level.setBlock(pos, state.setValue(ACTIVE, false), Block.UPDATE_ALL);
        }
    }

    @Override
    protected VoxelShape getBlockSupportShape(BlockState state, BlockGetter level, BlockPos pos) {
        return Shapes.empty();
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return NO_SNOW_COLLISION_SHAPE;
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
        return false;
    }
}
