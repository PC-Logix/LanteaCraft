//------------------------------------------------------------------------------------------------
//
//   Greg's Mod Base - Generic Mod
//
//------------------------------------------------------------------------------------------------

package gcewing.sg;

import java.io.*;
import java.lang.reflect.*;
import java.net.*;
import java.util.*;
import java.util.jar.*;

import net.minecraft.block.*;
import net.minecraft.creativetab.*;
import net.minecraft.entity.*;
import net.minecraft.entity.player.*;
import net.minecraft.inventory.*;
import net.minecraft.item.*;
import net.minecraft.util.*;
import net.minecraft.world.*;
import net.minecraftforge.common.*;
import net.minecraftforge.client.*;
import net.minecraftforge.oredict.*;
import cpw.mods.fml.common.*;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.*;
import cpw.mods.fml.common.network.*;
import cpw.mods.fml.common.registry.*;
import cpw.mods.fml.common.registry.VillagerRegistry.*;
import cpw.mods.fml.relauncher.ReflectionHelper;

public class BaseMod implements IGuiHandler {

	interface IBlock {
		public void setRenderType(int id);
		public String getQualifiedRendererClassName();
	}
	
	interface ITileEntity {
		public void onAddedToWorld();
	}
	
	static class IDBinding<T> {
		public int id;
		public T object;
	}
	
	public BaseConfiguration config;
	public String modPackage;
	public String assetKey;
	public String resourceDir; // path to resources directory with leading and trailing slashes
	//public String textureFile; // path to default texture file with leading slash
	public URL resourceURL; // URL to the resources directory
	//public BaseMod base;
	public BaseModClient client;
	public IGuiHandler proxy;
	public boolean serverSide, clientSide;
	public boolean debugGui;

	File cfgFile;
	List<IBlock> registeredBlocks = new ArrayList<IBlock>();

	public String resourcePath(String fileName) {
		return resourceDir + fileName;
	}

	public BaseMod() {
		modPackage = "gcewing.sg";
		assetKey = modPackage.replace(".", "_");
		String resourceRelDir = "assets/" + assetKey + "/";
		resourceDir = "/" + resourceRelDir;
		resourceURL = getClass().getClassLoader().getResource(resourceRelDir);
	}

	//@Mod.PreInit
	@EventHandler
	public void preInit(FMLPreInitializationEvent e) {
		serverSide = e.getSide().isServer();
		clientSide = e.getSide().isClient();
		if (clientSide) {
			client = initClient();
			proxy = client;
		}
		cfgFile = e.getSuggestedConfigurationFile();
		loadConfig();
		boolean[] configMarkers = ReflectionHelper.getPrivateValue(Configuration.class, config, "configMarkers");
		preallocateBlockIDs(configMarkers);
		preallocateItemIDs(configMarkers);
		if (client != null)
			client.preInit(e);
	}
	
	void preallocateBlockIDs(boolean[] configMarkers) {
		ConfigCategory items = config.getCategory(config.CATEGORY_BLOCK);
		for (Property prop : items.getValues().values()) {
			int id = prop.getInt();
			if (id != -1) {
				//System.out.printf("BaseMod.preallocateItemIDs: Marking block id %d as in use\n", id);
				configMarkers[id] = true;
			}
		}
	}
	
	void preallocateItemIDs(boolean[] configMarkers) {
		ConfigCategory items = config.getCategory(config.CATEGORY_ITEM);
		for (Property prop : items.getValues().values()) {
			int id = prop.getInt();
			if (id != -1) {
				//System.out.printf("BaseMod.preallocateItemIDs: Marking item id %d as in use\n", id);
				configMarkers[id + 256] = true;
			}
		}
	}
	
	//@Mod.Init
	@EventHandler
	public void init(FMLInitializationEvent e) {
		MinecraftForge.EVENT_BUS.register(this);
		if (client != null)
			client.init(e);
	}
	
	//@Mod.PostInit
	@EventHandler
	public void postInit(FMLPostInitializationEvent e) {
		registerBlocks();
		registerItems();
		registerOres();
		registerRecipes();
		registerTileEntities();
		registerRandomItems();
		registerWorldGenerators();
		registerContainers();
		registerEntities();
		registerVillagers();
		registerOther();
		if (client != null)
			client.postInit(e);
		if (proxy == null)
			proxy = this;
		NetworkRegistry.instance().registerGuiHandler(this, proxy);
		saveConfig();
	}

	void loadConfig() {
		config = new BaseConfiguration(cfgFile);
	}

	void saveConfig() {
		if (config.extended)
			config.save();
	}

	String qualifiedName(String name) {
		return modPackage + "." + name;
	}
	
	//----------------- Client Proxy -------------------------------------------------------------
	
	BaseModClient initClient() {
		return new BaseModClient(this);
	}

	//--------------- Item construction ----------------------------------------------------------
	
	public Item newItem(String name, String title) {
		return newItem(name, Item.class, title);
	}

	public <ITEM extends Item> ITEM newItem(String name, Class<ITEM> cls, String title) {
		try {
			int id = config.getItem(name, 31743).getInt();
			Constructor<ITEM> ctor = cls.getConstructor(int.class);
			ITEM item = ctor.newInstance(id);
			String qualName = assetKey + ":" + name;
			item.setUnlocalizedName(qualName);
			//item.func_111206_d(qualName.toLowerCase()); // Set default icon name
				item.setTextureName(qualName + "_" + SGCraft.RenderHD); // Set default icon name
			LanguageRegistry.addName(item, title);
			if (clientSide) {
				if (item.getCreativeTab() == null)
					item.setCreativeTab(CreativeTabs.tabMisc);
			}
			//System.out.printf("BaseMod.newItem: %s unlocalizedName = %s title = %s\n", item, item.getUnlocalizedName(), title);
			return item;
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	//--------------- Block construction ----------------------------------------------------------

	public Block newBlock(String name, String title) {
		return newBlock(name, Block.class, title);
	}
	
	public <BLOCK extends Block> BLOCK newBlock(String name, Class<BLOCK> cls, String title) {
		return newBlock(name, cls, ItemBlock.class, title);
	}
	
	public <BLOCK extends Block> BLOCK newBlock(String name, Class<BLOCK> cls, Class itemClass, String title) {
		try {
			int id = config.getBlock(name, 4095).getInt();
			Constructor<BLOCK> ctor = cls.getConstructor(int.class);
			BLOCK block = ctor.newInstance(id);
			String qualName = assetKey + ":" + name;
			block.setUnlocalizedName(qualName);
			//block.func_111022_d(qualName.toLowerCase()); // Set default icon name
			block.setTextureName(qualName + "_" + SGCraft.RenderHD); // Set default icon name
			GameRegistry.registerBlock(block, itemClass);
			if (title != null) {
				LanguageRegistry.addName(block, title);
				if (clientSide) {
					//System.out.printf("%s: BaseMod.newBlock: %s: creative tab = %s\n",
					//	this, block.getUnlocalizedName(), block.getCreativeTabToDisplayOn());
					if (block.getCreativeTabToDisplayOn() == null && !title.startsWith("["))
						block.setCreativeTab(CreativeTabs.tabMisc);
				}
			}
			if (block instanceof IBlock)
				registeredBlocks.add((IBlock)block);
			return block;
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	//--------------- Ore registration ----------------------------------------------------------

	public void registerOre(String name, Block block) {
		OreDictionary.registerOre(name, new ItemStack(block));
	}
	
	public void registerOre(String name, Item item) {
		OreDictionary.registerOre(name, item);
	}
	
	public static boolean blockMatchesOre(Block block, String name) {
		return stackMatchesOre(new ItemStack(block), name);
	}

	public static boolean itemMatchesOre(Item item, String name) {
		return stackMatchesOre(new ItemStack(item), name);
	}

	public static boolean stackMatchesOre(ItemStack stack, String name) {
		int id = OreDictionary.getOreID(stack);
		return id == OreDictionary.getOreID(name);
	}

	//--------------- Recipe construction ----------------------------------------------------------

	public void newRecipe(Item product, int qty, Object... params) {
		newRecipe(new ItemStack(product, qty), params);
	}
	
	public void newRecipe(Block product, int qty, Object... params) {
		newRecipe(new ItemStack(product, qty), params);
	}

	public void newRecipe(ItemStack product, Object... params) {
		GameRegistry.addRecipe(new ShapedOreRecipe(product, params));
	}

	public void newShapelessRecipe(Block product, int qty, Object... params) {
		newShapelessRecipe(new ItemStack(product, qty), params);
	}
	
	public void newShapelessRecipe(Item product, int qty, Object... params) {
		newShapelessRecipe(new ItemStack(product, qty), params);
	}
	
	public void newShapelessRecipe(ItemStack product, Object... params) {
		GameRegistry.addRecipe(new ShapelessOreRecipe(product, params));
	}

	public void newSmeltingRecipe(Item product, int qty, Item input) {
		GameRegistry.addSmelting(input.itemID, new ItemStack(product, qty), 0);
	}
	
	public void newSmeltingRecipe(Item product, int qty, Block input) {
		GameRegistry.addSmelting(input.blockID, new ItemStack(product, qty), 0);
	}
	
	//--------------- Dungeon loot ----------------------------------------------------------

	public void addRandomChestItem(ItemStack stack, int minQty, int maxQty, int weight, String... category) {
		WeightedRandomChestContent item = new WeightedRandomChestContent(stack, minQty, maxQty, weight);
		for (int i = 0; i < category.length; i++)
			ChestGenHooks.addItem(category[i], item);
	}

	//--------------- Entity registration ----------------------------------------------------------

	public void registerEntity(Class<? extends Entity> cls, String name, Enum id) {
		registerEntity(cls, name, id.ordinal());
	}
	
	public void registerEntity(Class<? extends Entity> cls, String name, int id) {
		registerEntity(cls, name, id, 1, true);
	}

	public void registerEntity(Class<? extends Entity> cls, String name, Enum id,
		int updateFrequency, boolean sendVelocityUpdates)
	{
		registerEntity(cls, name, id.ordinal(), updateFrequency, sendVelocityUpdates);
	}
	
	public void registerEntity(Class<? extends Entity> cls, String name, int id,
		int updateFrequency, boolean sendVelocityUpdates)
	{
		//System.out.printf("%s: BaseMod.registerEntity: %s, \"%s\", %s\n", getClass().getSimpleName(), cls.getSimpleName(), name, id);
		EntityRegistry.registerModEntity(cls, name, id, /*base*/this, 256, updateFrequency, sendVelocityUpdates);
	}

	//--------------- Villager registration -------------------------------------------------
	
	static class VSBinding extends IDBinding<ResourceLocation> {};
	
	public List<VSBinding> registeredVillagers = new ArrayList<VSBinding>();
	
	int addVillager(String name, ResourceLocation skin) {
		int id = config.getVillager(name);
		VSBinding b = new VSBinding();
		b.id = id;
		b.object = skin;
		registeredVillagers.add(b);
		return id;
	}
	
	void addTradeHandler(int villagerID, IVillageTradeHandler handler) {
		VillagerRegistry.instance().registerVillageTradeHandler(villagerID, handler);
	}

	//--------------- Method stubs ----------------------------------------------------------

	void registerBlocks() {}
	void registerItems() {}
	void registerOres() {}
	void registerRecipes() {}
	void registerTileEntities() {}
	void registerRandomItems() {}
	void registerWorldGenerators() {}
	void registerEntities() {}
	void registerVillagers() {}
	void registerOther() {}
	
	//--------------- Resources ----------------------------------------------------------
	
	public ResourceLocation resourceLocation(String path) {
		return new ResourceLocation(assetKey, path);
	}
	
	public String soundName(String name) {
		return assetKey + ":" + name;
	}
	
	public ResourceLocation textureLocation(String path) {
			return resourceLocation("textures/" + path);
		
	}

	public Set<String> listResources(String subdir) {
		try {
			Set<String>result = new HashSet<String>();
			String protocol = resourceURL.getProtocol();
			if (protocol.equals("jar")) {
				String resPath = resourceURL.getPath();
				int pling = resPath.lastIndexOf("!");
				URL jarURL = new URL(resPath.substring(0, pling));
				String resDirInJar = resPath.substring(pling + 2);
				String prefix = resDirInJar + subdir + "/";
				//System.out.printf("BaseMod.listResources: looking for names starting with %s\n", prefix);
				JarFile jar = new JarFile(new File(jarURL.toURI()));
				Enumeration<JarEntry> entries = jar.entries();
				while (entries.hasMoreElements()) {
					String name = entries.nextElement().getName();
					if (name.startsWith(prefix) && !name.endsWith("/") && !name.contains("/.")) {
						//System.out.printf("BaseMod.listResources: name = %s\n", name);
						result.add(name.substring(prefix.length()));
					}
				}
			}
			else
				throw new RuntimeException("Resource URL protocol " + protocol + " not supported");
			return result;
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	//--------------- GUIs - Registration ------------------------------------------------

	void registerContainers() {
		//  Make calls to addContainer() here.
		//
		//  Container classes registered using these methods must implement either:
		//
		//  (1) A static method create(EntityPlayer player, World world, int x, int y, int z)
		//  (2) A constructor MyContainer(EntityPlayer player, World world, int x, int y, int z)
	}
	
	public void addContainer(Enum id, Class<? extends Container> cls) {
		addContainer(id.ordinal(), cls);
	}

	public void addContainer(int id, Class<? extends Container> cls) {
		containerClasses.put(id, cls);
	}
	
	//--------------- GUIs  - Invoking -------------------------------------------------

	public void openGui(EntityPlayer player, Enum id, World world, int x, int y, int z) {
		openGui(player, id.ordinal(), world, x, y, z);
	}

	public void openGui(EntityPlayer player, int id, World world, int x, int y, int z) {
		//if (debugGui)
			//System.out.printf("BaseMod.openGui: for %s with id %s in %s at (%s, %s, %s)\n", /*base*/this, id, world, x, y, z);
		player.openGui(/*base*/this, id, world, x, y, z);
	}
	
	//--------------- GUIs  - Inernal -------------------------------------------------

	Map<Integer, Class<? extends Container>> containerClasses =
		new HashMap<Integer, Class<? extends Container>>();

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
		//if (debugGui)
			//System.out.printf("BaseMod.getServerGuiElement: for id %s\n", id);
		Class cls = containerClasses.get(id);
		Object result;
		if (cls != null)
			result = createGuiElement(cls, player, world, x, y, z);
		else
			result = getGuiContainer(id, player, world, x, y, z);
		//if (debugGui)
			//System.out.printf("BaseMod.getServerGuiElement: Returning %s\n", result);
		return result;
	}
	
	Container getGuiContainer(int id, EntityPlayer player, World world, int x, int y, int z) {
		//  Called when container id not found in registry
		//if (debugGui)
			//System.out.printf("%s: BaseMod.getGuiContainer: No Container class found for gui id %d\n", this, id);
		return null;
	}
	
	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		return null;
	}
	
	Object createGuiElement(Class cls, EntityPlayer player, World world, int x, int y, int z) {
		try {
			try {
				//if (debugGui)
					//System.out.printf("BaseMod.createGuiElement: Invoking create method of %s for %s in %s\n", cls, player, world);
				return cls.getMethod("create", EntityPlayer.class, World.class, int.class, int.class, int.class)
					.invoke(null, player, world, x, y, z);
			}
			catch (NoSuchMethodException e) {
				//if (debugGui)
					//System.out.printf("BaseMod.createGuiElement: Invoking constructor of %s\n", cls);
				return cls.getConstructor(EntityPlayer.class, World.class, int.class, int.class, int.class)
					.newInstance(player, world, x, y, z);
			}
		}
		catch (Exception e) {
			Throwable cause = e.getCause();
			//System.out.printf("BaseMod.createGuiElement: %s: %s\n", e, cause);
			if (cause != null)
				cause.printStackTrace();
			else
				e.printStackTrace();
			//throw new RuntimeException(e);
			return null;
		}
	}
	

}
