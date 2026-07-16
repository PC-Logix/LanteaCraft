package com.pclogix.lanteacraft.registry;

import com.pclogix.lanteacraft.LanteaCraft;
import com.pclogix.lanteacraft.block.entity.DhdBlockEntity;
import com.pclogix.lanteacraft.block.entity.ZpmHubBlockEntity;
import com.pclogix.lanteacraft.item.EnergyCrystalItem;
import com.pclogix.lanteacraft.item.ZpmItem;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.block.entity.BannerPatternLayers;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, LanteaCraft.MODID);

    private static final ResourceKey<CreativeModeTab> STARGATE_BLOCKS_KEY = key("stargate_blocks");
    private static final ResourceKey<CreativeModeTab> DECORATION_BLOCKS_KEY = key("decoration_blocks");
    private static final ResourceKey<CreativeModeTab> EGYPTIAN_PAINTINGS_KEY = key("egyptian_paintings");
    private static final ResourceKey<CreativeModeTab> TECHNICAL_KEY = key("technical");
    private static final ResourceKey<BannerPattern> STARGATE_BANNER_PATTERN = bannerPatternKey("stargate");
    private static final ResourceKey<BannerPattern> LANTEAN_BANNER_PATTERN = bannerPatternKey("lantean");
    private static final ResourceKey<BannerPattern> GOAULD_BANNER_PATTERN = bannerPatternKey("goauld");

    private static final String[] EGYPTIAN_PAINTINGS = {
            "eye_of_ra", "ankh_lotus", "sacred_scarab", "djed_pillar",
            "winged_sun", "uraeus_cobra", "ra_solar_barque", "anubis_ankh",
            "bastet", "horus", "isis_wings", "thoth_scribe", "sekhmet", "hathor",
            "osiris", "maat", "sobek", "canopic_jars", "jackal_guardians",
            "temple_offerings", "nile_procession", "pharaoh_chariot", "pyramids_at_dusk",
            "lotus_pool", "temple_columns", "weighing_heart", "royal_feast", "ra_and_anubis"
    };

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> STARGATE_BLOCKS = CREATIVE_TABS.register(
            STARGATE_BLOCKS_KEY.location().getPath(),
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.lanteacraft.stargate_blocks"))
                    .withTabsBefore(CreativeModeTabs.REDSTONE_BLOCKS)
                    .icon(() -> ModItems.CREATIVE_TAB_ICON.get().getDefaultInstance())
                    .displayItems((parameters, output) -> addStargateBlocks(output))
                    .build());

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> DECORATION_BLOCKS = CREATIVE_TABS.register(
            DECORATION_BLOCKS_KEY.location().getPath(),
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.lanteacraft.decoration_blocks"))
                    .withTabsAfter(STARGATE_BLOCKS_KEY)
                    .icon(() -> ModItems.LANTEAN_PANEL.get().getDefaultInstance())
                    .displayItems(ModCreativeTabs::addDecorationBlocks)
                    .build());

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> EGYPTIAN_PAINTINGS_TAB = CREATIVE_TABS.register(
            EGYPTIAN_PAINTINGS_KEY.location().getPath(),
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.lanteacraft.egyptian_paintings"))
                    .withTabsAfter(DECORATION_BLOCKS_KEY)
                    .icon(() -> painting("winged_sun"))
                    .displayItems((parameters, output) -> addEgyptianPaintings(output))
                    .build());

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> TECHNICAL = CREATIVE_TABS.register(
            TECHNICAL_KEY.location().getPath(),
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.lanteacraft.technical"))
                    .withTabsAfter(EGYPTIAN_PAINTINGS_KEY)
                    .withTabsBefore(CreativeModeTabs.REDSTONE_BLOCKS)
                    .icon(() -> ModItems.ZPM_HUB.get().getDefaultInstance())
                    .displayItems((parameters, output) -> addTechnicalItems(output))
                    .build());

    private ModCreativeTabs() {
    }

    public static void register(IEventBus modEventBus) {
        CREATIVE_TABS.register(modEventBus);
    }

    private static ResourceKey<CreativeModeTab> key(String path) {
        return ResourceKey.create(Registries.CREATIVE_MODE_TAB, ResourceLocation.fromNamespaceAndPath(LanteaCraft.MODID, path));
    }

    private static ResourceKey<BannerPattern> bannerPatternKey(String path) {
        return ResourceKey.create(Registries.BANNER_PATTERN, ResourceLocation.fromNamespaceAndPath(LanteaCraft.MODID, path));
    }

    private static void addStargateBlocks(CreativeModeTab.Output output) {
        output.accept(ModItems.STARGATE_RING);
        output.accept(ModItems.STARGATE_CHEVRON);
        output.accept(ModItems.STARGATE_BASE);
        output.accept(ModItems.DHD);
        output.accept(ModItems.NOX_STARGATE_RING);
        output.accept(ModItems.NOX_STARGATE_CHEVRON);
        output.accept(ModItems.NOX_STARGATE_BASE);
        output.accept(ModItems.NOX_DHD);
        output.accept(ModItems.WRAITH_STARGATE_RING);
        output.accept(ModItems.WRAITH_STARGATE_CHEVRON);
        output.accept(ModItems.WRAITH_STARGATE_BASE);
        output.accept(ModItems.WRAITH_DHD);
        output.accept(ModItems.PEGASUS_STARGATE_RING);
        output.accept(ModItems.PEGASUS_STARGATE_CHEVRON);
        output.accept(ModItems.PEGASUS_STARGATE_BASE);
        output.accept(ModItems.PEGASUS_DHD);
    }

    private static void addDecorationBlocks(CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output output) {
        output.accept(ModItems.NAQUADAH_ORE);
        output.accept(ModItems.TRINIUM_ORE);
        output.accept(ModItems.NAQUADAH);
        output.accept(ModItems.TRINIUM);
        output.accept(ModItems.NAQUADAH_INGOT);
        output.accept(ModItems.TRINIUM_INGOT);
        output.accept(ModItems.NAQUADAH_BLOCK);
        output.accept(ModItems.TRINIUM_BLOCK);
        output.accept(ModItems.NAQUADAH_BRAZIER);
        output.accept(ModItems.TRINIUM_BRAZIER);
        output.accept(ModItems.GOAULD_BRAZIER);
        output.accept(ModItems.OBELISK);
        output.accept(ModItems.SARCOPHAGUS);
        output.accept(ModItems.SPHINX);
        output.accept(ModItems.SPHINX_HEAD);
        output.accept(ModItems.CANOPIC_JAR);
        output.accept(ModItems.SCARAB_IDOL);
        output.accept(ModItems.OFFERING_ALTAR);
        output.accept(ModItems.BROKEN_COLUMN);
        output.accept(ModItems.SITTING_CAT_STATUE);
        output.accept(ModItems.BRONZE_SITTING_CAT_STATUE);
        output.accept(ModItems.RECLINING_CAT_STATUE);
        output.accept(ModItems.LANTEAN_WALL);
        output.accept(ModItems.LANTEAN_CARVED_WALL);
        output.accept(ModItems.LANTEAN_PANEL);
        output.accept(ModItems.LANTEAN_LIGHT_PANEL);
        output.accept(ModItems.LANTEAN_DARK_TRIM);
        output.accept(ModItems.LANTEAN_GLASS);
        output.accept(ModItems.ANCIENT_CONTAINMENT_BLOCK);
        output.accept(ModItems.GOAULD_CONTAINMENT_BLOCK);
        output.accept(ModItems.EXPEDITION_REWARD_DOOR);
        output.accept(ModItems.EXPEDITION_REWARD_DOOR_MARKER);
        output.accept(ModItems.STARGATE_BANNER_PATTERN_ITEM);
        output.accept(ModItems.LANTEAN_BANNER_PATTERN_ITEM);
        output.accept(ModItems.GOAULD_BANNER_PATTERN_ITEM);
        output.accept(banner(parameters, Items.BLACK_BANNER.getDefaultInstance(), STARGATE_BANNER_PATTERN, DyeColor.LIGHT_BLUE, "item.lanteacraft.stargate_banner"));
        output.accept(banner(parameters, Items.CYAN_BANNER.getDefaultInstance(), LANTEAN_BANNER_PATTERN, DyeColor.WHITE, "item.lanteacraft.lantean_banner"));
        output.accept(banner(parameters, Items.BLACK_BANNER.getDefaultInstance(), GOAULD_BANNER_PATTERN, DyeColor.YELLOW, "item.lanteacraft.goauld_banner"));
    }

    private static ItemStack banner(CreativeModeTab.ItemDisplayParameters parameters, ItemStack base, ResourceKey<BannerPattern> patternKey, DyeColor color, String nameKey) {
        HolderGetter<BannerPattern> patterns = parameters.holders().lookupOrThrow(Registries.BANNER_PATTERN);
        Holder<BannerPattern> pattern = patterns.getOrThrow(patternKey);
        base.set(DataComponents.BANNER_PATTERNS, new BannerPatternLayers.Builder().add(pattern, color).build());
        base.set(DataComponents.CUSTOM_NAME, Component.translatable(nameKey));
        return base;
    }

    private static void addEgyptianPaintings(CreativeModeTab.Output output) {
        for (String variant : EGYPTIAN_PAINTINGS) {
            output.accept(painting(variant));
        }
    }

    private static ItemStack painting(String variant) {
        ItemStack stack = new ItemStack(Items.PAINTING);
        CompoundTag entityData = new CompoundTag();
        entityData.putString("id", "minecraft:painting");
        entityData.putString("variant", LanteaCraft.MODID + ":" + variant);
        stack.set(DataComponents.ENTITY_DATA, CustomData.of(entityData));
        stack.set(DataComponents.CUSTOM_NAME, Component.translatable("painting.lanteacraft." + variant + ".title"));
        return stack;
    }

    private static void addTechnicalItems(CreativeModeTab.Output output) {
        output.accept(ModItems.TRANSPORT_RING);
        output.accept(ModItems.NAQUADAH_GENERATOR);
        output.accept(ModItems.ZPM_HUB);
        output.accept(ModItems.BLANK_CRYSTAL);
        output.accept(ModItems.CORE_CRYSTAL);
        output.accept(ModItems.CONTROL_CRYSTAL);
        output.accept(ModItems.EIGHTH_CHEVRON_CRYSTAL);
        output.accept(ModItems.ENERGY_CRYSTAL);
        output.accept(DhdBlockEntity.chargedCrystal(EnergyCrystalItem.CAPACITY));
        output.accept(ZpmHubBlockEntity.chargedZpm(ZpmItem.capacity()));
        output.accept(ModItems.ZPM);
        output.accept(ModItems.STAFF_WEAPON);
        output.accept(ModItems.P90);
        output.accept(ModItems.P90_EMPTY_MAGAZINE);
        output.accept(ModItems.P90_MAGAZINE);
        output.accept(ModItems.P90_ROUND);
        output.accept(ModItems.MECHANICAL_IRIS);
        output.accept(ModItems.ENERGY_IRIS);
        output.accept(ModItems.GDO);
        output.accept(ModItems.DECORATOR);
        output.accept(ModItems.ADDRESS_TABLET);
        output.accept(ModItems.EXPEDITION_ADDRESS_TABLET);
        output.accept(ModItems.ABYDOS_ADDRESS_TABLET);
        output.accept(ModItems.ATLANTIS_ADDRESS_TABLET);
        output.accept(ModItems.TOKRA_TRADER_SPAWN_EGG);
        output.accept(ModItems.GOAULD_SOLDIER_SPAWN_EGG);
    }
}
