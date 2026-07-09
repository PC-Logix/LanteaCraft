package com.pclogix.lanteacraft;

import com.pclogix.lanteacraft.client.NaquadahGeneratorScreen;
import com.pclogix.lanteacraft.client.DhdPowerScreen;
import com.pclogix.lanteacraft.client.StargateScreen;
import com.pclogix.lanteacraft.client.ZpmHubScreen;
import com.pclogix.lanteacraft.client.model.GoauldSoldierModel;
import com.pclogix.lanteacraft.client.render.GoauldSoldierRenderer;
import com.pclogix.lanteacraft.client.render.ObeliskRenderer;
import com.pclogix.lanteacraft.client.render.StargateBaseRenderer;
import com.pclogix.lanteacraft.client.render.TokraTraderRenderer;
import com.pclogix.lanteacraft.client.render.TransportRingRenderer;
import com.pclogix.lanteacraft.client.render.ZpmHubRenderer;
import com.pclogix.lanteacraft.registry.ModBlockEntities;
import com.pclogix.lanteacraft.registry.ModEntities;
import com.pclogix.lanteacraft.registry.ModMenus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterDimensionSpecialEffectsEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

// This class will not load on dedicated servers. Accessing client side code from here is safe.
@Mod(value = LanteaCraft.MODID, dist = Dist.CLIENT)
// You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
@EventBusSubscriber(modid = LanteaCraft.MODID, value = Dist.CLIENT)
public class LanteaCraftClient {
    private static final ResourceLocation ATLANTIS_EFFECTS = ResourceLocation.fromNamespaceAndPath(LanteaCraft.MODID, "atlantis");

    public LanteaCraftClient(ModContainer container) {
        // Allows NeoForge to create a config screen for this mod's configs.
        // The config screen is accessed by going to the Mods screen > clicking on your mod > clicking on config.
        // Do not forget to add translations for your config options to the en_us.json file.
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        // Some client setup code
        LanteaCraft.LOGGER.info("HELLO FROM CLIENT SETUP");
        LanteaCraft.LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
    }

    @SubscribeEvent
    static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.STARGATE_BASE.get(), StargateBaseRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.TRANSPORT_RING.get(), TransportRingRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.ZPM_HUB.get(), ZpmHubRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.OBELISK.get(), ObeliskRenderer::new);
        event.registerEntityRenderer(ModEntities.TOKRA_TRADER.get(), TokraTraderRenderer::new);
        event.registerEntityRenderer(ModEntities.GOAULD_SOLDIER.get(), GoauldSoldierRenderer::new);
    }

    @SubscribeEvent
    static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(GoauldSoldierRenderer.MODEL_LAYER, GoauldSoldierModel::createBodyLayer);
    }

    @SubscribeEvent
    static void registerMenuScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenus.STARGATE.get(), StargateScreen::new);
        event.register(ModMenus.NAQUADAH_GENERATOR.get(), NaquadahGeneratorScreen::new);
        event.register(ModMenus.DHD_POWER.get(), DhdPowerScreen::new);
        event.register(ModMenus.ZPM_HUB.get(), ZpmHubScreen::new);
    }

    @SubscribeEvent
    static void registerDimensionEffects(RegisterDimensionSpecialEffectsEvent event) {
        event.register(ATLANTIS_EFFECTS, new AtlantisEffects());
    }

    private static final class AtlantisEffects extends DimensionSpecialEffects {
        private AtlantisEffects() {
            super(Float.NaN, true, SkyType.NORMAL, false, false);
        }

        @Override
        public Vec3 getBrightnessDependentFogColor(Vec3 fogColor, float brightness) {
            return fogColor.multiply(
                    brightness * 0.94F + 0.06F,
                    brightness * 0.94F + 0.06F,
                    brightness * 0.91F + 0.09F);
        }

        @Override
        public boolean isFoggyAt(int x, int y) {
            return false;
        }
    }
}
