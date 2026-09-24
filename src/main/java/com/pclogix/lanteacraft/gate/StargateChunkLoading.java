package com.pclogix.lanteacraft.gate;

import com.pclogix.lanteacraft.LanteaCraft;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.common.world.chunk.RegisterTicketControllersEvent;
import net.neoforged.neoforge.common.world.chunk.TicketHelper;
import net.neoforged.neoforge.common.world.chunk.TicketSet;
import net.neoforged.neoforge.common.world.chunk.TicketController;

public final class StargateChunkLoading {
    private static final ResourceLocation CONTROLLER_ID = ResourceLocation.fromNamespaceAndPath(LanteaCraft.MODID, "stargate");
    private static final AtomicBoolean SABLE_LOOKUP_WARNING_LOGGED = new AtomicBoolean();
    private static final TicketController CONTROLLER = new TicketController(CONTROLLER_ID, StargateChunkLoading::validateTickets);

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
        Optional<Boolean> sablePlot = isSablePlotChunk(level, chunk);
        if (sablePlot.orElse(true)) {
            // Sable cancels addRegionTicket for plot chunks. NeoForge records the
            // forced ticket before that cancellation, then its later removal
            // reaches vanilla ChunkMap without a matching add and can leave a
            // generation holder missing during distance-manager updates.
            return;
        }
        CONTROLLER.forceChunk(level, owner, chunk.x, chunk.z, add, true);
    }

    private static void validateTickets(ServerLevel level, TicketHelper helper) {
        if (!ModList.get().isLoaded("sable")) {
            return;
        }

        for (Map.Entry<BlockPos, TicketSet> ownerTickets : helper.getBlockTickets().entrySet()) {
            BlockPos owner = ownerTickets.getKey();
            TicketSet tickets = ownerTickets.getValue();
            removeSablePlotTickets(level, helper, owner, tickets.nonTicking(), false);
            removeSablePlotTickets(level, helper, owner, tickets.ticking(), true);
        }
    }

    private static void removeSablePlotTickets(ServerLevel level, TicketHelper helper, BlockPos owner, it.unimi.dsi.fastutil.longs.LongSet chunks, boolean ticking) {
        for (long chunkKey : chunks.toLongArray()) {
            Optional<Boolean> sablePlot = isSablePlotChunk(level, new ChunkPos(chunkKey));
            if (sablePlot.orElse(false)) {
                helper.removeTicket(owner, chunkKey, ticking);
            }
        }
    }

    private static Optional<Boolean> isSablePlotChunk(ServerLevel level, ChunkPos chunk) {
        if (!ModList.get().isLoaded("sable")) {
            return Optional.of(false);
        }

        try {
            Class<?> containerClass = Class.forName("dev.ryanhcode.sable.api.sublevel.SubLevelContainer");
            Class<?> serverContainerClass = Class.forName("dev.ryanhcode.sable.api.sublevel.ServerSubLevelContainer");
            MethodHandles.Lookup lookup = MethodHandles.publicLookup();
            MethodHandle getContainer = lookup.findStatic(
                    containerClass,
                    "getContainer",
                    MethodType.methodType(serverContainerClass, ServerLevel.class));
            Object container = getContainer.invoke(level);
            if (container == null) {
                return Optional.of(false);
            }
            MethodHandle inBounds = lookup.findVirtual(
                    containerClass,
                    "inBounds",
                    MethodType.methodType(boolean.class, ChunkPos.class));
            return Optional.of((boolean)inBounds.invoke(container, chunk));
        } catch (Throwable exception) {
            if (SABLE_LOOKUP_WARNING_LOGGED.compareAndSet(false, true)) {
                LanteaCraft.LOGGER.error("Unable to check Sable plot bounds; skipping Stargate chunk tickets for safety.", exception);
            }
            return Optional.empty();
        }
    }

    private static ServerLevel level(MinecraftServer server, ResourceLocation dimension) {
        ResourceKey<Level> key = ResourceKey.create(Registries.DIMENSION, dimension);
        return server.getLevel(key);
    }
}
