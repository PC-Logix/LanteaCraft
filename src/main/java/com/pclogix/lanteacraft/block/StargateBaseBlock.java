package com.pclogix.lanteacraft.block;

import com.pclogix.lanteacraft.LanteaCraft;
import com.pclogix.lanteacraft.Config;
import com.pclogix.lanteacraft.gate.StargateAddressMessage;
import com.pclogix.lanteacraft.gate.StargateCamouflage;
import com.pclogix.lanteacraft.gate.StargateMultiblock;
import com.pclogix.lanteacraft.gate.StargateVariant;
import com.pclogix.lanteacraft.item.IrisUpgradeItem;
import com.pclogix.lanteacraft.menu.StargateMenu;
import com.pclogix.lanteacraft.registry.ModBlockEntities;
import com.mojang.serialization.MapCodec;
import com.pclogix.lanteacraft.block.entity.StargateBaseBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class StargateBaseBlock extends HorizontalDirectionalBlock implements EntityBlock {
    public static final MapCodec<StargateBaseBlock> CODEC = simpleCodec(StargateBaseBlock::new);
    public static final BooleanProperty ASSEMBLED = BooleanProperty.create("assembled");
    private static final VoxelShape NO_SNOW_COLLISION_SHAPE = box(0.0D, 0.0D, 0.0D, 16.0D, 15.0D, 16.0D);
    private final StargateVariant variant;

    public StargateBaseBlock(Properties properties) {
        this(properties, StargateVariant.MILKY_WAY);
    }

    public StargateBaseBlock(Properties properties, StargateVariant variant) {
        super(properties);
        this.variant = variant;
        registerDefaultState(stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(ASSEMBLED, false));
    }

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<net.minecraft.world.level.block.Block, BlockState> builder) {
        builder.add(FACING, ASSEMBLED);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new StargateBaseBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        if (blockEntityType != ModBlockEntities.STARGATE_BASE.get()) {
            return null;
        }

        return (tickLevel, pos, tickState, blockEntity) -> StargateBaseBlockEntity.tick(tickLevel, pos, tickState, (StargateBaseBlockEntity)blockEntity);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    public StargateVariant variant() {
        return variant;
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
        if (!level.isClientSide && !state.is(oldState.getBlock())) {
            if (Config.DEBUG_LOGGING.getAsBoolean()) {
                LanteaCraft.LOGGER.info("Stargate base placed at {}: facing={}, new={}, old={}, movedByPiston={}", pos, state.getValue(FACING), state, oldState, movedByPiston);
            }
            StargateMultiblock.tryAssembleAtBase(level, pos);
        }
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!state.getValue(ASSEMBLED)) {
            return InteractionResult.PASS;
        }

        if (player.isShiftKeyDown()) {
            if (!level.isClientSide && StargateMultiblock.disassembleAtBase(level, pos)) {
                player.displayClientMessage(Component.translatable("message.lanteacraft.stargate_disassembled").withStyle(ChatFormatting.YELLOW), true);
            }

            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        if (!level.isClientSide) {
            StargateMultiblock.findEntryFrom(level, pos).ifPresentOrElse(
                    entry -> {
                        if (player instanceof ServerPlayer serverPlayer && level.getBlockEntity(pos) instanceof StargateBaseBlockEntity base) {
                            serverPlayer.openMenu(new MenuProvider() {
                                @Override
                                public Component getDisplayName() {
                                    return Component.translatable("screen.lanteacraft.stargate");
                                }

                                @Override
                                public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player menuPlayer) {
                                    return new StargateMenu(containerId, inventory, base, variant, entry.address());
                                }
                            }, buffer -> {
                                buffer.writeBlockPos(pos);
                                buffer.writeUtf(variant.name(), 32);
                                buffer.writeUtf(entry.address(), 16);
                            });
                        } else {
                            StargateAddressMessage.send(player, entry);
                        }
                    },
                    () -> player.displayClientMessage(net.minecraft.network.chat.Component.literal("No assembled Stargate address found.").withStyle(net.minecraft.ChatFormatting.RED), false));
        }

        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (state.getValue(ASSEMBLED) && stack.getItem() instanceof IrisUpgradeItem irisItem && level.getBlockEntity(pos) instanceof StargateBaseBlockEntity base) {
            if (!level.isClientSide) {
                if (!base.hasIris()) {
                    base.installIris(stack);
                    if (!player.getAbilities().instabuild) {
                        stack.shrink(1);
                    }
                    player.displayClientMessage(Component.translatable("message.lanteacraft.iris_installed"), true);
                } else {
                    base.toggleIris();
                    player.displayClientMessage(Component.translatable(base.isIrisClosedOrClosing()
                            ? "message.lanteacraft.iris_closing"
                            : "message.lanteacraft.iris_opening"), true);
                }
            }

            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }

        ItemInteractionResult camouflageResult = StargateCamouflage.tryApplyBottomCamouflage(stack, state, level, pos, player);
        if (camouflageResult.consumesAction()) {
            return camouflageResult;
        }

        InteractionResult blockResult = useWithoutItem(state, level, pos, player, hitResult);
        return blockResult.consumesAction() ? ItemInteractionResult.sidedSuccess(level.isClientSide) : ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected VoxelShape getBlockSupportShape(BlockState state, BlockGetter level, BlockPos pos) {
        return Shapes.block();
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return NO_SNOW_COLLISION_SHAPE;
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
        return false;
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide && state.getValue(ASSEMBLED)) {
            StargateMultiblock.disassembleAtBase(level, pos);
        }

        return super.playerWillDestroy(level, pos, state, player);
    }
}
