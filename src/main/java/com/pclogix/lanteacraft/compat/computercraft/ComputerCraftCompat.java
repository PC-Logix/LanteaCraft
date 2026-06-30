package com.pclogix.lanteacraft.compat.computercraft;

import com.pclogix.lanteacraft.gate.StargateEntry;
import com.pclogix.lanteacraft.gate.StargateEventDispatcher;
import com.pclogix.lanteacraft.gate.StargateMultiblock;
import com.pclogix.lanteacraft.registry.ModBlocks;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.PeripheralCapability;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

public final class ComputerCraftCompat {
    private static final Map<GateKey, StargatePeripheral> PERIPHERALS = new ConcurrentHashMap<>();

    private ComputerCraftCompat() {
    }

    public static void register(IEventBus modEventBus) {
        modEventBus.addListener(ComputerCraftCompat::registerCapabilities);
        StargateEventDispatcher.register(ComputerCraftCompat::handleGateEvent);
    }

    private static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlock(
                PeripheralCapability.get(),
                (level, pos, state, blockEntity, side) -> {
                    if (!(level instanceof ServerLevel serverLevel)) {
                        return null;
                    }

                    return StargateMultiblock.findEntryFrom(serverLevel, pos)
                            .map(entry -> peripheral(serverLevel, entry))
                            .orElse(null);
                },
                stargateBlocks());
    }

    private static Block[] stargateBlocks() {
        return new Block[] {
                ModBlocks.STARGATE_BASE.get(),
                ModBlocks.STARGATE_RING.get(),
                ModBlocks.STARGATE_CHEVRON.get(),
                ModBlocks.NOX_STARGATE_BASE.get(),
                ModBlocks.NOX_STARGATE_RING.get(),
                ModBlocks.NOX_STARGATE_CHEVRON.get(),
                ModBlocks.WRAITH_STARGATE_BASE.get(),
                ModBlocks.WRAITH_STARGATE_RING.get(),
                ModBlocks.WRAITH_STARGATE_CHEVRON.get(),
                ModBlocks.PEGASUS_STARGATE_BASE.get(),
                ModBlocks.PEGASUS_STARGATE_RING.get(),
                ModBlocks.PEGASUS_STARGATE_CHEVRON.get()
        };
    }

    private static StargatePeripheral peripheral(ServerLevel level, StargateEntry entry) {
        return PERIPHERALS.computeIfAbsent(GateKey.of(entry), ignored -> new StargatePeripheral(level, entry.basePos()));
    }

    private static void handleGateEvent(StargateEventDispatcher.GateEvent event) {
        StargatePeripheral peripheral = PERIPHERALS.get(GateKey.of(event.local()));
        if (peripheral != null) {
            peripheral.queue(event);
        }
    }

    static GateKey key(ServerLevel level, BlockPos basePos) {
        return new GateKey(level.dimension().location().toString(), basePos.immutable());
    }

    private record GateKey(String dimension, BlockPos basePos) {
        static GateKey of(StargateEntry entry) {
            return new GateKey(entry.dimension().toString(), entry.basePos());
        }
    }
}
