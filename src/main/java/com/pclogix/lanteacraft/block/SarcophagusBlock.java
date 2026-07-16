package com.pclogix.lanteacraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class SarcophagusBlock extends HorizontalDirectionalBlock {
    public static final MapCodec<SarcophagusBlock> CODEC = simpleCodec(SarcophagusBlock::new);
    public static final EnumProperty<Part> PART = EnumProperty.create("part", Part.class);
    public static final BooleanProperty VERTICAL = BooleanProperty.create("vertical");
    private static final VoxelShape SHAPE = box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);

    public SarcophagusBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(VERTICAL, false)
                .setValue(PART, Part.FOOT));
    }

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction clickedFace = context.getClickedFace();
        boolean vertical = clickedFace.getAxis().isHorizontal();
        Direction facing = vertical ? clickedFace : context.getHorizontalDirection();
        Direction lengthDirection = vertical ? Direction.UP : facing;
        BlockPos foot = context.getClickedPos();
        if (!context.getLevel().getBlockState(foot.relative(lengthDirection)).canBeReplaced(context)
                || !context.getLevel().getBlockState(foot.relative(lengthDirection, 2)).canBeReplaced(context)) {
            return null;
        }
        return defaultBlockState()
                .setValue(FACING, facing)
                .setValue(VERTICAL, vertical)
                .setValue(PART, Part.FOOT);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (!level.isClientSide) {
            Direction lengthDirection = lengthDirection(state);
            level.setBlock(pos.relative(lengthDirection), state.setValue(PART, Part.MIDDLE), UPDATE_ALL);
            level.setBlock(pos.relative(lengthDirection, 2), state.setValue(PART, Part.HEAD), UPDATE_ALL);
        }
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide && state.getValue(PART) != Part.FOOT) {
            BlockPos foot = footPos(pos, state);
            if (level.getBlockState(foot).is(this)) {
                level.destroyBlock(foot, !player.isCreative(), player);
            }
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock())) {
            BlockPos foot = footPos(pos, state);
            Direction lengthDirection = lengthDirection(state);
            for (int offset = 0; offset < 3; offset++) {
                BlockPos partPos = foot.relative(lengthDirection, offset);
                if (!partPos.equals(pos) && level.getBlockState(partPos).is(this)) {
                    level.removeBlock(partPos, false);
                }
            }
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    private static BlockPos footPos(BlockPos pos, BlockState state) {
        Direction lengthDirection = lengthDirection(state);
        return switch (state.getValue(PART)) {
            case FOOT -> pos;
            case MIDDLE -> pos.relative(lengthDirection.getOpposite());
            case HEAD -> pos.relative(lengthDirection.getOpposite(), 2);
        };
    }

    private static Direction lengthDirection(BlockState state) {
        return state.getValue(VERTICAL) ? Direction.UP : state.getValue(FACING);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, VERTICAL, PART);
    }

    public enum Part implements StringRepresentable {
        FOOT("foot"), MIDDLE("middle"), HEAD("head");

        private final String name;

        Part(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return name;
        }
    }
}
