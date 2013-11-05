//------------------------------------------------------------------------------------------------
//
//   SG Craft - Main Class
//
//------------------------------------------------------------------------------------------------

package gcewing.sg;

import gcewing.sg.base.BaseConfiguration;
import gcewing.sg.base.BaseMod;
import gcewing.sg.base.BaseModClient;
import gcewing.sg.base.BaseTEChunkManager;
import gcewing.sg.blocks.BlockNaquadah;
import gcewing.sg.blocks.BlockNaquadahOre;
import gcewing.sg.blocks.BlockStargateBase;
import gcewing.sg.blocks.BlockStargateController;
import gcewing.sg.blocks.BlockPegasusStargateBase;
import gcewing.sg.blocks.BlockPegasusStargateController;
import gcewing.sg.blocks.BlockPegasusStargateRing;
import gcewing.sg.blocks.BlockPortal;
import gcewing.sg.blocks.BlockStargateRing;
import gcewing.sg.container.ContainerStargateBase;
import gcewing.sg.core.GateAddressHelper;
import gcewing.sg.core.StargateNetworkChannel;
import gcewing.sg.core.EnumGuiList;
import gcewing.sg.generators.FeatureGeneration;
import gcewing.sg.generators.FeatureUnderDesertPyramid;
import gcewing.sg.generators.NaquadahOreWorldGen;
import gcewing.sg.generators.ChunkData;
import gcewing.sg.generators.TradeHandler;
import gcewing.sg.items.ItemPegasusStargateRing;
import gcewing.sg.items.ItemStargateRing;
import gcewing.sg.render.BaseOrientedCtrBlkRenderer;
import gcewing.sg.render.blocks.BlockStargateBaseRenderer;
import gcewing.sg.render.blocks.BlockStargateRingRenderer;
import gcewing.sg.render.model.StargateControllerModel;
import gcewing.sg.render.tileentity.TileEntityPegasusStargateBaseRenderer;
import gcewing.sg.render.tileentity.TileEntityStargateBaseRenderer;
import gcewing.sg.render.tileentity.TileEntityStargateControllerRenderer;
import gcewing.sg.tileentity.TileEntityStargateBase;
import gcewing.sg.tileentity.TileEntityStargateController;
import gcewing.sg.tileentity.TileEntityStargateRing;
import gcewing.sg.util.HelperCreativeTab;

import java.io.File;
import java.lang.reflect.Constructor;
import java.net.URL;

import net.minecraft.block.Block;
import net.minecraft.crash.CallableMinecraftVersion;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.structure.MapGenStructureIO;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.Property;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.terraingen.InitMapGenEvent;
import net.minecraftforge.event.world.ChunkDataEvent;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

@Mod(modid = Info.modID, name = Info.modName, version = Info.versionNumber)
@NetworkMod(clientSideRequired = true, serverSideRequired = false)
public class SGCraft extends BaseMod {

	/**
	 * The private instance of the mod. Use {@link #getInstance()} to access
	 * this object safely
	 */
	private static SGCraft mod;

	/**
	 * Returns the current instance singleton of the SGCraft mod object
	 * 
	 * @return The current, if any, instance of the SGCraft mod
	 */
	public static SGCraft getInstance() {
		return SGCraft.mod;
	}

	/**
	 * Public declaration of all Block objects
	 */
	public static class Blocks {
		public static BlockStargateBase sgBaseBlock;
		public static BlockStargateRing sgRingBlock;
		public static BlockStargateController sgControllerBlock;

		public static BlockPegasusStargateBase sgPegasusBaseBlock;
		public static BlockPegasusStargateRing sgPegasusRingBlock;
		public static BlockPegasusStargateController sgPegasusControllerBlock;

		public static BlockPortal sgPortalBlock;

		public static Block naquadahBlock;
		public static Block naquadahOre;
	}

	/**
	 * Public declaration of all Item objects
	 */
	public static class Items {
		public static Item naquadah;
		public static Item naquadahIngot;
		public static Item sgCoreCrystal;
		public static Item sgControllerCrystal;
	}

	/**
	 * Public declaration of all render objects
	 */
	public static class Render {
		public static StargateControllerModel modelController;

		public static BaseOrientedCtrBlkRenderer blockOrientedRenderer;

		public static BlockStargateBaseRenderer blockBaseRenderer;
		public static BlockStargateRingRenderer blockRingRenderer;

		public static TileEntityStargateBaseRenderer tileEntityBaseRenderer;
		public static TileEntityPegasusStargateBaseRenderer tileEntityPegausBaseRenderer;
		public static TileEntityStargateControllerRenderer tileEntityControllerRenderer;
	}

	public static HelperCreativeTab sgCraftTab = new HelperCreativeTab(CreativeTabs.getNextID(), "SGCraft") {
		@Override
		public ItemStack getIconItemStack() {
			return new ItemStack(Item.bakedPotato);
		}
	};

	public static boolean GenerateStruct = false;
	public static boolean GalacticraftCompat = false;
	public static int RenderHD = 32;
	public static boolean HDModels = true;
	public static boolean ActiveGateExplosion = true;

	public static StargateNetworkChannel channel;
	public static BaseTEChunkManager chunkManager;

	public static boolean addOresToExistingWorlds;
	public static NaquadahOreWorldGen naquadahOreGenerator;
	public static int tokraVillagerID;

	public BaseConfiguration config;
	public String modPackage;
	public String assetKey = "gcewing_sg";
	public String resourceDir;
	public URL resourceURL;
	public BaseModClient client;
	public IGuiHandler proxy;
	public boolean serverSide, clientSide;
	public boolean debugGui;

	File cfgFile;

	public SGCraft() {
		SGCraft.mod = this;
	}

	@EventHandler
	public void preInit(FMLPreInitializationEvent e) {
		serverSide = e.getSide().isServer();
		clientSide = e.getSide().isClient();
		if (clientSide) {
			client = new SGCraftClient(this);
			proxy = client;
		}
		cfgFile = e.getSuggestedConfigurationFile();
		config = new BaseConfiguration(cfgFile);
		setConfig(config, clientSide);
		if (client != null)
			client.preInit(e);
	}

	@EventHandler
	public void init(FMLInitializationEvent e) {
		MinecraftForge.EVENT_BUS.register(this);
		if (client != null)
			client.init(e);
		configure();
		channel = new StargateNetworkChannel(Info.modID);
		chunkManager = new BaseTEChunkManager(this);
		chunkManager.debug = true;
	}

	@EventHandler
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
		if (client != null)
			client.postInit(e);
		if (proxy == null)
			proxy = this;
		NetworkRegistry.instance().registerGuiHandler(this, proxy);
		if (config.extended)
			config.save();
	}

	void configure() {
		TileEntityStargateController.configure(config);
		NaquadahOreWorldGen.configure(config);
		TileEntityStargateBase.configure(config);
		addOresToExistingWorlds = config.getBoolean("options", "addOresToExistingWorlds", false);

		Property GenerateStruct = config.get("options", "GenerateStructures", true);
		SGCraft.GenerateStruct = config.getBoolean("stargate", "GenerateStructures", true);
		GenerateStruct.comment = "true/false Enables/Disables generation of Gate Rooms under Desert Pyramids";

		Property GalacticraftCompat = config.get("options", "GalacticCraftCompat", false);
		SGCraft.GalacticraftCompat = config.getBoolean("stargate", "GalacticCraftCompat", false);
		GalacticraftCompat.comment = "true/false Enables/Disables Galcticraft support THIS WILL CHANGE ALL EXISTING ADDRESSES!";

		Property textureRes = config.get("graphics_options", "textureRes", 32);
		SGCraft.RenderHD = config.getInteger("graphics_options", "textureRes", 32);
		textureRes.comment = "This value must be either 32, 64, or 128. Controls Textures Resolution";

		Property HDModels = config.get("graphics_options", "HDModels", true);
		SGCraft.HDModels = config.getBoolean("graphics_options", "HDModels", true);
		HDModels.comment = "True/False Should HD models be loaded for DHDs.";

		ActiveGateExplosion = config.getBoolean("options", "ActiveGateExplosion", true);

		if (SGCraft.GalacticraftCompat == true)
			GateAddressHelper.minDimension = -99;
	}

	void registerOther() {
		if (GenerateStruct == true) {
			System.out.println("GenerateStructure = true");
			MinecraftForge.TERRAIN_GEN_BUS.register(this);
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
		//Blocks.sgPegasusRingBlock = (BlockPegasusStargateRing) registerBlock(BlockPegasusStargateRing.class, ItemPegasusStargateRing.class,
		//		GCESGCompatHelper.getBlockMapping("blockPegasusRing"), "stargatePegasusRing", "Pegasus Stargate Ring Segment");
		

		Blocks.sgBaseBlock = (BlockStargateBase) registerBlock(BlockStargateBase.class, ItemBlock.class,
				GCESGCompatHelper.getBlockMapping("blockBase"), "stargateBase", "Stargate Base");
		//Blocks.sgPegasusBaseBlock = (BlockPegasusStargateBase) registerBlock(BlockPegasusStargateBase.class, ItemBlock.class, 
		//		GCESGCompatHelper.getBlockMapping("blockPegasusBase"), "stargatePegasusBase", "Pegasus Stargate Base");
		
		
		Blocks.sgControllerBlock = (BlockStargateController) registerBlock(BlockStargateController.class, ItemBlock.class,
				GCESGCompatHelper.getBlockMapping("blockController"), "stargateController", "Stargate Controller");
		//Blocks.sgPegasusControllerBlock = (BlockPegasusStargateController) registerBlock(BlockPegasusStargateController.class, ItemBlock.class,
		//		GCESGCompatHelper.getBlockMapping("blockPegasusController"), "stargatePegasusController", "Pegasus Stargate Controller");


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
			block.setUnlocalizedName(assetKey + ":" + unlocalizedName);
			block.setTextureName(assetKey + ":" + unlocalizedName + "_" + SGCraft.RenderHD);
			if (clientSide)
				block.setCreativeTab(sgCraftTab);
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
			item.setUnlocalizedName(assetKey + ":" + unlocalizedName);
			item.setTextureName(assetKey + ":" + unlocalizedName + "_" + SGCraft.RenderHD);
			if (clientSide)
				item.setCreativeTab(sgCraftTab);
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
		newRecipe(Blocks.sgRingBlock, 1, "CCC", "NNN", "SSS", 'S', smoothSandstone, 'N', "ingotNaquadahAlloy", 'C',
				chiselledSandstone);
		newRecipe(sgChevronBlock, "CgC", "NpN", "SrS", 'S', smoothSandstone, 'N', "ingotNaquadahAlloy", 'C',
				chiselledSandstone, 'g', Item.glowstone, 'r', Item.redstone, 'p', Item.enderPearl);
		newRecipe(Blocks.sgBaseBlock, 1, "CrC", "NeN", "ScS", 'S', smoothSandstone, 'N', "ingotNaquadahAlloy", 'C',
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
		addContainer(EnumGuiList.SGBase, ContainerStargateBase.class);
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
		GameRegistry.registerTileEntity(TileEntityStargateBase.class, GCESGCompatHelper.getTileEntityMapping("tileEntityBase"));
		GameRegistry.registerTileEntity(TileEntityStargateRing.class, GCESGCompatHelper.getTileEntityMapping("tileEntityRing"));
		GameRegistry.registerTileEntity(TileEntityStargateController.class, GCESGCompatHelper.getTileEntityMapping("tileEntityController"));
	}

	@ForgeSubscribe
	public void onChunkLoad(ChunkDataEvent.Load e) {
		Chunk chunk = e.getChunk();
		ChunkData.onChunkLoad(e);
	}

	@ForgeSubscribe
	public void onChunkSave(ChunkDataEvent.Save e) {
		Chunk chunk = e.getChunk();
		ChunkData.onChunkSave(e);
	}

	@ForgeSubscribe
	public void onInitMapGen(InitMapGenEvent e) {
		FeatureGeneration.onInitMapGen(e);
	}

	public static ResourceLocation getResource(String path) {
		return new ResourceLocation(SGCraft.getInstance().assetKey, path);
	}

}
