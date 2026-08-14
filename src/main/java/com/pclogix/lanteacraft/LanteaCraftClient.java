package com.pclogix.lanteacraft;

import com.pclogix.lanteacraft.client.NaquadahGeneratorScreen;
import com.pclogix.lanteacraft.client.DhdPowerScreen;
import com.pclogix.lanteacraft.client.StargateScreen;
import com.pclogix.lanteacraft.client.ZpmHubScreen;
import com.pclogix.lanteacraft.client.InterServerTransferClientState;
import com.pclogix.lanteacraft.client.model.GoauldSoldierModel;
import com.pclogix.lanteacraft.client.render.GoauldSoldierRenderer;
import com.pclogix.lanteacraft.client.render.ObeliskRenderer;
import com.pclogix.lanteacraft.client.render.OfferingAltarRenderer;
import com.pclogix.lanteacraft.client.render.StargateBaseRenderer;
import com.pclogix.lanteacraft.client.render.TokraTraderRenderer;
import com.pclogix.lanteacraft.client.render.StaffBlastRenderer;
import com.pclogix.lanteacraft.client.render.P90BulletRenderer;
import com.pclogix.lanteacraft.client.render.TransportRingRenderer;
import com.pclogix.lanteacraft.client.render.ZpmHubRenderer;
import com.pclogix.lanteacraft.registry.ModBlockEntities;
import com.pclogix.lanteacraft.registry.ModEntities;
import com.pclogix.lanteacraft.registry.ModMenus;
import com.pclogix.lanteacraft.registry.ModSounds;
import com.pclogix.lanteacraft.network.ModNetworking;
import com.pclogix.lanteacraft.registry.ModItems;
import com.pclogix.lanteacraft.worldgen.LanteaDimensions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.Music;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.HumanoidArm;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterDimensionSpecialEffectsEvent;
import net.neoforged.neoforge.client.event.SelectMusicEvent;
import net.neoforged.neoforge.client.event.RenderPlayerEvent;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.common.NeoForge;
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
    private static final int MUSIC_MIN_DELAY = 12_000;
    private static final int MUSIC_MAX_DELAY = 24_000;
    private static final Music ABYDOS_MUSIC = new Music(ModSounds.ABYDOS_MUSIC, MUSIC_MIN_DELAY, MUSIC_MAX_DELAY, true);
    private static final Music EXPEDITION_MUSIC = new Music(ModSounds.EXPEDITION_MUSIC, MUSIC_MIN_DELAY, MUSIC_MAX_DELAY, true);

    public LanteaCraftClient(IEventBus modEventBus, ModContainer container) {
        // Allows NeoForge to create a config screen for this mod's configs.
        // The config screen is accessed by going to the Mods screen > clicking on your mod > clicking on config.
        // Do not forget to add translations for your config options to the en_us.json file.
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
        NeoForge.EVENT_BUS.addListener(LanteaCraftClient::selectDimensionMusic);
        NeoForge.EVENT_BUS.addListener(LanteaCraftClient::poseP90Player);
        ModNetworking.registerClient(modEventBus, (payload, context) ->
                context.enqueueWork(() -> InterServerTransferClientState.receive(payload)));
    }

    @SubscribeEvent
    static void onClientLoggingIn(ClientPlayerNetworkEvent.LoggingIn event) {
        InterServerTransferClientState.onLoggingIn(event.getConnection());
    }

    private static void poseP90Player(RenderPlayerEvent.Pre event) {
        if (!event.getEntity().isUsingItem() || !event.getEntity().getUseItem().is(ModItems.P90.get())) {
            return;
        }

        PlayerModel<?> model = event.getRenderer().getModel();
        if (event.getEntity().getMainArm() == HumanoidArm.RIGHT) {
            model.rightArmPose = HumanoidModel.ArmPose.CROSSBOW_HOLD;
            model.leftArmPose = HumanoidModel.ArmPose.ITEM;
        } else {
            model.leftArmPose = HumanoidModel.ArmPose.CROSSBOW_HOLD;
            model.rightArmPose = HumanoidModel.ArmPose.ITEM;
        }
    }

    private static void selectDimensionMusic(SelectMusicEvent event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null) {
            return;
        }

        if (minecraft.level.dimension().equals(LanteaDimensions.ABYDOS)) {
            event.overrideMusic(ABYDOS_MUSIC);
        } else if (minecraft.level.dimension().equals(LanteaDimensions.EXPEDITIONS)) {
            event.overrideMusic(EXPEDITION_MUSIC);
        }
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
        event.registerBlockEntityRenderer(ModBlockEntities.OFFERING_ALTAR.get(), OfferingAltarRenderer::new);
        event.registerEntityRenderer(ModEntities.TOKRA_TRADER.get(), TokraTraderRenderer::new);
        event.registerEntityRenderer(ModEntities.GOAULD_SOLDIER.get(), GoauldSoldierRenderer::new);
        event.registerEntityRenderer(ModEntities.STAFF_BLAST.get(), StaffBlastRenderer::new);
        event.registerEntityRenderer(ModEntities.P90_BULLET.get(), P90BulletRenderer::new);
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
