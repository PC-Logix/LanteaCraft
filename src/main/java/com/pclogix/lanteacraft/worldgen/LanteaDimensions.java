package com.pclogix.lanteacraft.worldgen;

import com.pclogix.lanteacraft.LanteaCraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

public final class LanteaDimensions {
    public static final ResourceKey<Level> ABYDOS = dimension("abydos");
    public static final ResourceKey<Level> ATLANTIS = dimension("atlantis");
    public static final ResourceKey<Level> EXPEDITIONS = dimension("expeditions");

    private LanteaDimensions() {
    }

    private static ResourceKey<Level> dimension(String name) {
        return ResourceKey.create(Registries.DIMENSION, ResourceLocation.fromNamespaceAndPath(LanteaCraft.MODID, name));
    }
}
