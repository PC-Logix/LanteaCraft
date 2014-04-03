package pcl.lc;

import java.lang.reflect.Constructor;
import java.util.logging.Level;
import java.io.File;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;
import pcl.common.audio.ClientAudioEngine;
import pcl.common.network.ModPacket;
import pcl.common.render.GenericBlockRenderer;
import pcl.common.render.RotationOrientedBlockRenderer;
import pcl.lc.core.ClientTickHandler;
import pcl.lc.guis.GuiStatCollection;
import pcl.lc.guis.ScreenNaquadahGenerator;
import pcl.lc.guis.ScreenStargateBase;
import pcl.lc.guis.ScreenStargateController;
import pcl.lc.guis.ScreenStargateControllerEnergy;
import pcl.lc.network.ClientPacketHandler;
import pcl.lc.network.PacketLogger;
import pcl.lc.render.blocks.BlockNaquadahGeneratorRenderer;
import pcl.lc.render.blocks.BlockStargateBaseRenderer;
import pcl.lc.render.blocks.BlockStargateControllerRenderer;
import pcl.lc.render.blocks.BlockStargateRingRenderer;
import pcl.lc.render.blocks.BlockVoidRenderer;
import pcl.lc.render.models.NaquadahGeneratorModel;
import pcl.lc.render.models.RingPlatformBaseModel;
import pcl.lc.render.models.RingPlatformRingModel;
import pcl.lc.render.models.StargateControllerModel;
import pcl.lc.render.tileentity.TileEntityLanteaDecorGlassRenderer;
import pcl.lc.render.tileentity.TileEntityNaquadahGeneratorRenderer;
import pcl.lc.render.tileentity.TileEntityRingPlatformRenderer;
import pcl.lc.render.tileentity.TileEntityStargateBaseRenderer;
import pcl.lc.render.tileentity.TileEntityStargateControllerRenderer;
import pcl.lc.tileentity.TileEntityLanteaDecorGlass;
import pcl.lc.tileentity.TileEntityNaquadahGenerator;
import pcl.lc.tileentity.TileEntityRingPlatform;
import pcl.lc.tileentity.TileEntityStargateBase;
import pcl.lc.tileentity.TileEntityStargateController;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

public class LanteaCraftClientProxy extends LanteaCraftCommonProxy {
	public static class ClientHooks {
		private boolean shownStatGui = false;
		private LanteaCraftClientProxy proxy;

		public ClientHooks(LanteaCraftClientProxy proxy) {
			this.proxy = proxy;
		}

		@ForgeSubscribe
		public void openMainMenu(GuiOpenEvent event) {
			if ((event.gui instanceof GuiMainMenu) && !shownStatGui) {
				shownStatGui = true;
				// event.gui = new GuiStatCollection(event.gui,
				// proxy.analyticsHelper);
			}
		}
	}

	private ClientTickHandler clientTickHandler = new ClientTickHandler();
	private ClientHooks hooks = new ClientHooks(this);

	public LanteaCraftClientProxy() {
		super();
		if (BuildInfo.NET_DEBUGGING)
			clientPacketLogger = new PacketLogger(new File("lc-network-client.dat"));
		clientPacketHandler = new ClientPacketHandler(clientPacketLogger);
	}

	@Override
	public void preInit(FMLPreInitializationEvent e) {
		super.preInit(e);
	}

	@Override
	public void init(FMLInitializationEvent e) {
		super.init(e);
		MinecraftForge.EVENT_BUS.register(hooks);
		audioContext = new ClientAudioEngine();
		clientTickHandler.registerTickHost(audioContext);
		TickRegistry.registerTickHandler(clientTickHandler, Side.CLIENT);
		registerScreens();
		registerRenderers();

	}

	public void registerScreens() {
		addScreen(LanteaCraft.EnumGUIs.StargateBase, ScreenStargateBase.class);
		addScreen(LanteaCraft.EnumGUIs.StargateController, ScreenStargateController.class);
		addScreen(LanteaCraft.EnumGUIs.StargateControllerEnergy, ScreenStargateControllerEnergy.class);
		addScreen(LanteaCraft.EnumGUIs.NaquadahGenerator, ScreenNaquadahGenerator.class);
	}

	/**
	 * TODO: We can and should probably come up with some clever way of moving
	 * this into the sub Module init() method (maybe pass Side.SERVER/CLIENT or
	 * something?).
	 */
	public void registerRenderers() {
		LanteaCraft.Render.modelController = new StargateControllerModel("/assets/pcl_lc/models/dhd.obj");
		LanteaCraft.Render.modelNaquadahGenerator = new NaquadahGeneratorModel(
				"/assets/pcl_lc/models/naquadah_generator.obj");
		LanteaCraft.Render.modelRingPlatformBase = new RingPlatformBaseModel(
				"/assets/pcl_lc/models/transport_rings_base.obj");
		LanteaCraft.Render.modelRingPlatformRing = new RingPlatformRingModel(
				"/assets/pcl_lc/models/transport_rings.obj");

		LanteaCraft.Render.tileEntityBaseRenderer = new TileEntityStargateBaseRenderer();
		addTileEntityRenderer(TileEntityStargateBase.class, LanteaCraft.Render.tileEntityBaseRenderer);

		LanteaCraft.Render.tileEntityControllerRenderer = new TileEntityStargateControllerRenderer();
		addTileEntityRenderer(TileEntityStargateController.class, LanteaCraft.Render.tileEntityControllerRenderer);

		LanteaCraft.Render.tileEntityNaquadahGeneratorRenderer = new TileEntityNaquadahGeneratorRenderer();
		addTileEntityRenderer(TileEntityNaquadahGenerator.class, LanteaCraft.Render.tileEntityNaquadahGeneratorRenderer);

		LanteaCraft.Render.tileEntityRingPlatformRenderer = new TileEntityRingPlatformRenderer();
		addTileEntityRenderer(TileEntityRingPlatform.class, LanteaCraft.Render.tileEntityRingPlatformRenderer);

		LanteaCraft.Render.tileEntityLanteaDecorGlassRenderer = new TileEntityLanteaDecorGlassRenderer();
		addTileEntityRenderer(TileEntityLanteaDecorGlass.class, LanteaCraft.Render.tileEntityLanteaDecorGlassRenderer);

		LanteaCraft.Render.blockOrientedRenderer = new RotationOrientedBlockRenderer();
		registerRenderer(LanteaCraft.Render.blockOrientedRenderer);

		LanteaCraft.Render.blockStargateBaseRenderer = new BlockStargateBaseRenderer();
		registerRenderer(LanteaCraft.Render.blockStargateBaseRenderer);

		LanteaCraft.Render.blockStargateRingRenderer = new BlockStargateRingRenderer();
		registerRenderer(LanteaCraft.Render.blockStargateRingRenderer);

		LanteaCraft.Render.blockControllerRenderer = new BlockStargateControllerRenderer();
		registerRenderer(LanteaCraft.Render.blockControllerRenderer);

		LanteaCraft.Render.blockNaquadahGeneratorRenderer = new BlockNaquadahGeneratorRenderer();
		registerRenderer(LanteaCraft.Render.blockNaquadahGeneratorRenderer);

		LanteaCraft.Render.blockVoidRenderer = new BlockVoidRenderer();
		registerRenderer(LanteaCraft.Render.blockVoidRenderer);

	}

	void addTileEntityRenderer(Class<? extends TileEntity> teClass, TileEntitySpecialRenderer renderer) {
		ClientRegistry.bindTileEntitySpecialRenderer(teClass, renderer);
	}

	void registerRenderer(GenericBlockRenderer renderer) {
		renderer.renderID = RenderingRegistry.getNextAvailableRenderId();
		RenderingRegistry.registerBlockHandler(renderer);
	}

	void addScreen(Enum<?> id, Class<? extends GuiScreen> cls) {
		registeredGUIs.put(id.ordinal(), cls);
	}

	public void spawnEffect(EntityFX effect) {
		Minecraft.getMinecraft().effectRenderer.addEffect(effect);
	}

	@Override
	public Object getGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		LanteaCraft.getLogger().log(Level.FINE, String.format("Initializing GUI with ordinal %s.", ID));
		Class<? extends GuiScreen> gui = LanteaCraft.getProxy().getGUI(ID);
		if (gui != null)
			try {
				LanteaCraft.getLogger().log(Level.FINE, String.format("Initializing GUI of class %s.", gui.getName()));
				TileEntity entity = world.getBlockTileEntity(x, y, z);
				Constructor<?> constr = gui.getConstructor(new Class<?>[] { entity.getClass(), EntityPlayer.class });
				Object val = constr.newInstance(entity, player);
				return val;
			} catch (Throwable t) {
				LanteaCraft.getLogger().log(Level.SEVERE, String.format("Failed to create GUI with ID %s", ID), t);
			}
		return null;
	}

	@Override
	public void handlePacket(ModPacket packet, Player player) {
		if (packet.getPacketIsForServer())
			serverPacketHandler.handlePacket(packet, player);
		else
			clientPacketHandler.handlePacket(packet, player);
	}

	@Override
	public void sendToServer(ModPacket packet) {
		Packet250CustomPayload payload = packet.toPacket();
		payload.channel = BuildInfo.modID;
		LanteaCraft.getLogger().log(Level.INFO,
				String.format("sendToServer: 250 %s %s", payload.channel, payload.length));
		FMLClientHandler.instance().sendPacket(payload);
	}

	private void movePlayerToServer(String address, int port) {
		Minecraft mc = Minecraft.getMinecraft();
		mc.theWorld.sendQuittingDisconnectingPacket();
		mc.setServer(address, port);
	}

}
