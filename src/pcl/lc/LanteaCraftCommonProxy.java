package pcl.lc;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.crash.CallableMinecraftVersion;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.MapGenStructureIO;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.Property;
import net.minecraftforge.event.terraingen.InitMapGenEvent;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import pcl.common.base.TileEntityChunkManager;
import pcl.common.helpers.AnalyticsHelper;
import pcl.common.helpers.ConfigValue;
import pcl.common.helpers.ConfigurationHelper;
import pcl.common.helpers.GUIHandler;
import pcl.common.helpers.NetworkHelpers;
import pcl.common.helpers.RegistrationHelper;
import pcl.common.network.ModPacket;
import pcl.lc.LanteaCraft.Blocks;
import pcl.lc.LanteaCraft.Fluids;
import pcl.lc.LanteaCraft.Items;
import pcl.lc.blocks.BlockNaquadah;
import pcl.lc.blocks.BlockNaquadahGenerator;
import pcl.lc.blocks.BlockNaquadahOre;
import pcl.lc.blocks.BlockStargateBase;
import pcl.lc.blocks.BlockStargateController;
import pcl.lc.blocks.BlockStargateRing;
import pcl.lc.compat.UpgradeHelper;
import pcl.lc.containers.ContainerNaquadahGenerator;
import pcl.lc.containers.ContainerStargateBase;
import pcl.lc.core.GateAddressHelper;
import pcl.lc.fluids.BlockLiquidNaquadah;
import pcl.lc.fluids.ItemSpecialBucket;
import pcl.lc.fluids.LiquidNaquadah;
import pcl.lc.items.ItemControllerCrystal;
import pcl.lc.items.ItemCoreCrystal;
import pcl.lc.items.ItemDebugTool;
import pcl.lc.items.ItemEnergyCrystal;
import pcl.lc.items.ItemNaquadah;
import pcl.lc.items.ItemNaquadahIngot;
import pcl.lc.items.ItemStargateRing;
import pcl.lc.items.ItemTokraSpawnEgg;
import pcl.lc.network.ClientPacketHandler;
import pcl.lc.network.ServerPacketHandler;
import pcl.lc.tileentity.TileEntityNaquadahGenerator;
import pcl.lc.tileentity.TileEntityStargateBase;
import pcl.lc.tileentity.TileEntityStargateController;
import pcl.lc.tileentity.TileEntityStargateRing;
import pcl.lc.worldgen.FeatureGeneration;
import pcl.lc.worldgen.FeatureUnderDesertPyramid;
import pcl.lc.worldgen.NaquadahOreWorldGen;
import pcl.lc.worldgen.TradeHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.VillagerRegistry;
import cpw.mods.fml.common.registry.VillagerRegistry.IVillageTradeHandler;

public class LanteaCraftCommonProxy {

	protected File cfgFile;
	protected ConfigurationHelper config;
	protected ArrayList<ConfigValue<?>> configValues = new ArrayList<ConfigValue<?>>();
	public TileEntityChunkManager chunkManager;

	private NaquadahOreWorldGen naquadahOreGenerator;
	private int tokraVillagerID;

	private AnalyticsHelper analyticsHelper = new AnalyticsHelper(false, null);

	protected Map<Integer, Class<? extends Container>> registeredContainers = new HashMap<Integer, Class<? extends Container>>();
	protected Map<Integer, Class<? extends GuiScreen>> registeredGUIs = new HashMap<Integer, Class<? extends GuiScreen>>();
	protected Map<String, VSBinding> registeredVillagers = new HashMap<String, VSBinding>();

	protected Map<String, ResourceLocation> resourceCache = new HashMap<String, ResourceLocation>();

	protected ClientPacketHandler defaultClientPacketHandler;
	protected ServerPacketHandler defaultServerPacketHandler;
	protected NetworkHelpers networkHelpers;

	public static UpgradeHelper upgradeHelper;

	protected class VSBinding {
		public int id;
		public ResourceLocation object;
	};

	public LanteaCraftCommonProxy() {
		System.out.println("This is " + BuildInfo.modName + " version " + BuildInfo.versionNumber + " build "
				+ BuildInfo.buildNumber + " as modid " + BuildInfo.modID);
		defaultServerPacketHandler = new ServerPacketHandler();
		networkHelpers = new NetworkHelpers();
	}

	public void preInit(FMLPreInitializationEvent e) {
		cfgFile = e.getSuggestedConfigurationFile();
		config = new ConfigurationHelper(cfgFile);
		configure();
	}

	public void init(FMLInitializationEvent e) {
		LanteaCraft.getLogger().log(Level.INFO, "LanteaCraft setting up...");
		MinecraftForge.EVENT_BUS.register(LanteaCraft.getInstance());
		MinecraftForge.EVENT_BUS.register(LanteaCraft.getSpecialBucketHandler());
		chunkManager = new TileEntityChunkManager(LanteaCraft.getInstance());
		NetworkRegistry.instance().registerGuiHandler(LanteaCraft.getInstance(), new GUIHandler());
		networkHelpers.init();
	}

	public void postInit(FMLPostInitializationEvent e) {
		registerBlocks();
		registerTileEntities();
		registerItems();
		registerFluids();
		registerOres();
		registerRecipes();
		registerRandomItems();
		registerWorldGenerators();
		registerContainers();
		registerVillagers();
		registerOther();
		if (config.extended)
			config.save();
		LanteaCraft.getLogger().log(Level.INFO, "LanteaCraft done setting up!");

		LanteaCraft.getLogger().log(Level.INFO, "[COMPAT] LanteaCraft looking for other versions of SGCraft...");
		if (UpgradeHelper.detectSGCraftInstall() || UpgradeHelper.detectSGCraftReloadedInstall()) {
			upgradeHelper = new UpgradeHelper();
			if (UpgradeHelper.detectSGCraftInstall())
				upgradeHelper.hookSGCraft();
			if (UpgradeHelper.detectSGCraftReloadedInstall())
				upgradeHelper.hookSGCraftReloaded();
		}
		LanteaCraft.getLogger().log(Level.INFO, "[COMPAT] LanteaCraft done looking for other versions.");
	}

	public NaquadahOreWorldGen getOreGenerator() {
		return naquadahOreGenerator;
	}

	public void onInitMapGen(InitMapGenEvent e) {
		LanteaCraft.getLogger().log(Level.FINE, "InitMapGenEvent fired");
		FeatureGeneration.onInitMapGen(e);
	}

	void configure() {
		TileEntityStargateController.configure(config);
		NaquadahOreWorldGen.configure(config);
		TileEntityStargateBase.configure(config);
		configValues.add(new ConfigValue<Boolean>("addOresToExistingWorlds", config.getBoolean("options",
				"addOresToExistingWorlds", false)));

		String version = new StringBuilder().append(BuildInfo.versionNumber).append(" build ")
				.append(BuildInfo.buildNumber).toString();
		Property prop = config.get("general", "currentVersion", 0);
		prop.comment = "Version cache - do not change this!";
		String previousVersion = prop.getString();

		prop.set(version);

		Property GenerateStruct = config.get("options", "GenerateStructures", true);
		configValues.add(new ConfigValue<Boolean>("doGenerateStructures", config.getBoolean("stargate",
				"GenerateStructures", true)));
		GenerateStruct.comment = "Enables/Disables generation of Gate Rooms under Desert Pyramids. (true/false)";

		Property GalacticraftCompat = config.get("options", "GalacticCraftCompat", false);
		configValues.add(new ConfigValue<Boolean>("doGalacticCraftCompat", config.getBoolean("stargate",
				"GalacticCraftCompat", true)));
		GalacticraftCompat.comment = "Enables/Disables Galcticraft support - this will change all addresses! (true/false)";

		Property textureRes = config.get("graphics_options", "textureRes", 32);
		configValues.add(new ConfigValue<Integer>("renderQuality", config.getInteger("graphics_options", "textureRes",
				32)));
		textureRes.comment = "Texture resolution setting. (32 / 64 / 128)";

		Property HDModels = config.get("graphics_options", "HDModels", true);
		configValues.add(new ConfigValue<Boolean>("renderUseModels", config.getBoolean("graphics_options", "HDModels",
				true)));
		HDModels.comment = "Should HD models be used. (true/false)";

		configValues.add(new ConfigValue<Boolean>("doGateExplosion", config.getBoolean("options",
				"ActiveGateExplosion", true)));

		Property enableAnalytics = config.get("options", "enableAnalytics", true);
		configValues.add(new ConfigValue<Boolean>("enableAnalytics", config.getBoolean("options", "enableAnalytics",
				true)));
		enableAnalytics.comment = "Submit anonymous usage statistic data. (true/false)";

		if (((ConfigValue<Boolean>) getConfigValue("doGalacticCraftCompat")).getValue())
			GateAddressHelper.minDimension = -99;

		if (version != previousVersion && enableAnalytics.getBoolean(true))
			analyticsHelper.start();
	}

	void registerOther() {
		if (((ConfigValue<Boolean>) getConfigValue("doGenerateStructures")).getValue()) {
			LanteaCraft.getLogger().log(Level.FINE, "Registering LanteaCraft structures...");
			MinecraftForge.TERRAIN_GEN_BUS.register(LanteaCraft.getInstance());
			try {
				if (new CallableMinecraftVersion(null).minecraftVersion().equals("1.6.4"))
					MapGenStructureIO.func_143031_a(FeatureUnderDesertPyramid.class, "LanteaCraft:DesertPyramid");
			} catch (Throwable e) {
				LanteaCraft.getLogger().log(Level.FINE, "Could not register structure type LanteaCraft:DesertPyramid",
						e);
			}
		}
	}

	void registerBlocks() {
		LanteaCraft.getLogger().log(Level.FINE, "Registering LanteaCraft blocks...");
		Blocks.stargateRingBlock = RegistrationHelper.registerBlock(BlockStargateRing.class, ItemStargateRing.class,
				"stargateRing");
		Blocks.stargateBaseBlock = RegistrationHelper.registerBlock(BlockStargateBase.class, "stargateBase");
		Blocks.stargateControllerBlock = RegistrationHelper.registerBlock(BlockStargateController.class,
				"stargateController");

		Blocks.naquadahBlock = RegistrationHelper.registerBlock(BlockNaquadah.class, "naquadahBlock");
		Blocks.naquadahOre = RegistrationHelper.registerBlock(BlockNaquadahOre.class, "naquadahOre");

		Blocks.naquadahGenerator = (BlockNaquadahGenerator) RegistrationHelper.registerBlock(
				BlockNaquadahGenerator.class, "naquadahGenerator");
	}

	void registerItems() {
		LanteaCraft.getLogger().log(Level.FINE, "Registering LanteaCraft items...");
		Items.naquadah = RegistrationHelper.registerItem(ItemNaquadah.class, "naquadah");
		Items.naquadahIngot = RegistrationHelper.registerItem(ItemNaquadahIngot.class, "naquadahIngot");

		Items.coreCrystal = RegistrationHelper.registerItem(ItemCoreCrystal.class, "coreCrystal");
		Items.controllerCrystal = RegistrationHelper.registerItem(ItemControllerCrystal.class, "controllerCrystal");
		Items.energyCrystal = RegistrationHelper.registerItem(ItemEnergyCrystal.class, "energyCrystal");

		Items.tokraSpawnEgg = RegistrationHelper.registerItem(ItemTokraSpawnEgg.class, "tokraSpawnEgg");
		Items.debugger = RegistrationHelper.registerItem(ItemDebugTool.class, "lanteadebug");
	}

	void registerOres() {
		LanteaCraft.getLogger().log(Level.FINE, "Registering LanteaCraft ores...");
		RegistrationHelper.registerOre("oreNaquadah", new ItemStack(Blocks.naquadahOre));
		RegistrationHelper.registerOre("naquadah", new ItemStack(Items.naquadah));
		RegistrationHelper.registerOre("ingotNaquadahAlloy", new ItemStack(Items.naquadahIngot));
	}

	void registerRecipes() {
		LanteaCraft.getLogger().log(Level.FINE, "Registering LanteaCraft recipes...");

		ItemStack chiselledSandstone = new ItemStack(Block.sandStone, 1, 1);
		ItemStack smoothSandstone = new ItemStack(Block.sandStone, 1, 2);
		ItemStack sgChevronBlock = new ItemStack(Blocks.stargateRingBlock, 1, 1);
		ItemStack blueDye = new ItemStack(Item.dyePowder, 1, 4);
		ItemStack orangeDye = new ItemStack(Item.dyePowder, 1, 14);

		if (config.getBoolean("options", "allowCraftingNaquadah", false))
			RegistrationHelper.newShapelessRecipe(new ItemStack(Items.naquadah, 1), Item.coal, Item.slimeBall,
					Item.blazePowder);
		RegistrationHelper.newRecipe(new ItemStack(Blocks.stargateRingBlock, 1), "ICI", "NNN", "III", 'I',
				Item.ingotIron, 'N', "ingotNaquadahAlloy", 'C', chiselledSandstone);
		RegistrationHelper.newRecipe(sgChevronBlock, "CgC", "NpN", "IrI", 'I', Item.ingotIron, 'N',
				"ingotNaquadahAlloy", 'C', chiselledSandstone, 'g', Item.glowstone, 'r', Item.redstone, 'p',
				Item.enderPearl);
		RegistrationHelper.newRecipe(new ItemStack(Blocks.stargateBaseBlock, 1), "CrC", "NeN", "IcI", 'I',
				Item.ingotIron, 'N', "ingotNaquadahAlloy", 'C', chiselledSandstone, 'r', Item.redstone, 'e',
				Item.eyeOfEnder, 'c', Items.coreCrystal);
		RegistrationHelper.newRecipe(new ItemStack(Blocks.stargateControllerBlock, 1), "bbb", "OpO", "OcO", 'b',
				Block.stoneButton, 'O', Block.obsidian, 'p', Item.enderPearl, 'r', Item.redstone, 'c',
				Items.controllerCrystal);
		RegistrationHelper.newShapelessRecipe(new ItemStack(Items.naquadahIngot, 1), "naquadah", Item.ingotIron);
		RegistrationHelper.newRecipe(new ItemStack(Blocks.naquadahBlock, 1), "NNN", "NNN", "NNN", 'N',
				"ingotNaquadahAlloy");
		RegistrationHelper.newRecipe(new ItemStack(Items.naquadahIngot, 9), "B", 'B', Blocks.naquadahBlock);
		if (config.getBoolean("options", "allowCraftingCrystals", false)) {
			RegistrationHelper.newRecipe(new ItemStack(Items.coreCrystal, 1), "bbr", "rdb", "brb", 'b', blueDye, 'r',
					Item.redstone, 'd', Item.diamond);
			RegistrationHelper.newRecipe(new ItemStack(Items.controllerCrystal, 1), "roo", "odr", "oor", 'o',
					orangeDye, 'r', Item.redstone, 'd', Item.diamond);
		}
	}

	void registerContainers() {
		LanteaCraft.getLogger().log(Level.FINE, "Registering LanteaCraft containers...");
		addContainer(LanteaCraft.EnumGUIs.StargateBase.ordinal(), ContainerStargateBase.class);
		addContainer(LanteaCraft.EnumGUIs.NaquadahGenerator.ordinal(), ContainerNaquadahGenerator.class);
	}

	void registerRandomItems() {
		LanteaCraft.getLogger().log(Level.FINE, "Registering LanteaCraft random drop items...");
		String[] categories = { ChestGenHooks.MINESHAFT_CORRIDOR, ChestGenHooks.PYRAMID_DESERT_CHEST,
				ChestGenHooks.PYRAMID_JUNGLE_CHEST, ChestGenHooks.STRONGHOLD_LIBRARY, ChestGenHooks.VILLAGE_BLACKSMITH };
		RegistrationHelper.addRandomChestItem(new ItemStack(Blocks.stargateBaseBlock), 1, 1, 2, categories);
		RegistrationHelper.addRandomChestItem(new ItemStack(Blocks.stargateControllerBlock), 1, 1, 1, categories);
		RegistrationHelper.addRandomChestItem(new ItemStack(Blocks.stargateRingBlock, 1, 0), 1, 3, 8, categories);
		RegistrationHelper.addRandomChestItem(new ItemStack(Blocks.stargateRingBlock, 1, 1), 1, 3, 7, categories);
		RegistrationHelper.addRandomChestItem(new ItemStack(Items.coreCrystal, 1, 0), 1, 1, 2, categories);
		RegistrationHelper.addRandomChestItem(new ItemStack(Items.controllerCrystal, 1, 0), 1, 1, 1, categories);
	}

	void registerWorldGenerators() {
		if (config.getBoolean("options", "enableNaquadahOre", true)) {
			LanteaCraft.getLogger().log(Level.FINE, "Registering LanteaCraft NaquadahOre generator...");
			naquadahOreGenerator = new NaquadahOreWorldGen();
			GameRegistry.registerWorldGenerator(naquadahOreGenerator);
		}
	}

	void registerVillagers() {
		LanteaCraft.getLogger().log(Level.FINE, "Registering LanteaCraft Tokra villagers...");
		tokraVillagerID = addVillager(config.getVillager("tokra"), "tokra",
				LanteaCraft.getResource("textures/skins/tokra.png"));
		RegistrationHelper.addTradeHandler(tokraVillagerID, new TradeHandler());
	}

	void registerTileEntities() {
		LanteaCraft.getLogger().log(Level.FINE, "Registering LanteaCraft tile entities...");
		GameRegistry.registerTileEntity(TileEntityStargateBase.class, "tileEntityStargateBase");
		GameRegistry.registerTileEntity(TileEntityStargateRing.class, "tileEntityStargateRing");
		GameRegistry.registerTileEntity(TileEntityStargateController.class, "tileEntityStargateController");
		GameRegistry.registerTileEntity(TileEntityNaquadahGenerator.class, "tileEntityNaquadahGenerator");
	}

	void registerFluids() {
		LanteaCraft.getLogger().log(Level.FINE, "Registering LanteaCraft fluids...");

		Fluids.fluidLiquidNaquadah = new LiquidNaquadah();
		FluidRegistry.registerFluid(Fluids.fluidLiquidNaquadah);
		Fluids.fluidLiquidNaquadahHost = RegistrationHelper.registerBlock(BlockLiquidNaquadah.class, ItemBlock.class,
				"blockLiquidNaquadah", false);
		Fluids.fluidLiquidNaquadahBucket = RegistrationHelper.registerSpecialBucket(Fluids.fluidLiquidNaquadahHost,
				"liquidNaquadahBucket");
	}

	public ConfigValue<?> getConfigValue(String name) {
		LanteaCraft.getLogger().log(Level.FINE, "Fetching configuration value `" + name + "`");
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
			LanteaCraft.getLogger().log(Level.SEVERE, "Failed to create GUI element, an exception occured.", e);
			return null;
		}
	}

	public int addVillager(int id, String name, ResourceLocation skin) {
		LanteaCraft.getLogger().log(Level.FINE, "Adding villager ID " + id + " with name " + name);
		VSBinding b = new VSBinding();
		b.id = id;
		b.object = skin;
		registeredVillagers.put(name, b);
		return id;
	}

	public void addContainer(int id, Class<? extends Container> cls) {
		LanteaCraft.getLogger().log(Level.FINE,
				"Registering container with ID " + id + ", class " + cls.getCanonicalName());
		registeredContainers.put(id, cls);
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

	public int getVillagerID(String name) {
		VSBinding villager = registeredVillagers.get(name);
		if (villager != null)
			return villager.id;
		return 0;
	}

	public void handlePacket(ModPacket modPacket, Player player) {
		if (modPacket.getPacketIsForServer())
			defaultServerPacketHandler.handlePacket(modPacket, player);
		else
			return;
	}

	public void sendToServer(ModPacket packet) {
		throw new RuntimeException("Cannot send to server: this method was not overridden!!");
	}

	public void sendToAllPlayers(ModPacket packet) {
		MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
		if (server != null) {
			LanteaCraft.getLogger().log(Level.INFO, "LanteaCraft sending packet to all players: " + packet.toString());
			Packet250CustomPayload payload = packet.toPacket();
			payload.channel = BuildInfo.modID;
			server.getConfigurationManager().sendPacketToAllPlayers(payload);
		}
	}

	public void sendToPlayer(EntityPlayer player, ModPacket packet) {
		Packet250CustomPayload payload = packet.toPacket();
		payload.channel = BuildInfo.modID;
		PacketDispatcher.sendPacketToPlayer(payload, (Player) player);
	}

	public ConfigurationHelper getConfig() {
		return config;
	}

	public ResourceLocation fetchResource(String resource) {
		if (!resourceCache.containsKey(resource))
			resourceCache.put(resource, new ResourceLocation(LanteaCraft.getAssetKey(), resource));
		return resourceCache.get(resource);
	}

}
