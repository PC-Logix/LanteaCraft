package com.pclogix.lanteacraft.block;

import com.pclogix.lanteacraft.LanteaCraft;
import com.pclogix.lanteacraft.Config;
import com.pclogix.lanteacraft.gate.StargateAddressMessage;
import com.pclogix.lanteacraft.gate.StargateCamouflage;
import com.pclogix.lanteacraft.gate.StargateMultiblock;
import com.pclogix.lanteacraft.gate.StargateVariant;
import net.minecraft.core.BlockPos;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class StargateComponentBlock extends Block {
    public static final BooleanProperty ASSEMBLED = BooleanProperty.create("assembled");
    private static final VoxelShape NO_SNOW_COLLISION_SHAPE = box(0.0D, 0.0D, 0.0D, 16.0D, 15.0D, 16.0D);
    private final StargateVariant variant;

    public StargateComponentBlock(Properties properties) {
        this(properties, StargateVariant.MILKY_WAY);
    }

    public StargateComponentBlock(Properties properties, StargateVariant variant) {
        super(properties);
        this.variant = variant;
        registerDefaultState(stateDefinition.any().setValue(ASSEMBLED, false));
    }

    public StargateVariant variant() {
        return variant;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ASSEMBLED);
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
        if (!level.isClientSide && !state.is(oldState.getBlock())) {
            if (Config.DEBUG_LOGGING.getAsBoolean()) {
                LanteaCraft.LOGGER.info("Stargate component placed at {}: new={}, old={}, movedByPiston={}", pos, state, oldState, movedByPiston);
            }
            StargateMultiblock.tryAssembleFrom(level, pos);
        }
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!state.getValue(ASSEMBLED)) {
            return InteractionResult.PASS;
        }

        if (player.isShiftKeyDown()) {
            if (!level.isClientSide && StargateMultiblock.disassembleFrom(level, pos)) {
                player.displayClientMessage(Component.translatable("message.lanteacraft.stargate_disassembled").withStyle(ChatFormatting.YELLOW), true);
            }

            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        if (!level.isClientSide) {
            StargateMultiblock.findEntryFrom(level, pos).ifPresentOrElse(
                    entry -> StargateAddressMessage.send(player, entry),
                    () -> player.displayClientMessage(net.minecraft.network.chat.Component.literal("No assembled Stargate address found.").withStyle(net.minecraft.ChatFormatting.RED), false));
        }

        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
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
            StargateMultiblock.disassembleFrom(level, pos);
        }

        return super.playerWillDestroy(level, pos, state, player);
    }
}
