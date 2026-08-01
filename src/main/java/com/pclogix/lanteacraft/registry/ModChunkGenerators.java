package com.pclogix.lanteacraft.registry;

import com.mojang.serialization.MapCodec;
import com.pclogix.lanteacraft.LanteaCraft;
import com.pclogix.lanteacraft.worldgen.AbydosChunkGenerator;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModChunkGenerators {
    public static final DeferredRegister<MapCodec<? extends ChunkGenerator>> CHUNK_GENERATORS =
            DeferredRegister.create(Registries.CHUNK_GENERATOR, LanteaCraft.MODID);

    public static final DeferredHolder<MapCodec<? extends ChunkGenerator>, MapCodec<AbydosChunkGenerator>> ABYDOS =
            CHUNK_GENERATORS.register("abydos", () -> AbydosChunkGenerator.CODEC);

    private ModChunkGenerators() {
    }

    public static void register(IEventBus modEventBus) {
        CHUNK_GENERATORS.register(modEventBus);
    }
}
