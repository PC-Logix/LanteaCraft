package com.pclogix.lanteacraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import org.jetbrains.annotations.Nullable;

public class SphinxBlock extends HorizontalDirectionalBlock {
    public static final MapCodec<SphinxBlock> CODEC = simpleCodec(SphinxBlock::new);
    public static final EnumProperty<Part> PART = EnumProperty.create("part", Part.class);

    public SphinxBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(PART, Part.BASE_LEFT_FRONT));
    }

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction facing = context.getHorizontalDirection().getOpposite();
        BlockPos origin = context.getClickedPos();
        for (Part part : Part.values()) {
            if (!context.getLevel().getBlockState(worldPos(origin, facing, part)).canBeReplaced(context)) {
                return null;
            }
        }
        return defaultBlockState().setValue(FACING, facing).setValue(PART, Part.BASE_LEFT_FRONT);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (!level.isClientSide) {
            for (Part part : Part.values()) {
                if (part != Part.BASE_LEFT_FRONT) {
                    level.setBlock(worldPos(pos, state.getValue(FACING), part), state.setValue(PART, part), UPDATE_ALL);
                }
            }
        }
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide && state.getValue(PART) != Part.BASE_LEFT_FRONT) {
            BlockPos origin = origin(pos, state);
            if (level.getBlockState(origin).is(this)) {
                level.destroyBlock(origin, !player.isCreative(), player);
            }
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock())) {
            Direction facing = state.getValue(FACING);
            BlockPos origin = origin(pos, state);
            for (Part part : Part.values()) {
                BlockPos partPos = worldPos(origin, facing, part);
                if (!partPos.equals(pos) && level.getBlockState(partPos).is(this)) {
                    level.removeBlock(partPos, false);
                }
            }
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    private static BlockPos origin(BlockPos pos, BlockState state) {
        Part part = state.getValue(PART);
        Direction facing = state.getValue(FACING);
        return pos.below(part.y).relative(facing.getCounterClockWise(), part.x).relative(facing.getOpposite(), part.z);
    }

    private static BlockPos worldPos(BlockPos origin, Direction facing, Part part) {
        return origin.above(part.y).relative(facing.getClockWise(), part.x).relative(facing, part.z);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<net.minecraft.world.level.block.Block, BlockState> builder) {
        builder.add(FACING, PART);
    }

    public enum Part implements StringRepresentable {
        BASE_LEFT_FRONT("base_left_front", 0, 0, 0), BASE_RIGHT_FRONT("base_right_front", 1, 0, 0),
        BASE_LEFT_MIDDLE("base_left_middle", 0, 0, 1), BASE_RIGHT_MIDDLE("base_right_middle", 1, 0, 1),
        BASE_LEFT_REAR("base_left_rear", 0, 0, 2), BASE_RIGHT_REAR("base_right_rear", 1, 0, 2),
        BODY_LEFT_FRONT("body_left_front", 0, 1, 0), BODY_RIGHT_FRONT("body_right_front", 1, 1, 0),
        BODY_LEFT_MIDDLE("body_left_middle", 0, 1, 1), BODY_RIGHT_MIDDLE("body_right_middle", 1, 1, 1),
        BODY_LEFT_REAR("body_left_rear", 0, 1, 2), BODY_RIGHT_REAR("body_right_rear", 1, 1, 2),
        HEAD_LEFT("head_left", 0, 2, 2), HEAD_RIGHT("head_right", 1, 2, 2);

        private final String name;
        private final int x;
        private final int y;
        private final int z;

        Part(String name, int x, int y, int z) {
            this.name = name;
            this.x = x;
            this.y = y;
            this.z = z;
        }

        @Override
        public String getSerializedName() {
            return name;
        }
    }
}
