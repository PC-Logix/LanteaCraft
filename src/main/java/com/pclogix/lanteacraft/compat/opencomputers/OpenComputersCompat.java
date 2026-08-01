package com.pclogix.lanteacraft.compat.opencomputers;

import com.pclogix.lanteacraft.gate.StargateEventDispatcher;
import li.cil.oc.api.Driver;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

public final class OpenComputersCompat {
    private OpenComputersCompat() {
    }

    public static void register(IEventBus modEventBus) {
        modEventBus.addListener(OpenComputersCompat::commonSetup);
        StargateEventDispatcher.register(StargateOpenComputersDriver::queue);
    }

    private static void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> Driver.add(new StargateOpenComputersDriver()));
    }
}
