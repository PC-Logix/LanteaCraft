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
import pcl.lc.render.models.NaquadahGeneratorModel;
import pcl.lc.render.models.StargateControllerModel;
import pcl.lc.render.tileentity.TileEntityNaquadahGeneratorRenderer;
import pcl.lc.render.tileentity.TileEntityStargateBaseRenderer;
import pcl.lc.render.tileentity.TileEntityStargateControllerRenderer;
import pcl.lc.tileentity.TileEntityNaquadahGenerator;
import pcl.lc.tileentity.TileEntityStargateBase;
import pcl.lc.tileentity.TileEntityStargateController;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.Player;

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
			pool.addSound("pcl_lc:sg1_abort.ogg");
			pool.addSound("pcl_lc:sg1_close.ogg");
			pool.addSound("pcl_lc:sg1_dial.ogg");
			pool.addSound("pcl_lc:gate_open.ogg");
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
		LanteaCraft.Render.modelNaquadahGenerator = new NaquadahGeneratorModel("/assets/pcl_lc/models/naquadah_generator.obj");

		LanteaCraft.Render.tileEntityBaseRenderer = new TileEntityStargateBaseRenderer();
		addTileEntityRenderer(TileEntityStargateBase.class, LanteaCraft.Render.tileEntityBaseRenderer);

		LanteaCraft.Render.tileEntityControllerRenderer = new TileEntityStargateControllerRenderer();
		addTileEntityRenderer(TileEntityStargateController.class, LanteaCraft.Render.tileEntityControllerRenderer);

		LanteaCraft.Render.tileEntityNaquadahGeneratorRenderer = new TileEntityNaquadahGeneratorRenderer();
		addTileEntityRenderer(TileEntityNaquadahGenerator.class, LanteaCraft.Render.tileEntityNaquadahGeneratorRenderer);

		LanteaCraft.Render.blockOrientedRenderer = new RotationOrientedBlockRenderer();
		registerRenderer(LanteaCraft.Render.blockOrientedRenderer);

		LanteaCraft.Render.blockBaseRenderer = new BlockStargateBaseRenderer();
		registerRenderer(LanteaCraft.Render.blockBaseRenderer);

		LanteaCraft.Render.blockRingRenderer = new BlockStargateRingRenderer();
		registerRenderer(LanteaCraft.Render.blockRingRenderer);

		LanteaCraft.Render.blockControllerRenderer = new BlockStargateControllerRenderer();
		registerRenderer(LanteaCraft.Render.blockControllerRenderer);

		LanteaCraft.Render.blockNaquadahGeneratorRenderer = new BlockNaquadahGeneratorRenderer();
		registerRenderer(LanteaCraft.Render.blockNaquadahGeneratorRenderer);

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
