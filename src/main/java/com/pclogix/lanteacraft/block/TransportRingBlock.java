package com.pclogix.lanteacraft.block;

import com.mojang.serialization.MapCodec;
import com.pclogix.lanteacraft.block.entity.TransportRingBlockEntity;
import com.pclogix.lanteacraft.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class TransportRingBlock extends HorizontalDirectionalBlock implements EntityBlock {
    public static final MapCodec<TransportRingBlock> CODEC = simpleCodec(TransportRingBlock::new);
    private static final VoxelShape RING_SHAPE = Shapes.or(
            box(0.0D, 0.0D, 0.0D, 16.0D, 3.0D, 16.0D),
            box(0.0D, 3.0D, 0.0D, 3.0D, 16.0D, 16.0D),
            box(13.0D, 3.0D, 0.0D, 16.0D, 16.0D, 16.0D),
            box(3.0D, 3.0D, 0.0D, 13.0D, 16.0D, 3.0D),
            box(3.0D, 3.0D, 13.0D, 13.0D, 16.0D, 16.0D));

    public TransportRingBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, net.minecraft.core.Direction.NORTH));
    }

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TransportRingBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        if (blockEntityType != ModBlockEntities.TRANSPORT_RING.get()) {
            return null;
        }

        return (tickLevel, pos, tickState, blockEntity) -> TransportRingBlockEntity.tick(tickLevel, pos, tickState, (TransportRingBlockEntity)blockEntity);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        if (level.getBlockEntity(pos) instanceof TransportRingBlockEntity ring && ring.activate(player)) {
            return InteractionResult.CONSUME;
        }

        player.displayClientMessage(Component.literal("No paired transport ring platform found."), true);
        return InteractionResult.CONSUME;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return RING_SHAPE;
    }
}
