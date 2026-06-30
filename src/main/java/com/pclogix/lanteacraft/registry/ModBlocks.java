package com.pclogix.lanteacraft.registry;

import com.pclogix.lanteacraft.LanteaCraft;
import com.pclogix.lanteacraft.block.DhdBlock;
import com.pclogix.lanteacraft.block.StargateBaseBlock;
import com.pclogix.lanteacraft.block.StargateComponentBlock;
import com.pclogix.lanteacraft.block.TransportRingBlock;
import com.pclogix.lanteacraft.gate.StargateVariant;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(LanteaCraft.MODID);

    public static final DeferredBlock<Block> STARGATE_RING = BLOCKS.register(
            "stargate_ring",
            () -> new StargateComponentBlock(machineProperties(MapColor.METAL).strength(8.0F, 1200.0F)));

    public static final DeferredBlock<Block> STARGATE_CHEVRON = BLOCKS.register(
            "stargate_chevron",
            () -> new StargateComponentBlock(machineProperties(MapColor.COLOR_RED).strength(8.0F, 1200.0F)));

    public static final DeferredBlock<Block> STARGATE_BASE = BLOCKS.register(
            "stargate_base",
            () -> new StargateBaseBlock(machineProperties(MapColor.COLOR_GRAY).strength(10.0F, 1200.0F)));

    public static final DeferredBlock<Block> DHD = BLOCKS.register(
            "dhd",
            () -> new DhdBlock(machineProperties(MapColor.TERRACOTTA_BLUE).strength(4.0F, 12.0F)));

    public static final DeferredBlock<Block> NOX_STARGATE_RING = BLOCKS.register(
            "nox_stargate_ring",
            () -> new StargateComponentBlock(machineProperties(MapColor.PLANT).strength(8.0F, 1200.0F), StargateVariant.NOX));

    public static final DeferredBlock<Block> NOX_STARGATE_CHEVRON = BLOCKS.register(
            "nox_stargate_chevron",
            () -> new StargateComponentBlock(machineProperties(MapColor.PLANT).strength(8.0F, 1200.0F), StargateVariant.NOX));

    public static final DeferredBlock<Block> NOX_STARGATE_BASE = BLOCKS.register(
            "nox_stargate_base",
            () -> new StargateBaseBlock(machineProperties(MapColor.PLANT).strength(10.0F, 1200.0F), StargateVariant.NOX));

    public static final DeferredBlock<Block> NOX_DHD = BLOCKS.register(
            "nox_dhd",
            () -> new DhdBlock(machineProperties(MapColor.PLANT).strength(4.0F, 12.0F), StargateVariant.NOX));

    public static final DeferredBlock<Block> WRAITH_STARGATE_RING = BLOCKS.register(
            "wraith_stargate_ring",
            () -> new StargateComponentBlock(machineProperties(MapColor.COLOR_GREEN).strength(8.0F, 1200.0F), StargateVariant.WRAITH));

    public static final DeferredBlock<Block> WRAITH_STARGATE_CHEVRON = BLOCKS.register(
            "wraith_stargate_chevron",
            () -> new StargateComponentBlock(machineProperties(MapColor.COLOR_GREEN).strength(8.0F, 1200.0F), StargateVariant.WRAITH));

    public static final DeferredBlock<Block> WRAITH_STARGATE_BASE = BLOCKS.register(
            "wraith_stargate_base",
            () -> new StargateBaseBlock(machineProperties(MapColor.COLOR_GREEN).strength(10.0F, 1200.0F), StargateVariant.WRAITH));

    public static final DeferredBlock<Block> WRAITH_DHD = BLOCKS.register(
            "wraith_dhd",
            () -> new DhdBlock(machineProperties(MapColor.COLOR_GREEN).strength(4.0F, 12.0F), StargateVariant.WRAITH));

    public static final DeferredBlock<Block> PEGASUS_STARGATE_RING = BLOCKS.register(
            "pegasus_stargate_ring",
            () -> new StargateComponentBlock(machineProperties(MapColor.COLOR_LIGHT_BLUE).strength(8.0F, 1200.0F), StargateVariant.PEGASUS));

    public static final DeferredBlock<Block> PEGASUS_STARGATE_CHEVRON = BLOCKS.register(
            "pegasus_stargate_chevron",
            () -> new StargateComponentBlock(machineProperties(MapColor.COLOR_LIGHT_BLUE).strength(8.0F, 1200.0F), StargateVariant.PEGASUS));

    public static final DeferredBlock<Block> PEGASUS_STARGATE_BASE = BLOCKS.register(
            "pegasus_stargate_base",
            () -> new StargateBaseBlock(machineProperties(MapColor.COLOR_LIGHT_BLUE).strength(10.0F, 1200.0F), StargateVariant.PEGASUS));

    public static final DeferredBlock<Block> PEGASUS_DHD = BLOCKS.register(
            "pegasus_dhd",
            () -> new DhdBlock(machineProperties(MapColor.COLOR_LIGHT_BLUE).strength(4.0F, 12.0F), StargateVariant.PEGASUS));

    public static final DeferredBlock<Block> TRANSPORT_RING = BLOCKS.register(
            "transport_ring",
            () -> new TransportRingBlock(machineProperties(MapColor.GOLD).strength(5.0F, 20.0F)));

    public static final DeferredBlock<Block> NAQUADAH_ORE = BLOCKS.registerSimpleBlock(
            "naquadah_ore",
            oreProperties(MapColor.STONE));

    public static final DeferredBlock<Block> TRINIUM_ORE = BLOCKS.registerSimpleBlock(
            "trinium_ore",
            oreProperties(MapColor.STONE));

    public static final DeferredBlock<Block> NAQUADAH_BLOCK = BLOCKS.registerSimpleBlock(
            "naquadah_block",
            metalProperties(MapColor.COLOR_GREEN).strength(5.0F, 6.0F));

    public static final DeferredBlock<Block> TRINIUM_BLOCK = BLOCKS.registerSimpleBlock(
            "trinium_block",
            metalProperties(MapColor.COLOR_LIGHT_BLUE).strength(5.0F, 6.0F));

    private ModBlocks() {
    }

    public static void register(IEventBus modEventBus) {
        BLOCKS.register(modEventBus);
    }

    private static BlockBehaviour.Properties machineProperties(MapColor mapColor) {
        return BlockBehaviour.Properties.of()
                .mapColor(mapColor)
                .noOcclusion()
                .requiresCorrectToolForDrops()
                .sound(SoundType.METAL);
    }

    private static BlockBehaviour.Properties metalProperties(MapColor mapColor) {
        return BlockBehaviour.Properties.of()
                .mapColor(mapColor)
                .requiresCorrectToolForDrops()
                .sound(SoundType.METAL);
    }

    private static BlockBehaviour.Properties oreProperties(MapColor mapColor) {
        return BlockBehaviour.Properties.of()
                .mapColor(mapColor)
                .strength(3.0F, 3.0F)
                .requiresCorrectToolForDrops()
                .sound(SoundType.STONE);
    }
}
