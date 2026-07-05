package com.pclogix.lanteacraft.block;

import com.pclogix.lanteacraft.Config;
import com.pclogix.lanteacraft.block.entity.DhdBlockEntity;
import com.pclogix.lanteacraft.client.DhdClientHooks;
import com.pclogix.lanteacraft.gate.StargateMultiblock;
import com.pclogix.lanteacraft.gate.StargateVariant;
import com.mojang.serialization.MapCodec;
import com.pclogix.lanteacraft.menu.DhdPowerMenu;
import com.pclogix.lanteacraft.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class DhdBlock extends HorizontalDirectionalBlock implements EntityBlock {
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
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new DhdBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        if (blockEntityType != ModBlockEntities.DHD.get()) {
            return null;
        }

        return (tickLevel, pos, tickState, blockEntity) -> DhdBlockEntity.tick(tickLevel, pos, tickState, (DhdBlockEntity)blockEntity);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (isDialFace(hitResult)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        if (level.getBlockEntity(pos) instanceof DhdBlockEntity dhd && dhd.crystalItems().isItemValid(0, stack)) {
            if (!level.isClientSide && dhd.insertCrystal(stack)) {
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
        if (isDialFace(hitResult)) {
            if (level.isClientSide) {
                DhdClientHooks.open(pos, variant);
            } else {
                pulse(level, pos);
            }

            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer && level.getBlockEntity(pos) instanceof DhdBlockEntity dhd) {
            serverPlayer.openMenu(new MenuProvider() {
                @Override
                public Component getDisplayName() {
                    return Component.translatable("screen.lanteacraft.dhd_power");
                }

                @Override
                public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player menuPlayer) {
                    return new DhdPowerMenu(containerId, inventory, dhd);
                }
            }, buffer -> buffer.writeBlockPos(pos));
        }

        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    private static boolean isDialFace(BlockHitResult hitResult) {
        return hitResult.getDirection() == Direction.UP;
    }

    public static void pulse(Level level, BlockPos pos) {
        setActive(level, pos, true);
        level.scheduleTick(pos, level.getBlockState(pos).getBlock(), ACTIVE_TICKS);
    }

    public static void setActive(Level level, BlockPos pos, boolean active) {
        BlockState state = level.getBlockState(pos);
        if (!(state.getBlock() instanceof DhdBlock) || !state.hasProperty(ACTIVE)) {
            return;
        }

        if (state.getValue(ACTIVE) != active) {
            level.setBlock(pos, state.setValue(ACTIVE, active), Block.UPDATE_ALL);
        }
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (state.getValue(ACTIVE) && !hasOpenLinkedGate(level, pos)) {
            level.setBlock(pos, state.setValue(ACTIVE, false), Block.UPDATE_ALL);
        }
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock()) && level.getBlockEntity(pos) instanceof DhdBlockEntity dhd) {
            ItemStack crystal = dhd.removeCrystal();
            if (!crystal.isEmpty()) {
                Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), crystal);
            }
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    private static boolean hasOpenLinkedGate(ServerLevel level, BlockPos pos) {
        return StargateMultiblock.findNearestEntry(level, pos, Config.DHD_SEARCH_RADIUS.get())
                .map(entry -> level.getBlockState(entry.basePos()))
                .filter(state -> state.hasProperty(StargateBaseBlock.WORMHOLE_OPEN))
                .map(state -> state.getValue(StargateBaseBlock.WORMHOLE_OPEN))
                .orElse(false);
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
