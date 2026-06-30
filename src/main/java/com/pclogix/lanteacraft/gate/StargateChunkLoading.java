package com.pclogix.lanteacraft.gate;

import com.pclogix.lanteacraft.LanteaCraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.world.chunk.RegisterTicketControllersEvent;
import net.neoforged.neoforge.common.world.chunk.TicketController;

public final class StargateChunkLoading {
    private static final ResourceLocation CONTROLLER_ID = ResourceLocation.fromNamespaceAndPath(LanteaCraft.MODID, "stargate");
    private static final TicketController CONTROLLER = new TicketController(CONTROLLER_ID);

    private StargateChunkLoading() {
    }

    public static void registerTicketController(RegisterTicketControllersEvent event) {
        event.register(CONTROLLER);
    }

    public static void forceConnection(ServerLevel sourceLevel, StargateEntry source, StargateEntry destination, boolean add) {
        forceGateChunk(sourceLevel, source.basePos(), source.basePos(), add);

        ServerLevel destinationLevel = level(sourceLevel.getServer(), destination.dimension());
        if (destinationLevel != null) {
            forceGateChunk(destinationLevel, source.basePos(), destination.basePos(), add);
        }
    }

    private static void forceGateChunk(ServerLevel level, BlockPos owner, BlockPos gatePos, boolean add) {
        ChunkPos chunk = new ChunkPos(gatePos);
        CONTROLLER.forceChunk(level, owner, chunk.x, chunk.z, add, true);
    }

    private static ServerLevel level(MinecraftServer server, ResourceLocation dimension) {
        ResourceKey<Level> key = ResourceKey.create(Registries.DIMENSION, dimension);
        return server.getLevel(key);
    }
}
