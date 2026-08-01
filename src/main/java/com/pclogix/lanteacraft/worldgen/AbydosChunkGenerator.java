package com.pclogix.lanteacraft.worldgen;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;

/** Overworld-shaped desert terrain with no global ocean or aquifer fluid. */
public final class AbydosChunkGenerator extends NoiseBasedChunkGenerator {
    public static final MapCodec<AbydosChunkGenerator> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                            BiomeSource.CODEC.fieldOf("biome_source").forGetter(generator -> generator.source),
                            NoiseGeneratorSettings.CODEC.fieldOf("settings").forGetter(generator -> generator.baseSettings))
                    .apply(instance, instance.stable(AbydosChunkGenerator::new)));

    private final BiomeSource source;
    private final Holder<NoiseGeneratorSettings> baseSettings;

    public AbydosChunkGenerator(BiomeSource source, Holder<NoiseGeneratorSettings> baseSettings) {
        super(source, Holder.direct(withoutOceans(baseSettings.value())));
        this.source = source;
        this.baseSettings = baseSettings;
    }

    @Override
    protected MapCodec<? extends ChunkGenerator> codec() {
        return CODEC;
    }

    private static NoiseGeneratorSettings withoutOceans(NoiseGeneratorSettings base) {
        return new NoiseGeneratorSettings(
                base.noiseSettings(),
                base.defaultBlock(),
                Blocks.AIR.defaultBlockState(),
                base.noiseRouter(),
                base.surfaceRule(),
                base.spawnTarget(),
                -64,
                base.disableMobGeneration(),
                false,
                base.oreVeinsEnabled(),
                base.useLegacyRandomSource());
    }
}
