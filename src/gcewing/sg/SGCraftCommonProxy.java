package gcewing.sg;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gcewing.sg.SGCraft.Blocks;
import gcewing.sg.SGCraft.Items;
import gcewing.sg.base.BaseConfiguration;
import gcewing.sg.base.BaseTEChunkManager;
import gcewing.sg.blocks.BlockNaquadah;
import gcewing.sg.blocks.BlockNaquadahOre;
import gcewing.sg.blocks.BlockStargateBase;
import gcewing.sg.blocks.BlockStargateController;
import gcewing.sg.blocks.BlockStargateRing;
import gcewing.sg.config.ConfigValue;
import gcewing.sg.container.ContainerStargateBase;
import gcewing.sg.core.EnumGuiList;
import gcewing.sg.core.GateAddressHelper;
import gcewing.sg.core.StargateNetworkChannel;
import gcewing.sg.forge.HelperGUIHandler;
import gcewing.sg.generators.ChunkData;
import gcewing.sg.generators.FeatureGeneration;
import gcewing.sg.generators.FeatureUnderDesertPyramid;
import gcewing.sg.generators.NaquadahOreWorldGen;
import gcewing.sg.generators.TradeHandler;
import gcewing.sg.items.ItemStargateRing;
import gcewing.sg.tileentity.TileEntityStargateBase;
import gcewing.sg.tileentity.TileEntityStargateController;
import gcewing.sg.tileentity.TileEntityStargateRing;
import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.crash.CallableMinecraftVersion;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.structure.MapGenStructureIO;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.Property;
import net.minecraftforge.event.terraingen.InitMapGenEvent;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.common.registry.VillagerRegistry;
import cpw.mods.fml.common.registry.VillagerRegistry.IVillageTradeHandler;
import cpw.mods.fml.relauncher.Side;

public class SGCraftCommonProxy {

	protected File cfgFile;
	protected BaseConfiguration config;

	public StargateNetworkChannel channel;
	public BaseTEChunkManager chunkManager;

	private NaquadahOreWorldGen naquadahOreGenerator;
	private int tokraVillagerID;

	// Registries
	protected Map<Integer, Class<? extends Container>> registeredContainers = new HashMap<Integer, Class<? extends Container>>();
	protected Map<Integer, Class<? extends GuiScreen>> registeredGUIs = new HashMap<Integer, Class<? extends GuiScreen>>();
	protected List<VSBinding> registeredVillagers = new ArrayList<VSBinding>();

	protected ArrayList<ConfigValue<?>> configValues = new ArrayList<ConfigValue<?>>();

	protected static class IDBinding<T> {
		public int id;
		public T object;
	}

	static class VSBinding extends IDBinding<ResourceLocation> {
	};

	public SGCraftCommonProxy() {
		System.out.println("This is " + BuildInfo.modName + " version " + BuildInfo.versionNumber + " build "
				+ BuildInfo.buildNumber + " as modid " + BuildInfo.modID);
	}

	public void preInit(FMLPreInitializationEvent e) {
		cfgFile = e.getSuggestedConfigurationFile();
		config = new BaseConfiguration(cfgFile);
		configure();
	}

	public void init(FMLInitializationEvent e) {
		MinecraftForge.EVENT_BUS.register(SGCraft.getInstance());
		chunkManager = new BaseTEChunkManager(SGCraft.getInstance());
		NetworkRegistry.instance().registerGuiHandler(SGCraft.getInstance(), new HelperGUIHandler());
	}

	public void postInit(FMLPostInitializationEvent e) {
		registerBlocks();
		registerTileEntities();
		registerItems();
		registerOres();
		registerRecipes();
		registerRandomItems();
		registerWorldGenerators();
		registerContainers();
		registerVillagers();
		registerOther();
		channel = new StargateNetworkChannel(BuildInfo.modID);
		if (config.extended)
			config.save();
	}

	public void onChunkLoad(ChunkDataEvent.Load e) {
		Chunk chunk = e.getChunk();
		ChunkData.onChunkLoad(e);
	}

	public void onChunkSave(ChunkDataEvent.Save e) {
		Chunk chunk = e.getChunk();
		ChunkData.onChunkSave(e);
	}

	public void onInitMapGen(InitMapGenEvent e) {
		FeatureGeneration.onInitMapGen(e);
	}

	void configure() {
		TileEntityStargateController.configure(config);
		NaquadahOreWorldGen.configure(config);
		TileEntityStargateBase.configure(config);
		configValues.add(new ConfigValue<Boolean>("addOresToExistingWorlds", config.getBoolean("options",
				"addOresToExistingWorlds", false)));

		Property GenerateStruct = config.get("options", "GenerateStructures", true);
		configValues.add(new ConfigValue<Boolean>("doGenerateStructures", config.getBoolean("stargate",
				"GenerateStructures", true)));
		GenerateStruct.comment = "true/false Enables/Disables generation of Gate Rooms under Desert Pyramids";

		Property GalacticraftCompat = config.get("options", "GalacticCraftCompat", false);
		configValues.add(new ConfigValue<Boolean>("doGalacticCraftCompat", config.getBoolean("stargate",
				"GalacticCraftCompat", true)));
		GalacticraftCompat.comment = "true/false Enables/Disables Galcticraft support THIS WILL CHANGE ALL EXISTING ADDRESSES!";

		Property textureRes = config.get("graphics_options", "textureRes", 32);
		configValues.add(new ConfigValue<Integer>("renderQuality", config.getInteger("graphics_options", "textureRes",
				32)));
		textureRes.comment = "This value must be either 32, 64, or 128. Controls Textures Resolution";

		Property HDModels = config.get("graphics_options", "HDModels", true);
		configValues.add(new ConfigValue<Boolean>("renderUseModels", config.getBoolean("graphics_options", "HDModels",
				true)));
		HDModels.comment = "True/False Should HD models be loaded for DHDs.";

		configValues.add(new ConfigValue<Boolean>("doGateExplosion", config.getBoolean("options",
				"ActiveGateExplosion", true)));

		if (((ConfigValue<Boolean>) getConfigValue("doGalacticCraftCompat")).getValue())
			GateAddressHelper.minDimension = -99;
	}

	void registerOther() {
		if (((ConfigValue<Boolean>) getConfigValue("doGenerateStructures")).getValue()) {
			MinecraftForge.TERRAIN_GEN_BUS.register(SGCraft.getInstance());
			try {
				if (new CallableMinecraftVersion(null).minecraftVersion().equals("1.6.4"))
					MapGenStructureIO.func_143031_a(FeatureUnderDesertPyramid.class, "SGCraft:DesertPyramid");
			} catch (Throwable e) {
				System.out.println("registerOther threw an exception");
			}
		}
	}

	void registerBlocks() {
		Blocks.sgRingBlock = (BlockStargateRing) registerBlock(BlockStargateRing.class, ItemStargateRing.class,
				GCESGCompatHelper.getBlockMapping("blockRing"), "stargateRing", "Stargate Ring Segment");
		// Blocks.sgPegasusRingBlock = (BlockPegasusStargateRing)
		// registerBlock(BlockPegasusStargateRing.class,
		// ItemPegasusStargateRing.class,
		// GCESGCompatHelper.getBlockMapping("blockPegasusRing"),
		// "stargatePegasusRing", "Pegasus Stargate Ring Segment");

		Blocks.sgBaseBlock = (BlockStargateBase) registerBlock(BlockStargateBase.class, ItemBlock.class,
				GCESGCompatHelper.getBlockMapping("blockBase"), "stargateBase", "Stargate Base");
		// Blocks.sgPegasusBaseBlock = (BlockPegasusStargateBase)
		// registerBlock(BlockPegasusStargateBase.class, ItemBlock.class,
		// GCESGCompatHelper.getBlockMapping("blockPegasusBase"),
		// "stargatePegasusBase", "Pegasus Stargate Base");

		Blocks.sgControllerBlock = (BlockStargateController) registerBlock(BlockStargateController.class,
				ItemBlock.class, GCESGCompatHelper.getBlockMapping("blockController"), "stargateController",
				"Stargate Controller");
		// Blocks.sgPegasusControllerBlock = (BlockPegasusStargateController)
		// registerBlock(BlockPegasusStargateController.class, ItemBlock.class,
		// GCESGCompatHelper.getBlockMapping("blockPegasusController"),
		// "stargatePegasusController", "Pegasus Stargate Controller");

		Blocks.naquadahBlock = registerBlock(BlockNaquadah.class, ItemBlock.class,
				GCESGCompatHelper.getBlockMapping("blockNaquadah"), "naquadahBlock", "Naquadah Alloy Block");
		Blocks.naquadahOre = registerBlock(BlockNaquadahOre.class, ItemBlock.class,
				GCESGCompatHelper.getBlockMapping("oreNaquadah"), "naquadahOre", "Naquadah Ore");
	}

	public Block registerBlock(Class<? extends Block> classOf, Class<? extends ItemBlock> itemClassOf,
			String idForName, String unlocalizedName, String localizedName) {
		try {
			int id = config.getBlock(unlocalizedName, 4095).getInt();
			Constructor<? extends Block> ctor = classOf.getConstructor(int.class);
			Block block = ctor.newInstance(id);
			block.setUnlocalizedName(SGCraft.getInstance().getAssetKey() + ":" + unlocalizedName);
			block.setTextureName(SGCraft.getInstance().getAssetKey() + ":" + unlocalizedName + "_" + getRenderMode());
			block.setCreativeTab(SGCraft.getInstance().getCreativeTab());
			GameRegistry.registerBlock(block, itemClassOf, idForName);
			LanguageRegistry.addName(block, localizedName);
			return block;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public Item registerItem(Class<? extends Item> classOf, String idForName, String unlocalizedName,
			String localizedName) {
		try {
			int id = config.getItem(unlocalizedName, 31743).getInt();
			Constructor<? extends Item> ctor = classOf.getConstructor(int.class);
			Item item = ctor.newInstance(id);
			item.setUnlocalizedName(SGCraft.getInstance().getAssetKey() + ":" + unlocalizedName);
			item.setTextureName(SGCraft.getInstance().getAssetKey() + ":" + unlocalizedName + "_" + getRenderMode());
			item.setCreativeTab(SGCraft.getInstance().getCreativeTab());
			GameRegistry.registerItem(item, idForName);
			LanguageRegistry.addName(item, localizedName);
			return item;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	void registerItems() {
		Items.naquadah = registerItem(Item.class, GCESGCompatHelper.getBlockMapping("itemNaquadah"), "naquadah",
				"Naquadah");
		Items.naquadahIngot = registerItem(Item.class, GCESGCompatHelper.getBlockMapping("itemNaquadahIngot"),
				"naquadahIngot", "Naquadah Alloy Ingot");
		Items.sgCoreCrystal = registerItem(Item.class, GCESGCompatHelper.getBlockMapping("itemCoreCrystal"),
				"sgCoreCrystal", "Stargate Core Crystal");
		Items.sgControllerCrystal = registerItem(Item.class,
				GCESGCompatHelper.getBlockMapping("itemControllerCrystal"), "sgControllerCrystal",
				"Stargate Controller Crystal");
	}

	void registerOres() {
		registerOre("oreNaquadah", Blocks.naquadahOre);
		registerOre("naquadah", Items.naquadah);
		registerOre("ingotNaquadahAlloy", Items.naquadahIngot);
	}

	void registerRecipes() {
		ItemStack chiselledSandstone = new ItemStack(Block.sandStone, 1, 1);
		ItemStack smoothSandstone = new ItemStack(Block.sandStone, 1, 2);
		ItemStack sgChevronBlock = new ItemStack(Blocks.sgRingBlock, 1, 1);
		ItemStack blueDye = new ItemStack(Item.dyePowder, 1, 4);
		ItemStack orangeDye = new ItemStack(Item.dyePowder, 1, 14);
		if (config.getBoolean("options", "allowCraftingNaquadah", false))
			newShapelessRecipe(Items.naquadah, 1, Item.coal, Item.slimeBall, Item.blazePowder);
		newRecipe(Blocks.sgRingBlock, 1, "ICI", "NNN", "III", 'I', Item.ingotIron, 'N', "ingotNaquadahAlloy", 'C',
				chiselledSandstone);
		newRecipe(sgChevronBlock, "CgC", "NpN", "IrI", 'I', Item.ingotIron, 'N', "ingotNaquadahAlloy", 'C',
				chiselledSandstone, 'g', Item.glowstone, 'r', Item.redstone, 'p', Item.enderPearl);
		newRecipe(Blocks.sgBaseBlock, 1, "CrC", "NeN", "IcI", 'I', Item.ingotIron, 'N', "ingotNaquadahAlloy", 'C',
				chiselledSandstone, 'r', Item.redstone, 'e', Item.eyeOfEnder, 'c', Items.sgCoreCrystal);
		newRecipe(Blocks.sgControllerBlock, 1, "bbb", "OpO", "OcO", 'b', Block.stoneButton, 'O', Block.obsidian, 'p',
				Item.enderPearl, 'r', Item.redstone, 'c', Items.sgControllerCrystal);
		newShapelessRecipe(Items.naquadahIngot, 1, "naquadah", Item.ingotIron);
		newRecipe(Blocks.naquadahBlock, 1, "NNN", "NNN", "NNN", 'N', "ingotNaquadahAlloy");
		newRecipe(Items.naquadahIngot, 9, "B", 'B', Blocks.naquadahBlock);
		if (config.getBoolean("options", "allowCraftingCrystals", false)) {
			newRecipe(Items.sgCoreCrystal, 1, "bbr", "rdb", "brb", 'b', blueDye, 'r', Item.redstone, 'd', Item.diamond);
			newRecipe(Items.sgControllerCrystal, 1, "roo", "odr", "oor", 'o', orangeDye, 'r', Item.redstone, 'd',
					Item.diamond);
		}
	}

	void registerContainers() {
		addContainer(EnumGuiList.SGBase.ordinal(), ContainerStargateBase.class);
	}

	void registerRandomItems() {
		String[] categories = { ChestGenHooks.MINESHAFT_CORRIDOR, ChestGenHooks.PYRAMID_DESERT_CHEST,
				ChestGenHooks.PYRAMID_JUNGLE_CHEST, ChestGenHooks.STRONGHOLD_LIBRARY, ChestGenHooks.VILLAGE_BLACKSMITH };
		addRandomChestItem(new ItemStack(Blocks.sgBaseBlock), 1, 1, 2, categories);
		addRandomChestItem(new ItemStack(Blocks.sgControllerBlock), 1, 1, 1, categories);
		addRandomChestItem(new ItemStack(Blocks.sgRingBlock, 1, 0), 1, 3, 8, categories);
		addRandomChestItem(new ItemStack(Blocks.sgRingBlock, 1, 1), 1, 3, 7, categories);
		addRandomChestItem(new ItemStack(Items.sgCoreCrystal, 1, 0), 1, 1, 2, categories);
		addRandomChestItem(new ItemStack(Items.sgControllerCrystal, 1, 0), 1, 1, 1, categories);
	}

	void registerWorldGenerators() {
		if (config.getBoolean("options", "enableNaquadahOre", true)) {
			naquadahOreGenerator = new NaquadahOreWorldGen();
			GameRegistry.registerWorldGenerator(naquadahOreGenerator);
		}
	}

	void registerVillagers() {
		tokraVillagerID = addVillager(config.getVillager("tokra"), "tokra",
				SGCraft.getResource("textures/skins/tokra.png"));
		addTradeHandler(tokraVillagerID, new TradeHandler());
	}

	void registerTileEntities() {
		GameRegistry.registerTileEntity(TileEntityStargateBase.class,
				GCESGCompatHelper.getTileEntityMapping("tileEntityBase"));
		GameRegistry.registerTileEntity(TileEntityStargateRing.class,
				GCESGCompatHelper.getTileEntityMapping("tileEntityRing"));
		GameRegistry.registerTileEntity(TileEntityStargateController.class,
				GCESGCompatHelper.getTileEntityMapping("tileEntityController"));
	}

	public ConfigValue<?> getConfigValue(String name) {
		for (ConfigValue<?> item : configValues)
			if (item.getName().equalsIgnoreCase(name))
				return item;
		return null;
	}

	public int getRenderMode() {
		int mode = ((ConfigValue<Integer>) getConfigValue("renderQuality")).getValue();
		if (mode <= 32)
			return 32;
		if (mode > 32 && mode <= 64)
			return 64;
		if (mode > 64 && mode <= 128)
			return 128;
		return 32; // invalid value?
	}

	public boolean isUsingModels() {
		return ((ConfigValue<Boolean>) getConfigValue("renderUseModels")).getValue();
	}

	Container getGuiContainer(int id, EntityPlayer player, World world, int x, int y, int z) {
		return null;
	}

	Object createGuiElement(Class cls, EntityPlayer player, World world, int x, int y, int z) {
		try {
			try {
				return cls.getMethod("create", EntityPlayer.class, World.class, int.class, int.class, int.class)
						.invoke(null, player, world, x, y, z);
			} catch (NoSuchMethodException e) {
				return cls.getConstructor(EntityPlayer.class, World.class, int.class, int.class, int.class)
						.newInstance(player, world, x, y, z);
			}
		} catch (Exception e) {
			Throwable cause = e.getCause();
			if (cause != null)
				cause.printStackTrace();
			else
				e.printStackTrace();
			return null;
		}
	}

	public int addVillager(int id, String name, ResourceLocation skin) {
		VSBinding b = new VSBinding();
		b.id = id;
		b.object = skin;
		registeredVillagers.add(b);
		return id;
	}

	public void addTradeHandler(int villagerID, IVillageTradeHandler handler) {
		VillagerRegistry.instance().registerVillageTradeHandler(villagerID, handler);
	}

	public void registerOre(String name, Block block) {
		registerOre(name, new ItemStack(block));
	}

	public void registerOre(String name, Item item) {
		registerOre(name, new ItemStack(item));
	}

	public void registerOre(String name, ItemStack item) {
		OreDictionary.registerOre(name, item);
	}

	public void newRecipe(Item product, int qty, Object... params) {
		newRecipe(new ItemStack(product, qty), params);
	}

	public void newRecipe(Block product, int qty, Object... params) {
		newRecipe(new ItemStack(product, qty), params);
	}

	public void newRecipe(ItemStack product, Object... params) {
		GameRegistry.addRecipe(new ShapedOreRecipe(product, params));
	}

	public void newShapelessRecipe(Item product, int qty, Object... params) {
		newShapelessRecipe(new ItemStack(product, qty), params);
	}

	public void newShapelessRecipe(ItemStack product, Object... params) {
		GameRegistry.addRecipe(new ShapelessOreRecipe(product, params));
	}

	public void addContainer(int id, Class<? extends Container> cls) {
		registeredContainers.put(id, cls);
	}

	public void addRandomChestItem(ItemStack stack, int minQty, int maxQty, int weight, String... category) {
		WeightedRandomChestContent item = new WeightedRandomChestContent(stack, minQty, maxQty, weight);
		for (String element : category)
			ChestGenHooks.addItem(element, item);
	}

	public NaquadahOreWorldGen getOreGenerator() {
		return naquadahOreGenerator;
	}

	public Class<? extends Container> getContainer(int id) {
		return registeredContainers.get(id);
	}

	public Object getGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		return null;
	}

	public Class<? extends GuiScreen> getGUI(int id) {
		return registeredGUIs.get(id);
	}

}
