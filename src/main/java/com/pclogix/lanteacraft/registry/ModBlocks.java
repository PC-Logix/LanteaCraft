package com.pclogix.lanteacraft.registry;

import com.pclogix.lanteacraft.LanteaCraft;
import com.pclogix.lanteacraft.block.BrazierBlock;
import com.pclogix.lanteacraft.block.DhdBlock;
import com.pclogix.lanteacraft.block.NaquadahGeneratorBlock;
import com.pclogix.lanteacraft.block.ObeliskBlock;
import com.pclogix.lanteacraft.block.ObeliskCollisionBlock;
import com.pclogix.lanteacraft.block.StargateBaseBlock;
import com.pclogix.lanteacraft.block.StargateComponentBlock;
import com.pclogix.lanteacraft.block.TransportRingBlock;
import com.pclogix.lanteacraft.block.ZpmHubBlock;
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
            () -> new StargateBaseBlock(stargateBaseProperties(MapColor.COLOR_GRAY)));

    public static final DeferredBlock<Block> DHD = BLOCKS.register(
            "dhd",
            () -> new DhdBlock(dhdProperties(MapColor.TERRACOTTA_BLUE)));

    public static final DeferredBlock<Block> NOX_STARGATE_RING = BLOCKS.register(
            "nox_stargate_ring",
            () -> new StargateComponentBlock(machineProperties(MapColor.PLANT).strength(8.0F, 1200.0F), StargateVariant.NOX));

    public static final DeferredBlock<Block> NOX_STARGATE_CHEVRON = BLOCKS.register(
            "nox_stargate_chevron",
            () -> new StargateComponentBlock(machineProperties(MapColor.PLANT).strength(8.0F, 1200.0F), StargateVariant.NOX));

    public static final DeferredBlock<Block> NOX_STARGATE_BASE = BLOCKS.register(
            "nox_stargate_base",
            () -> new StargateBaseBlock(stargateBaseProperties(MapColor.PLANT), StargateVariant.NOX));

    public static final DeferredBlock<Block> NOX_DHD = BLOCKS.register(
            "nox_dhd",
            () -> new DhdBlock(dhdProperties(MapColor.PLANT), StargateVariant.NOX));

    public static final DeferredBlock<Block> WRAITH_STARGATE_RING = BLOCKS.register(
            "wraith_stargate_ring",
            () -> new StargateComponentBlock(machineProperties(MapColor.COLOR_GREEN).strength(8.0F, 1200.0F), StargateVariant.WRAITH));

    public static final DeferredBlock<Block> WRAITH_STARGATE_CHEVRON = BLOCKS.register(
            "wraith_stargate_chevron",
            () -> new StargateComponentBlock(machineProperties(MapColor.COLOR_GREEN).strength(8.0F, 1200.0F), StargateVariant.WRAITH));

    public static final DeferredBlock<Block> WRAITH_STARGATE_BASE = BLOCKS.register(
            "wraith_stargate_base",
            () -> new StargateBaseBlock(stargateBaseProperties(MapColor.COLOR_GREEN), StargateVariant.WRAITH));

    public static final DeferredBlock<Block> WRAITH_DHD = BLOCKS.register(
            "wraith_dhd",
            () -> new DhdBlock(dhdProperties(MapColor.COLOR_GREEN), StargateVariant.WRAITH));

    public static final DeferredBlock<Block> PEGASUS_STARGATE_RING = BLOCKS.register(
            "pegasus_stargate_ring",
            () -> new StargateComponentBlock(machineProperties(MapColor.COLOR_LIGHT_BLUE).strength(8.0F, 1200.0F), StargateVariant.PEGASUS));

    public static final DeferredBlock<Block> PEGASUS_STARGATE_CHEVRON = BLOCKS.register(
            "pegasus_stargate_chevron",
            () -> new StargateComponentBlock(machineProperties(MapColor.COLOR_LIGHT_BLUE).strength(8.0F, 1200.0F), StargateVariant.PEGASUS));

    public static final DeferredBlock<Block> PEGASUS_STARGATE_BASE = BLOCKS.register(
            "pegasus_stargate_base",
            () -> new StargateBaseBlock(stargateBaseProperties(MapColor.COLOR_LIGHT_BLUE), StargateVariant.PEGASUS));

    public static final DeferredBlock<Block> PEGASUS_DHD = BLOCKS.register(
            "pegasus_dhd",
            () -> new DhdBlock(dhdProperties(MapColor.COLOR_LIGHT_BLUE), StargateVariant.PEGASUS));

    public static final DeferredBlock<Block> TRANSPORT_RING = BLOCKS.register(
            "transport_ring",
            () -> new TransportRingBlock(machineProperties(MapColor.GOLD).strength(5.0F, 20.0F)));

    public static final DeferredBlock<Block> NAQUADAH_GENERATOR = BLOCKS.register(
            "naquadah_generator",
            () -> new NaquadahGeneratorBlock(machineProperties(MapColor.COLOR_GREEN).strength(5.0F, 12.0F)
                    .lightLevel(state -> state.hasProperty(NaquadahGeneratorBlock.ACTIVE) && state.getValue(NaquadahGeneratorBlock.ACTIVE) ? 8 : 0)));

    public static final DeferredBlock<Block> ZPM_HUB = BLOCKS.register(
            "zpm_hub",
            () -> new ZpmHubBlock(machineProperties(MapColor.COLOR_CYAN).strength(5.0F, 12.0F)));

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

    public static final DeferredBlock<Block> NAQUADAH_BRAZIER = BLOCKS.register(
            "naquadah_brazier",
            () -> new BrazierBlock(brazierProperties(MapColor.COLOR_GREEN)));

    public static final DeferredBlock<Block> TRINIUM_BRAZIER = BLOCKS.register(
            "trinium_brazier",
            () -> new BrazierBlock(brazierProperties(MapColor.COLOR_LIGHT_BLUE)));

    public static final DeferredBlock<Block> GOAULD_BRAZIER = BLOCKS.register(
            "goauld_brazier",
            () -> new BrazierBlock(brazierProperties(MapColor.SAND).sound(SoundType.STONE)));

    public static final DeferredBlock<Block> OBELISK = BLOCKS.register(
            "obelisk",
            () -> new ObeliskBlock(lanteanStoneProperties(MapColor.SAND).strength(3.0F, 6.0F).noOcclusion()));

    public static final DeferredBlock<Block> OBELISK_COLLISION = BLOCKS.register(
            "obelisk_collision",
            () -> new ObeliskCollisionBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.NONE)
                    .strength(3.0F, 6.0F)
                    .noOcclusion()
                    .noLootTable()
                    .isValidSpawn((state, level, pos, entityType) -> false)
                    .isRedstoneConductor((state, level, pos) -> false)
                    .isSuffocating((state, level, pos) -> false)
                    .isViewBlocking((state, level, pos) -> false)));

    public static final DeferredBlock<Block> LANTEAN_WALL = BLOCKS.registerSimpleBlock(
            "lantean_wall",
            lanteanStoneProperties(MapColor.COLOR_LIGHT_BLUE));

    public static final DeferredBlock<Block> LANTEAN_CARVED_WALL = BLOCKS.registerSimpleBlock(
            "lantean_carved_wall",
            lanteanStoneProperties(MapColor.COLOR_LIGHT_BLUE));

    public static final DeferredBlock<Block> LANTEAN_PANEL = BLOCKS.registerSimpleBlock(
            "lantean_panel",
            lanteanMetalProperties(MapColor.COLOR_CYAN));

    public static final DeferredBlock<Block> LANTEAN_LIGHT_PANEL = BLOCKS.registerSimpleBlock(
            "lantean_light_panel",
            lanteanMetalProperties(MapColor.COLOR_CYAN).lightLevel(state -> 10));

    public static final DeferredBlock<Block> LANTEAN_DARK_TRIM = BLOCKS.registerSimpleBlock(
            "lantean_dark_trim",
            lanteanMetalProperties(MapColor.COLOR_BLUE));

    public static final DeferredBlock<Block> LANTEAN_GLASS = BLOCKS.register(
            "lantean_glass",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_CYAN)
                    .strength(1.2F, 4.0F)
                    .sound(SoundType.GLASS)
                    .noOcclusion()
                    .isValidSpawn((state, level, pos, entityType) -> false)
                    .isRedstoneConductor((state, level, pos) -> false)
                    .isSuffocating((state, level, pos) -> false)
                    .isViewBlocking((state, level, pos) -> false)));

    public static final DeferredBlock<Block> ANCIENT_CONTAINMENT_BLOCK = BLOCKS.registerSimpleBlock(
            "ancient_containment_block",
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BLUE)
                    .strength(-1.0F, 3600000.0F)
                    .sound(SoundType.DEEPSLATE_TILES)
                    .noLootTable());

    public static final DeferredBlock<Block> GOAULD_CONTAINMENT_BLOCK = BLOCKS.registerSimpleBlock(
            "goauld_containment_block",
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.SAND)
                    .strength(-1.0F, 3600000.0F)
                    .sound(SoundType.STONE)
                    .noLootTable());

    public static final DeferredBlock<Block> EXPEDITION_REWARD_DOOR_MARKER = BLOCKS.registerSimpleBlock(
            "expedition_reward_door_marker",
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_RED)
                    .strength(1.0F, 3.0F)
                    .sound(SoundType.STONE)
                    .noLootTable());

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

    private static BlockBehaviour.Properties stargateBaseProperties(MapColor mapColor) {
        return machineProperties(mapColor)
                .strength(10.0F, 1200.0F)
                .lightLevel(state -> state.hasProperty(StargateBaseBlock.WORMHOLE_OPEN) && state.getValue(StargateBaseBlock.WORMHOLE_OPEN) ? 12 : 0);
    }

    private static BlockBehaviour.Properties dhdProperties(MapColor mapColor) {
        return machineProperties(mapColor)
                .strength(4.0F, 12.0F)
                .lightLevel(state -> state.hasProperty(DhdBlock.ACTIVE) && state.getValue(DhdBlock.ACTIVE) ? 5 : 0);
    }

    private static BlockBehaviour.Properties metalProperties(MapColor mapColor) {
        return BlockBehaviour.Properties.of()
                .mapColor(mapColor)
                .requiresCorrectToolForDrops()
                .sound(SoundType.METAL);
    }

    private static BlockBehaviour.Properties brazierProperties(MapColor mapColor) {
        return metalProperties(mapColor)
                .strength(3.0F, 6.0F)
                .lightLevel(state -> 15)
                .noOcclusion();
    }

    private static BlockBehaviour.Properties oreProperties(MapColor mapColor) {
        return BlockBehaviour.Properties.of()
                .mapColor(mapColor)
                .strength(3.0F, 3.0F)
                .requiresCorrectToolForDrops()
                .sound(SoundType.STONE);
    }

    private static BlockBehaviour.Properties lanteanStoneProperties(MapColor mapColor) {
        return BlockBehaviour.Properties.of()
                .mapColor(mapColor)
                .strength(4.0F, 9.0F)
                .requiresCorrectToolForDrops()
                .sound(SoundType.DEEPSLATE_TILES);
    }

    private static BlockBehaviour.Properties lanteanMetalProperties(MapColor mapColor) {
        return BlockBehaviour.Properties.of()
                .mapColor(mapColor)
                .strength(4.5F, 12.0F)
                .requiresCorrectToolForDrops()
                .sound(SoundType.COPPER);
    }
}
