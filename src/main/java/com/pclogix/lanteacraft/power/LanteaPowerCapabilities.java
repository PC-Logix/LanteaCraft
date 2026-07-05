package com.pclogix.lanteacraft.power;

import com.pclogix.lanteacraft.block.entity.NaquadahGeneratorBlockEntity;
import com.pclogix.lanteacraft.block.entity.StargateBaseBlockEntity;
import com.pclogix.lanteacraft.item.EnergyCrystalItem;
import com.pclogix.lanteacraft.registry.ModDataComponents;
import com.pclogix.lanteacraft.registry.ModBlockEntities;
import com.pclogix.lanteacraft.registry.ModItems;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.energy.ComponentEnergyStorage;

public final class LanteaPowerCapabilities {
    private LanteaPowerCapabilities() {
    }

    public static void register(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.EnergyStorage.BLOCK,
                ModBlockEntities.STARGATE_BASE.get(),
                (blockEntity, direction) -> blockEntity.energyStorage());
        event.registerBlockEntity(
                Capabilities.EnergyStorage.BLOCK,
                ModBlockEntities.NAQUADAH_GENERATOR.get(),
                (blockEntity, direction) -> blockEntity.energyStorage());
        event.registerBlockEntity(
                Capabilities.EnergyStorage.BLOCK,
                ModBlockEntities.DHD.get(),
                (blockEntity, direction) -> blockEntity.energyInput());
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                ModBlockEntities.NAQUADAH_GENERATOR.get(),
                (blockEntity, direction) -> blockEntity.fuelItems());
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                ModBlockEntities.DHD.get(),
                (blockEntity, direction) -> blockEntity.crystalItems());
        event.registerItem(
                Capabilities.EnergyStorage.ITEM,
                (stack, context) -> new ComponentEnergyStorage(stack, ModDataComponents.ENERGY.get(), EnergyCrystalItem.CAPACITY, EnergyCrystalItem.MAX_TRANSFER),
                ModItems.ENERGY_CRYSTAL.get());
    }
}
