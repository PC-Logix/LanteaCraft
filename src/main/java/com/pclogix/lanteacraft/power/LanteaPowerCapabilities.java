package com.pclogix.lanteacraft.power;

import com.pclogix.lanteacraft.block.entity.NaquadahGeneratorBlockEntity;
import com.pclogix.lanteacraft.block.entity.StargateBaseBlockEntity;
import com.pclogix.lanteacraft.gate.StargateMultiblock;
import com.pclogix.lanteacraft.item.EnergyCrystalItem;
import com.pclogix.lanteacraft.registry.ModDataComponents;
import com.pclogix.lanteacraft.registry.ModBlockEntities;
import com.pclogix.lanteacraft.registry.ModBlocks;
import com.pclogix.lanteacraft.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.energy.ComponentEnergyStorage;
import net.neoforged.neoforge.energy.IEnergyStorage;

public final class LanteaPowerCapabilities {
    private LanteaPowerCapabilities() {
    }

    public static void register(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.EnergyStorage.BLOCK,
                ModBlockEntities.STARGATE_BASE.get(),
                (blockEntity, direction) -> blockEntity.energyStorage());
        event.registerBlock(
                Capabilities.EnergyStorage.BLOCK,
                LanteaPowerCapabilities::stargateFrameEnergyStorage,
                stargateComponentBlocks());
        event.registerBlockEntity(
                Capabilities.EnergyStorage.BLOCK,
                ModBlockEntities.NAQUADAH_GENERATOR.get(),
                (blockEntity, direction) -> blockEntity.energyStorage());
        event.registerBlockEntity(
                Capabilities.EnergyStorage.BLOCK,
                ModBlockEntities.DHD.get(),
                (blockEntity, direction) -> blockEntity.energyInput());
        event.registerBlockEntity(
                Capabilities.EnergyStorage.BLOCK,
                ModBlockEntities.ZPM_HUB.get(),
                (blockEntity, direction) -> blockEntity.energyOutput());
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                ModBlockEntities.NAQUADAH_GENERATOR.get(),
                (blockEntity, direction) -> blockEntity.fuelItems());
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                ModBlockEntities.DHD.get(),
                (blockEntity, direction) -> blockEntity.crystalItems());
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                ModBlockEntities.ZPM_HUB.get(),
                (blockEntity, direction) -> blockEntity.zpmItems());
        event.registerItem(
                Capabilities.EnergyStorage.ITEM,
                (stack, context) -> new ComponentEnergyStorage(stack, ModDataComponents.ENERGY.get(), EnergyCrystalItem.CAPACITY, EnergyCrystalItem.MAX_TRANSFER),
                ModItems.ENERGY_CRYSTAL.get());
        event.registerItem(
                Capabilities.EnergyStorage.ITEM,
                (stack, context) -> new ZpmEnergyStorage(stack),
                ModItems.ZPM.get());
    }

    private static IEnergyStorage stargateFrameEnergyStorage(net.minecraft.world.level.Level level, BlockPos pos, net.minecraft.world.level.block.state.BlockState state, net.minecraft.world.level.block.entity.BlockEntity blockEntity, net.minecraft.core.Direction direction) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return null;
        }

        return StargateMultiblock.findBasePosFrom(serverLevel, pos)
                .filter(basePos -> !basePos.equals(pos))
                .map(serverLevel::getBlockEntity)
                .filter(StargateBaseBlockEntity.class::isInstance)
                .map(StargateBaseBlockEntity.class::cast)
                .map(StargateBaseBlockEntity::energyStorage)
                .orElse(null);
    }

    private static Block[] stargateComponentBlocks() {
        return new Block[] {
                ModBlocks.STARGATE_RING.get(),
                ModBlocks.STARGATE_CHEVRON.get(),
                ModBlocks.NOX_STARGATE_RING.get(),
                ModBlocks.NOX_STARGATE_CHEVRON.get(),
                ModBlocks.WRAITH_STARGATE_RING.get(),
                ModBlocks.WRAITH_STARGATE_CHEVRON.get(),
                ModBlocks.PEGASUS_STARGATE_RING.get(),
                ModBlocks.PEGASUS_STARGATE_CHEVRON.get()
        };
    }
}
