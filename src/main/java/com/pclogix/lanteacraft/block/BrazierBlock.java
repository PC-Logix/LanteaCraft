package com.pclogix.lanteacraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BrazierBlock extends Block {
    public static final MapCodec<BrazierBlock> CODEC = simpleCodec(BrazierBlock::new);
    private static final VoxelShape SHAPE = Shapes.or(
            box(5.0D, 0.0D, 5.0D, 11.0D, 16.0D, 11.0D),
            box(3.0D, 16.0D, 3.0D, 13.0D, 22.0D, 13.0D));

    public BrazierBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends Block> codec() {
        return CODEC;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        double x = pos.getX() + 0.5D + (random.nextDouble() - 0.5D) * 0.35D;
        double y = pos.getY() + 1.28D;
        double z = pos.getZ() + 0.5D + (random.nextDouble() - 0.5D) * 0.35D;
        level.addParticle(ParticleTypes.FLAME, x, y, z, 0.0D, 0.025D, 0.0D);

        if (random.nextFloat() < 0.35F) {
            level.addParticle(ParticleTypes.SMOKE, x, y + 0.08D, z, 0.0D, 0.015D, 0.0D);
        }
        if (random.nextInt(24) == 0) {
            level.playLocalSound(pos.getX() + 0.5D, pos.getY() + 0.75D, pos.getZ() + 0.5D,
                    SoundEvents.FIRE_AMBIENT, SoundSource.BLOCKS, 0.35F + random.nextFloat() * 0.15F,
                    0.9F + random.nextFloat() * 0.25F, false);
        }
    }
}
