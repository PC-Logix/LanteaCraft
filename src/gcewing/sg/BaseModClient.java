//------------------------------------------------------------------------------------------------
//
//   Greg's Mod Base - Generic Client Proxy
//
//------------------------------------------------------------------------------------------------

package gcewing.sg;

import java.net.*;
import java.util.*;

import net.minecraft.block.*;
import net.minecraft.client.*;
import net.minecraft.client.gui.*;
import net.minecraft.client.audio.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.client.renderer.tileentity.*;
import net.minecraft.entity.*;
import net.minecraft.entity.player.*;
import net.minecraft.inventory.*;
import net.minecraft.item.*;
import net.minecraft.network.*;
import net.minecraft.network.packet.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.src.*;
import net.minecraft.tileentity.*;
import net.minecraft.util.*;
import net.minecraft.world.*;
import net.minecraftforge.common.*;
import net.minecraftforge.client.*;
import cpw.mods.fml.client.registry.*;
import cpw.mods.fml.common.event.*;
import cpw.mods.fml.common.network.*;
import cpw.mods.fml.common.registry.*;
import gcewing.sg.BaseMod.IBlock;
import gcewing.sg.BaseMod.VSBinding;

public class BaseModClient implements IGuiHandler {

	static class IDBinding<T> {
		public int id;
		public T object;
	}
	
	static class BRBinding extends IDBinding<ISimpleBlockRenderingHandler> {}
	
	static Map<String, BRBinding>
		blockRenderers = new HashMap<String, BRBinding>();

	public Minecraft mc;
	BaseMod base;
	boolean debugSound = false;

	Map<Integer, Class<? extends GuiScreen>> screenClasses =
		new HashMap<Integer, Class<? extends GuiScreen>>();

	public BaseModClient(BaseMod mod) {
		//System.out.printf("%s: BaseModClient()\n", this);
		base = mod;
		mc = ModLoader.getMinecraftInstance();
		//loadDependentClasses();
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
		registerImplicitBlockRenderers();
		registerSavedVillagerSkins();
	}
	
	void registerImplicitBlockRenderers() {
		for (IBlock block : base.registeredBlocks) {
			String name = block.getQualifiedRendererClassName();
			if (name != null) {
				BRBinding b = getBlockRendererForName(name);
				if (b != null) {
					//System.out.printf("BaseModClient: Binding renderer id %s to %s\n", b.id, block);
					block.setRenderType(b.id);
				}
			}
		}
	}
	
	void registerSavedVillagerSkins() {
		VillagerRegistry reg = VillagerRegistry.instance();
		for (VSBinding b : base.registeredVillagers)
			reg.registerVillagerSkin(b.id, b.object);
	}
	
	BRBinding getBlockRendererForName(String name) {
		//System.out.printf("BaseModClient: Getting block renderer class %s\n", name);
		BRBinding b = blockRenderers.get(name);
		if (b == null) {
			//System.out.printf("BaseModClient: Loading block renderer class %s\n", name);
			Class cls;
			ISimpleBlockRenderingHandler h;
			try {
				cls = Class.forName(name);
			}
			catch (ClassNotFoundException e) {
				throw new RuntimeException(String.format("Block renderer class %s not found", name));
			}
			try {
				h = (ISimpleBlockRenderingHandler)cls.newInstance();
			}
			catch (Exception e) {
				throw new RuntimeException(e);
			}
			b = new BRBinding();
			b.id = RenderingRegistry.getNextAvailableRenderId();
			b.object = h;
			RenderingRegistry.registerBlockHandler(b.id, h);
			blockRenderers.put(name, b);
		}
		return b;
	}
	
//	boolean classIsPresent(String name) {
//		try {
//			Class.forName(name);
//			return true;
//		}
//		catch (ClassNotFoundException e) {
//			return false;
//		}
//	}
	
	String qualifyName(String name) {
		return getClass().getPackage().getName() + "." + name;
	}
	
	void registerOther() {}
	
	//-------------- Screen registration --------------------------------------------------------
	
	void registerScreens() {
		//
		//  Make calls to addScreen() here.
		//
		//  Screen classes registered using these methods must implement either:
		//
		//  (1) A static method create(EntityPlayer player, World world, int x, int y, int z)
		//  (2) A constructor MyScreen(EntityPlayer player, World world, int x, int y, int z)
		//
		//System.out.printf("%s: BaseModClient.registerScreens\n", this);
	}
	
	public void addScreen(Enum id, Class<? extends GuiScreen> cls) {
		addScreen(id.ordinal(), cls);
	}

	public void addScreen(int id, Class<? extends GuiScreen> cls) {
		screenClasses.put(id, cls);
	}
	
	//-------------- Renderer registration --------------------------------------------------------
	
	void registerRenderers() {
		// Make calls to addBlockRenderer(), addItemRenderer() and addTileEntityRenderer() here
	}

	void addBlockRenderer(IBlock block, ISimpleBlockRenderingHandler renderer) {
		addBlockRenderer(renderer, block);
	}
	
	void addBlockRenderer(ISimpleBlockRenderingHandler renderer, IBlock... blocks) {
		int renderID = RenderingRegistry.getNextAvailableRenderId();
		for (IBlock block : blocks) {
			//System.out.printf("BaseModClient: Registering %s with id %s for %s\n", renderer, renderID, block);
			block.setRenderType(renderID);
			RenderingRegistry.registerBlockHandler(renderID, renderer);
		}
	}
	
	void addItemRenderer(Item item, IItemRenderer renderer) {
		MinecraftForgeClient.registerItemRenderer(item.itemID, renderer);
	}
	
	void addItemRenderer(Block block, IItemRenderer renderer) {
		MinecraftForgeClient.registerItemRenderer(block.idDropped(0, null, 0), renderer);
	}
	
	void addTileEntityRenderer(Class <? extends TileEntity> teClass, TileEntitySpecialRenderer renderer) {
		ClientRegistry.bindTileEntitySpecialRenderer(teClass, renderer);
	}
	
	void addEntityRenderer(Class<? extends Entity> entityClass, Render renderer) {
		RenderingRegistry.registerEntityRenderingHandler(entityClass, renderer);
	}
	
	//-------------- Rendering --------------------------------------------------------
	
	public void bindTexture(String path) {
		if (SGCraft.RenderHD == true) {
			ResourceLocation rsrc = base.resourceLocation("textures_HD/" + path);
			TextureManager tm = Minecraft.getMinecraft().getTextureManager();
			tm.bindTexture(rsrc);
		} else {
			ResourceLocation rsrc = base.resourceLocation("textures_SD/" + path);
			TextureManager tm = Minecraft.getMinecraft().getTextureManager();
			tm.bindTexture(rsrc);
		}

	}
	
	//-------------- Internal --------------------------------------------------------
	
	void registerSounds() {
		try {
			//if (debugSound)
				//System.out.printf("%s: BaseModClient.registerSounds\n", base.modPackage);
			SoundPool pool = mc.sndManager.soundPoolSounds;
			/*
			Set<String> items = base.listResources("sound");
			for (String item : items) {
				String soundSpec = String.format("%s:%s", base.assetKey, item);
				if (debugSound)
					System.out.printf("%s: BaseModClient.registerSounds: %s\n", base.modPackage, soundSpec);
				pool.addSound(soundSpec);
			}*/
			pool.addSound("gcewing_sg:sg_abort.ogg");
			pool.addSound("gcewing_sg:sg_close.ogg");
			pool.addSound("gcewing_sg:sg_dial.ogg");
			pool.addSound("gcewing_sg:sg_open.ogg");
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Returns a Container to be displayed to the user. 
	 * On the client side, this needs to return a instance of GuiScreen
	 * On the server side, this needs to return a instance of Container
	 *
	 * @param ID The Gui ID Number
	 * @param player The player viewing the Gui
	 * @param world The current world
	 * @param x X Position
	 * @param y Y Position
	 * @param z Z Position
	 * @return A GuiScreen/Container to be displayed to the user, null if none.
	 */
	
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
	
	GuiScreen getGuiScreen(int id, EntityPlayer player, World world, int x, int y, int z) {
		//  Called when screen id not found in registry
		//System.out.printf("%s: BaseModClient.getGuiScreen: No GuiScreen class found for gui id %d\n", this, id);
		return null;
	}

}
