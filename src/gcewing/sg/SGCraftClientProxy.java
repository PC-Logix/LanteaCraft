package gcewing.sg;

import java.lang.reflect.Constructor;

import gcewing.sg.core.EnumGuiList;
import gcewing.sg.gui.ScreenStargateBase;
import gcewing.sg.gui.ScreenStargateController;
import gcewing.sg.render.RotationOrientedBlockRenderer;
import gcewing.sg.render.GenericBlockRenderer;
import gcewing.sg.render.blocks.BlockStargateBaseRenderer;
import gcewing.sg.render.blocks.BlockStargateRingRenderer;
import gcewing.sg.render.model.StargateControllerModel;
import gcewing.sg.render.tileentity.TileEntityPegasusStargateBaseRenderer;
import gcewing.sg.render.tileentity.TileEntityStargateBaseRenderer;
import gcewing.sg.render.tileentity.TileEntityStargateControllerRenderer;
import gcewing.sg.tileentity.TileEntityPegasusStargateBase;
import gcewing.sg.tileentity.TileEntityStargateBase;
import gcewing.sg.tileentity.TileEntityStargateController;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundPool;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.IGuiHandler;

public class SGCraftClientProxy extends SGCraftCommonProxy {

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
			pool.addSound("gcewing_sg:sg1_abort.ogg");
			pool.addSound("gcewing_sg:sg1_close.ogg");
			pool.addSound("gcewing_sg:sg1_dial.ogg");
			pool.addSound("gcewing_sg:sg1_open.ogg");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void registerScreens() {
		addScreen(EnumGuiList.SGBase, ScreenStargateBase.class);
		addScreen(EnumGuiList.SGController, ScreenStargateController.class);
	}

	public void registerRenderers() {
		SGCraft.Render.modelController = new StargateControllerModel("/assets/gcewing_sg/models/dhd.obj");

		SGCraft.Render.tileEntityBaseRenderer = new TileEntityStargateBaseRenderer();
		addTileEntityRenderer(TileEntityStargateBase.class, SGCraft.Render.tileEntityBaseRenderer);

		SGCraft.Render.tileEntityPegausBaseRenderer = new TileEntityPegasusStargateBaseRenderer();
		addTileEntityRenderer(TileEntityPegasusStargateBase.class, SGCraft.Render.tileEntityPegausBaseRenderer);

		SGCraft.Render.tileEntityControllerRenderer = new TileEntityStargateControllerRenderer();
		addTileEntityRenderer(TileEntityStargateController.class, SGCraft.Render.tileEntityControllerRenderer);

		SGCraft.Render.blockOrientedRenderer = new RotationOrientedBlockRenderer();
		registerRenderer(SGCraft.Render.blockOrientedRenderer);

		SGCraft.Render.blockBaseRenderer = new BlockStargateBaseRenderer();
		registerRenderer(SGCraft.Render.blockBaseRenderer);

		SGCraft.Render.blockRingRenderer = new BlockStargateRingRenderer();
		registerRenderer(SGCraft.Render.blockRingRenderer);
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
		Class<? extends GuiScreen> gui = SGCraft.getProxy().getGUI(ID);
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

}
