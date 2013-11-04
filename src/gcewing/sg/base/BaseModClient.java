//------------------------------------------------------------------------------------------------
//
//   Greg's Mod Base - Generic Client Proxy
//
//------------------------------------------------------------------------------------------------

package gcewing.sg.base;

import gcewing.sg.base.BaseMod.VSBinding;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundPool;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.registry.VillagerRegistry;

/**
 * TODO: This stub is to be removed, for our purposes it's pretty useless and
 * can be replaced with inline loading code.
 */
public class BaseModClient implements IGuiHandler {

	protected static class IDBinding<T> {
		public int id;
		public T object;
	}

	protected static class BRBinding extends IDBinding<ISimpleBlockRenderingHandler> {
	}

	protected static Map<String, BRBinding> blockRenderers = new HashMap<String, BRBinding>();

	protected Minecraft mc = Minecraft.getMinecraft();
	protected BaseMod base;

	Map<Integer, Class<? extends GuiScreen>> screenClasses = new HashMap<Integer, Class<? extends GuiScreen>>();

	public BaseModClient(BaseMod mod) {
		base = mod;
	}

	public void preInit(FMLPreInitializationEvent e) {
	}

	public void init(FMLInitializationEvent e) {
	}

	public void postInit(FMLPostInitializationEvent e) {
		registerScreens();
		registerRenderers();
		registerSounds();
		registerOther();
		registerSavedVillagerSkins();
	}

	public void registerSavedVillagerSkins() {
		VillagerRegistry reg = VillagerRegistry.instance();
		for (VSBinding b : base.registeredVillagers)
			reg.registerVillagerSkin(b.id, b.object);
	}

	public String qualifyName(String name) {
		return getClass().getPackage().getName() + "." + name;
	}

	public void registerOther() {
	}

	public void registerScreens() {
	}

	public void addScreen(Enum id, Class<? extends GuiScreen> cls) {
		addScreen(id.ordinal(), cls);
	}

	public void addScreen(int id, Class<? extends GuiScreen> cls) {
		screenClasses.put(id, cls);
	}

	public void registerRenderers() {
	}

	public void addItemRenderer(Item item, IItemRenderer renderer) {
		MinecraftForgeClient.registerItemRenderer(item.itemID, renderer);
	}

	public void addItemRenderer(Block block, IItemRenderer renderer) {
		MinecraftForgeClient.registerItemRenderer(block.idDropped(0, null, 0), renderer);
	}

	public void addTileEntityRenderer(Class<? extends TileEntity> teClass, TileEntitySpecialRenderer renderer) {
		ClientRegistry.bindTileEntitySpecialRenderer(teClass, renderer);
	}

	public void addEntityRenderer(Class<? extends Entity> entityClass, Render renderer) {
		RenderingRegistry.registerEntityRenderingHandler(entityClass, renderer);
	}

	public void registerSounds() {
		try {
			SoundPool pool = mc.sndManager.soundPoolSounds;
			pool.addSound("gcewing_sg:sg_abort.ogg");
			pool.addSound("gcewing_sg:sg_close.ogg");
			pool.addSound("gcewing_sg:sg_dial.ogg");
			pool.addSound("gcewing_sg:sg_open.ogg");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		return base.getServerGuiElement(id, player, world, x, y, z);
	}

	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		Class cls = screenClasses.get(id);
		if (cls != null)
			return base.createGuiElement(cls, player, world, x, y, z);
		else
			return getGuiScreen(id, player, world, x, y, z);
	}

	public GuiScreen getGuiScreen(int id, EntityPlayer player, World world, int x, int y, int z) {
		return null;
	}

}
