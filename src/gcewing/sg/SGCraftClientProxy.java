package gcewing.sg;

import gcewing.sg.core.EnumGuiList;
import gcewing.sg.gui.ScreenStargateBase;
import gcewing.sg.gui.ScreenStargateController;
import gcewing.sg.render.BaseOrientedCtrBlkRenderer;
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
			pool.addSound("gcewing_sg:sg_abort.ogg");
			pool.addSound("gcewing_sg:sg_close.ogg");
			pool.addSound("gcewing_sg:sg_dial.ogg");
			pool.addSound("gcewing_sg:sg_open.ogg");
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

		SGCraft.Render.blockOrientedRenderer = new BaseOrientedCtrBlkRenderer();
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
	public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		return super.getServerGuiElement(id, player, world, x, y, z);
	}

	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		Class cls = registeredGUIs.get(id);
		if (cls != null)
			return super.createGuiElement(cls, player, world, x, y, z);
		else
			return getGuiScreen(id, player, world, x, y, z);
	}

	public GuiScreen getGuiScreen(int id, EntityPlayer player, World world, int x, int y, int z) {
		return null;
	}

	@Override
	public void openGui(EntityPlayer player, int id, World world, int x, int y, int z) {
		player.openGui(SGCraft.getInstance(), id, world, x, y, z);
	}

}
