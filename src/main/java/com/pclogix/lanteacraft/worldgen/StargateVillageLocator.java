package com.pclogix.lanteacraft.worldgen;

import com.mojang.datafixers.util.Pair;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.StructureTags;
import net.minecraft.world.level.levelgen.structure.Structure;

public final class StargateVillageLocator {
    private StargateVillageLocator() {
    }

    public static Optional<BlockPos> nearestVillage(ServerLevel level, BlockPos origin, int radius, boolean skipKnown) {
        Optional<HolderSet.Named<Structure>> villages = level.registryAccess()
                .registryOrThrow(Registries.STRUCTURE)
                .getTag(StructureTags.VILLAGE);
        if (villages.isEmpty()) {
            return Optional.empty();
        }

        Pair<BlockPos, Holder<Structure>> found = level.getChunkSource().getGenerator().findNearestMapStructure(
                level,
                villages.get(),
                origin,
                radius,
                skipKnown);
        return found == null ? Optional.empty() : Optional.of(found.getFirst());
    }
}
