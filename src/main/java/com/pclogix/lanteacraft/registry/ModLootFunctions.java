package com.pclogix.lanteacraft.registry;

import com.pclogix.lanteacraft.LanteaCraft;
import com.pclogix.lanteacraft.loot.SetZpmEnergyFunction;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModLootFunctions {
    public static final DeferredRegister<LootItemFunctionType<?>> LOOT_FUNCTIONS = DeferredRegister.create(Registries.LOOT_FUNCTION_TYPE, LanteaCraft.MODID);

    public static final DeferredHolder<LootItemFunctionType<?>, LootItemFunctionType<SetZpmEnergyFunction>> SET_ZPM_ENERGY = LOOT_FUNCTIONS.register(
            "set_zpm_energy",
            () -> new LootItemFunctionType<>(SetZpmEnergyFunction.CODEC));

    private ModLootFunctions() {
    }

    public static void register(IEventBus modEventBus) {
        LOOT_FUNCTIONS.register(modEventBus);
    }
}
