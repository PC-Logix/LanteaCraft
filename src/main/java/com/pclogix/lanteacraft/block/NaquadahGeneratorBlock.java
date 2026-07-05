package com.pclogix.lanteacraft.block;

import com.mojang.serialization.MapCodec;
import com.pclogix.lanteacraft.block.entity.NaquadahGeneratorBlockEntity;
import com.pclogix.lanteacraft.menu.NaquadahGeneratorMenu;
import com.pclogix.lanteacraft.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;

public class NaquadahGeneratorBlock extends HorizontalDirectionalBlock implements EntityBlock {
    public static final MapCodec<NaquadahGeneratorBlock> CODEC = simpleCodec(NaquadahGeneratorBlock::new);
    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");

    public NaquadahGeneratorBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(ACTIVE, false));
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

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new NaquadahGeneratorBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        if (blockEntityType != ModBlockEntities.NAQUADAH_GENERATOR.get()) {
            return null;
        }

        return (tickLevel, pos, tickState, blockEntity) -> NaquadahGeneratorBlockEntity.tick(tickLevel, pos, tickState, (NaquadahGeneratorBlockEntity)blockEntity);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (level.getBlockEntity(pos) instanceof NaquadahGeneratorBlockEntity generator && (generator.isFuel(stack) || generator.isPowerCrystal(stack))) {
            if (!level.isClientSide && generator.insertFuel(stack)) {
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (level.getBlockEntity(pos) instanceof NaquadahGeneratorBlockEntity generator) {
            if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
                serverPlayer.openMenu(new MenuProvider() {
                    @Override
                    public Component getDisplayName() {
                        return Component.translatable("screen.lanteacraft.naquadah_generator");
                    }

                    @Override
                    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player menuPlayer) {
                        return new NaquadahGeneratorMenu(containerId, inventory, generator);
                    }
                }, buffer -> buffer.writeBlockPos(pos));
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        return InteractionResult.PASS;
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock()) && level.getBlockEntity(pos) instanceof NaquadahGeneratorBlockEntity generator) {
            for (int slot = 0; slot < generator.fuelItems().getSlots(); slot++) {
                ItemStack stack = generator.removeItem(slot);
                if (!stack.isEmpty()) {
                    Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), stack);
                }
            }
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }
}
