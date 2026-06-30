package com.pclogix.lanteacraft.client;

import com.pclogix.lanteacraft.gate.StargateVariant;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;

public final class DhdClientHooks {
    private DhdClientHooks() {
    }

    public static void open(BlockPos dhdPos, StargateVariant variant) {
        Minecraft.getInstance().setScreen(new DhdScreen(dhdPos, variant));
    }
}
