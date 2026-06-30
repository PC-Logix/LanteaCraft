package com.pclogix.lanteacraft.registry;

import com.pclogix.lanteacraft.LanteaCraft;
import com.pclogix.lanteacraft.gate.IrisType;
import com.pclogix.lanteacraft.item.AddressTabletItem;
import com.pclogix.lanteacraft.item.DecoratorItem;
import com.pclogix.lanteacraft.item.GdoItem;
import com.pclogix.lanteacraft.item.IrisUpgradeItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(LanteaCraft.MODID);

    public static final DeferredItem<BlockItem> STARGATE_RING = ITEMS.registerSimpleBlockItem("stargate_ring", ModBlocks.STARGATE_RING);
    public static final DeferredItem<BlockItem> STARGATE_CHEVRON = ITEMS.registerSimpleBlockItem("stargate_chevron", ModBlocks.STARGATE_CHEVRON);
    public static final DeferredItem<BlockItem> STARGATE_BASE = ITEMS.registerSimpleBlockItem("stargate_base", ModBlocks.STARGATE_BASE);
    public static final DeferredItem<BlockItem> DHD = ITEMS.registerSimpleBlockItem("dhd", ModBlocks.DHD);
    public static final DeferredItem<BlockItem> NOX_STARGATE_RING = ITEMS.registerSimpleBlockItem("nox_stargate_ring", ModBlocks.NOX_STARGATE_RING);
    public static final DeferredItem<BlockItem> NOX_STARGATE_CHEVRON = ITEMS.registerSimpleBlockItem("nox_stargate_chevron", ModBlocks.NOX_STARGATE_CHEVRON);
    public static final DeferredItem<BlockItem> NOX_STARGATE_BASE = ITEMS.registerSimpleBlockItem("nox_stargate_base", ModBlocks.NOX_STARGATE_BASE);
    public static final DeferredItem<BlockItem> NOX_DHD = ITEMS.registerSimpleBlockItem("nox_dhd", ModBlocks.NOX_DHD);
    public static final DeferredItem<BlockItem> WRAITH_STARGATE_RING = ITEMS.registerSimpleBlockItem("wraith_stargate_ring", ModBlocks.WRAITH_STARGATE_RING);
    public static final DeferredItem<BlockItem> WRAITH_STARGATE_CHEVRON = ITEMS.registerSimpleBlockItem("wraith_stargate_chevron", ModBlocks.WRAITH_STARGATE_CHEVRON);
    public static final DeferredItem<BlockItem> WRAITH_STARGATE_BASE = ITEMS.registerSimpleBlockItem("wraith_stargate_base", ModBlocks.WRAITH_STARGATE_BASE);
    public static final DeferredItem<BlockItem> WRAITH_DHD = ITEMS.registerSimpleBlockItem("wraith_dhd", ModBlocks.WRAITH_DHD);
    public static final DeferredItem<BlockItem> PEGASUS_STARGATE_RING = ITEMS.registerSimpleBlockItem("pegasus_stargate_ring", ModBlocks.PEGASUS_STARGATE_RING);
    public static final DeferredItem<BlockItem> PEGASUS_STARGATE_CHEVRON = ITEMS.registerSimpleBlockItem("pegasus_stargate_chevron", ModBlocks.PEGASUS_STARGATE_CHEVRON);
    public static final DeferredItem<BlockItem> PEGASUS_STARGATE_BASE = ITEMS.registerSimpleBlockItem("pegasus_stargate_base", ModBlocks.PEGASUS_STARGATE_BASE);
    public static final DeferredItem<BlockItem> PEGASUS_DHD = ITEMS.registerSimpleBlockItem("pegasus_dhd", ModBlocks.PEGASUS_DHD);
    public static final DeferredItem<BlockItem> TRANSPORT_RING = ITEMS.registerSimpleBlockItem("transport_ring", ModBlocks.TRANSPORT_RING);
    public static final DeferredItem<BlockItem> NAQUADAH_ORE = ITEMS.registerSimpleBlockItem("naquadah_ore", ModBlocks.NAQUADAH_ORE);
    public static final DeferredItem<BlockItem> TRINIUM_ORE = ITEMS.registerSimpleBlockItem("trinium_ore", ModBlocks.TRINIUM_ORE);
    public static final DeferredItem<BlockItem> NAQUADAH_BLOCK = ITEMS.registerSimpleBlockItem("naquadah_block", ModBlocks.NAQUADAH_BLOCK);
    public static final DeferredItem<BlockItem> TRINIUM_BLOCK = ITEMS.registerSimpleBlockItem("trinium_block", ModBlocks.TRINIUM_BLOCK);

    public static final DeferredItem<Item> CONTROL_CRYSTAL = ITEMS.registerSimpleItem(
            "control_crystal",
            new Item.Properties().rarity(Rarity.UNCOMMON));
    public static final DeferredItem<Item> BLANK_CRYSTAL = ITEMS.registerSimpleItem(
            "blank_crystal",
            new Item.Properties().rarity(Rarity.UNCOMMON));
    public static final DeferredItem<Item> CORE_CRYSTAL = ITEMS.registerSimpleItem(
            "core_crystal",
            new Item.Properties().rarity(Rarity.UNCOMMON));
    public static final DeferredItem<Item> NAQUADAH = ITEMS.registerSimpleItem("naquadah");
    public static final DeferredItem<Item> TRINIUM = ITEMS.registerSimpleItem("trinium");
    public static final DeferredItem<Item> NAQUADAH_INGOT = ITEMS.registerSimpleItem("naquadah_ingot");
    public static final DeferredItem<Item> TRINIUM_INGOT = ITEMS.registerSimpleItem("trinium_ingot");
    public static final DeferredItem<IrisUpgradeItem> MECHANICAL_IRIS = ITEMS.register(
            "mechanical_iris",
            () -> new IrisUpgradeItem(IrisType.MECHANICAL, new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON)));
    public static final DeferredItem<IrisUpgradeItem> ENERGY_IRIS = ITEMS.register(
            "energy_iris",
            () -> new IrisUpgradeItem(IrisType.ENERGY, new Item.Properties().stacksTo(1).rarity(Rarity.RARE)));
    public static final DeferredItem<GdoItem> GDO = ITEMS.register(
            "gdo",
            () -> new GdoItem(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON)));
    public static final DeferredItem<AddressTabletItem> ADDRESS_TABLET = ITEMS.register(
            "address_tablet",
            () -> new AddressTabletItem(new Item.Properties().stacksTo(1).rarity(Rarity.RARE)));
    public static final DeferredItem<DecoratorItem> DECORATOR = ITEMS.register(
            "decorator",
            () -> new DecoratorItem(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON)));

    private ModItems() {
    }

    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }
}
