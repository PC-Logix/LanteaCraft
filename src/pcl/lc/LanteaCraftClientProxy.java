package pcl.lc;

import java.lang.reflect.Constructor;
import java.util.logging.Level;

import pcl.lc.gui.ScreenNaquadahGenerator;
import pcl.lc.gui.ScreenStargateBase;
import pcl.lc.gui.ScreenStargateController;
import pcl.lc.network.ClientPacketHandler;
import pcl.lc.network.LanteaPacket;
import pcl.lc.render.GenericBlockRenderer;
import pcl.lc.render.RotationOrientedBlockRenderer;
import pcl.lc.render.blocks.BlockStargateBaseRenderer;
import pcl.lc.render.blocks.BlockStargateRingRenderer;
import pcl.lc.render.model.NaquadahGeneratorModel;
import pcl.lc.render.model.StargateControllerModel;
import pcl.lc.render.tileentity.TileEntityNaquadahGeneratorRenderer;
import pcl.lc.render.tileentity.TileEntityStargateBaseRenderer;
import pcl.lc.render.tileentity.TileEntityStargateControllerRenderer;
import pcl.lc.tileentity.TileEntityNaquadahGenerator;
import pcl.lc.tileentity.TileEntityStargateBase;
import pcl.lc.tileentity.TileEntityStargateController;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundPool;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
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
		LanteaCraft.Render.modelNaquadahGenerator = new NaquadahGeneratorModel(
				"/assets/pcl_lc/models/naquada_generator.obj");

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

	}

	void addTileEntityRenderer(Class<? extends TileEntity> teClass, TileEntitySpecialRenderer renderer) {
		ClientRegistry.bindTileEntitySpecialRenderer(teClass, renderer);
	}

	void registerRenderer(GenericBlockRenderer renderer) {
		int id = RenderingRegistry.getNextAvailableRenderId();
		renderer.renderID = id;
		RenderingRegistry.registerBlockHandler(renderer);
	}

	void addScreen(Enum id, Class<? extends GuiScreen> cls) {
		registeredGUIs.put(id.ordinal(), cls);
	}

	@Override
	public Object getGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		Class<? extends GuiScreen> gui = LanteaCraft.getProxy().getGUI(ID);
		if (gui != null) {
			try {
				TileEntity entity = world.getBlockTileEntity(x, y, z);
				Constructor constr = gui.getConstructor(new Class<?>[] { entity.getClass(), EntityPlayer.class });
				Object val = constr.newInstance(entity, player);
				return val;
			} catch (Throwable t) {
				System.err.println("Could not create GUI, a " + t.getClass().getName() + " exception occurred.");
				t.printStackTrace(System.err);
			}
		}
		return null;
	}

	@Override
	public void handlePacket(LanteaPacket packet, Player player) {
		if (packet.getPacketIsForServer())
			defaultServerPacketHandler.handlePacket(packet, player);
		else
			defaultClientPacketHandler.handlePacket(packet, player);
	}

	@Override
	public void sendToServer(LanteaPacket packet) {
		LanteaCraft.getLogger().log(Level.INFO, "SGCraft sending packet to server: " + packet.toString());
		FMLClientHandler.instance().sendPacket(packet.toPacket());
	}

}
