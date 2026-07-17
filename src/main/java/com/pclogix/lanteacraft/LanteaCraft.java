package com.pclogix.lanteacraft;

import com.mojang.logging.LogUtils;
import com.pclogix.lanteacraft.compat.computercraft.ComputerCraftCompat;
import com.pclogix.lanteacraft.compat.bluemap.BlueMapCompat;
import com.pclogix.lanteacraft.command.LanteaCommands;
import com.pclogix.lanteacraft.registry.ModBlocks;
import com.pclogix.lanteacraft.registry.ModCreativeTabs;
import com.pclogix.lanteacraft.registry.ModDataComponents;
import com.pclogix.lanteacraft.registry.ModEntities;
import com.pclogix.lanteacraft.registry.ModItems;
import com.pclogix.lanteacraft.registry.ModLootFunctions;
import com.pclogix.lanteacraft.registry.ModMenus;
import com.pclogix.lanteacraft.network.ModNetworking;
import com.pclogix.lanteacraft.gate.StargateChunkLoading;
import com.pclogix.lanteacraft.gate.StargateTeleportHandler;
import com.pclogix.lanteacraft.gate.GeneratedGateProtection;
import com.pclogix.lanteacraft.enchantment.ExplodingEnchantmentHandler;
import com.pclogix.lanteacraft.item.P90FireModeHandler;
import com.pclogix.lanteacraft.power.LanteaPowerCapabilities;
import com.pclogix.lanteacraft.registry.ModBlockEntities;
import com.pclogix.lanteacraft.registry.ModSounds;
import com.pclogix.lanteacraft.worldgen.AbydosSpawner;
import com.pclogix.lanteacraft.worldgen.AtlantisCityManager;
import com.pclogix.lanteacraft.worldgen.AtlantisSpawnRules;
import com.pclogix.lanteacraft.worldgen.ExpeditionTrialTracker;
import com.pclogix.lanteacraft.worldgen.FixedDimensionGateBootstrap;
import com.pclogix.lanteacraft.worldgen.LanteaRetrogen;
import com.pclogix.lanteacraft.worldgen.LanteaWorldgenEvents;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;

@Mod(LanteaCraft.MODID)
public class LanteaCraft {
    public static final String MODID = "lanteacraft";
    public static final Logger LOGGER = LogUtils.getLogger();

    public LanteaCraft(IEventBus modEventBus, ModContainer modContainer) {
        ModBlocks.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModDataComponents.register(modEventBus);
        ModItems.register(modEventBus);
        ModLootFunctions.register(modEventBus);
        ModMenus.register(modEventBus);
        ModCreativeTabs.register(modEventBus);
        ModSounds.register(modEventBus);
        ModEntities.register(modEventBus);
        ModNetworking.register(modEventBus);
        modEventBus.addListener(LanteaPowerCapabilities::register);
        if (ModList.get().isLoaded("computercraft")) {
            ComputerCraftCompat.register(modEventBus);
        }
        if (ModList.get().isLoaded("bluemap")) {
            BlueMapCompat.register();
        }
        StargateTeleportHandler.register(NeoForge.EVENT_BUS);
        GeneratedGateProtection.register(NeoForge.EVENT_BUS);
        ExplodingEnchantmentHandler.register(NeoForge.EVENT_BUS);
        P90FireModeHandler.register(NeoForge.EVENT_BUS);
        NeoForge.EVENT_BUS.addListener(LanteaWorldgenEvents::onLootTableLoad);
        NeoForge.EVENT_BUS.addListener(LanteaRetrogen::onChunkLoad);
        NeoForge.EVENT_BUS.addListener(LanteaRetrogen::onLevelTick);
        NeoForge.EVENT_BUS.addListener(FixedDimensionGateBootstrap::onLevelTick);
        NeoForge.EVENT_BUS.addListener(ExpeditionTrialTracker::onLevelTick);
        NeoForge.EVENT_BUS.addListener(AbydosSpawner::onLevelTick);
        NeoForge.EVENT_BUS.addListener(AbydosSpawner::onChunkLoad);
        NeoForge.EVENT_BUS.addListener(AtlantisCityManager::onLevelTick);
        NeoForge.EVENT_BUS.addListener(AtlantisSpawnRules::onSpawnPlacementCheck);
        NeoForge.EVENT_BUS.addListener(AtlantisSpawnRules::onPositionCheck);
        NeoForge.EVENT_BUS.addListener(LanteaCommands::register);

        modEventBus.addListener(StargateChunkLoading::registerTicketController);
        modEventBus.addListener(this::commonSetup);
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        if (Config.DEBUG_LOGGING.getAsBoolean()) {
            LOGGER.info("LanteaCraft bootstrap complete.");
        }
    }
}
