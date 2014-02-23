package pcl.lc;

import java.lang.reflect.Constructor;
import java.util.logging.Level;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundPool;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import pcl.common.network.ModPacket;
import pcl.common.render.GenericBlockRenderer;
import pcl.common.render.RotationOrientedBlockRenderer;
import pcl.lc.guis.ScreenNaquadahGenerator;
import pcl.lc.guis.ScreenStargateBase;
import pcl.lc.guis.ScreenStargateController;
import pcl.lc.network.ClientPacketHandler;
import pcl.lc.render.blocks.BlockNaquadahGeneratorRenderer;
import pcl.lc.render.blocks.BlockStargateBaseRenderer;
import pcl.lc.render.blocks.BlockStargateControllerRenderer;
import pcl.lc.render.blocks.BlockStargateRingRenderer;
import pcl.lc.render.blocks.BlockVoidRenderer;
import pcl.lc.render.models.NaquadahGeneratorModel;
import pcl.lc.render.models.RingPlatformBaseModel;
import pcl.lc.render.models.RingPlatformRingModel;
import pcl.lc.render.models.StargateControllerModel;
import pcl.lc.render.tileentity.TileEntityNaquadahGeneratorRenderer;
import pcl.lc.render.tileentity.TileEntityRingPlatformRenderer;
import pcl.lc.render.tileentity.TileEntityStargateBaseRenderer;
import pcl.lc.render.tileentity.TileEntityStargateControllerRenderer;
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
import cpw.mods.fml.relauncher.Side;

public class LanteaCraftClientProxy extends LanteaCraftCommonProxy {

	public LanteaCraftClientProxy() {
		super();
		defaultClientPacketHandler = new ClientPacketHandler();
	}

	@Override
	public void preInit(FMLPreInitializationEvent e) {
		super.preInit(e);
	}

	@Override
	public void init(FMLInitializationEvent e) {
		super.init(e);
		registerSounds();
		registerScreens();
		registerRenderers();
	}

	public void registerSounds() {
		try {
			SoundPool pool = Minecraft.getMinecraft().sndManager.soundPoolSounds;

			for (String group : new String[] { "milkyway", "pegasus" }) {
				for (String tag : new String[] { "abort", "chevron_incoming", "chevron_lock", "close", "dhd_button",
						"open", "roll" }) {
					StringBuilder label = new StringBuilder().append("pcl_lc:stargate/").append(group).append("/");
					label.append(group).append("_").append(tag).append(".ogg");
					pool.addSound(label.toString());
				}
			}

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void registerScreens() {
		addScreen(LanteaCraft.EnumGUIs.StargateBase, ScreenStargateBase.class);
		addScreen(LanteaCraft.EnumGUIs.StargateController, ScreenStargateController.class);
		addScreen(LanteaCraft.EnumGUIs.NaquadahGenerator, ScreenNaquadahGenerator.class);
	}

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

	@Override
	public Object getGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		Class<? extends GuiScreen> gui = LanteaCraft.getProxy().getGUI(ID);
		if (gui != null)
			try {
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
			defaultServerPacketHandler.handlePacket(packet, player);
		else
			defaultClientPacketHandler.handlePacket(packet, player);
	}

	@Override
	public void sendToServer(ModPacket packet) {
		LanteaCraft.getLogger().log(Level.FINEST, "LanteaCraft sending packet to server: " + packet.toString());
		Packet250CustomPayload payload = packet.toPacket();
		payload.channel = BuildInfo.modID;
		FMLClientHandler.instance().sendPacket(payload);
	}

}
