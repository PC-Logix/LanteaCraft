package pcl.lc;

import java.io.File;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import pcl.lc.base.TileEntityChunkManager;
import pcl.lc.blocks.BlockNaquadah;
import pcl.lc.blocks.BlockNaquadahOre;
import pcl.lc.blocks.BlockPortal;
import pcl.lc.blocks.BlockStargateBase;
import pcl.lc.blocks.BlockStargateController;
import pcl.lc.blocks.BlockStargateRing;
import pcl.lc.config.ConfigurationHelper;
import pcl.lc.container.ContainerStargateBase;
import pcl.lc.core.EnumGuiList;
import pcl.lc.core.GateAddressHelper;
import pcl.lc.generators.ChunkData;
import pcl.lc.generators.FeatureGeneration;
import pcl.lc.generators.FeatureUnderDesertPyramid;
import pcl.lc.generators.NaquadahOreWorldGen;
import pcl.lc.generators.TradeHandler;
import pcl.lc.items.ItemDebugTool;
import pcl.lc.items.ItemStargateRing;
import pcl.lc.items.ItemTokraSpawnEgg;
import pcl.lc.network.SGCraftPacket;
import pcl.lc.render.RotationOrientedBlockRenderer;
import pcl.lc.render.blocks.BlockStargateBaseRenderer;
import pcl.lc.render.blocks.BlockStargateRingRenderer;
import pcl.lc.render.model.StargateControllerModel;
import pcl.lc.render.tileentity.TileEntityPegasusStargateBaseRenderer;
import pcl.lc.render.tileentity.TileEntityStargateBaseRenderer;
import pcl.lc.render.tileentity.TileEntityStargateControllerRenderer;
import pcl.lc.tileentity.TileEntityStargateBase;
import pcl.lc.tileentity.TileEntityStargateController;
import pcl.lc.tileentity.TileEntityStargateRing;
import pcl.lc.util.HelperCreativeTab;
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
import net.minecraftforge.event.world.WorldEvent;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

@Mod(modid = BuildInfo.modID, name = BuildInfo.modName, version = BuildInfo.versionNumber + "build"
		+ BuildInfo.buildNumber)
@NetworkMod(clientSideRequired = true, serverSideRequired = true, channels = { BuildInfo.modID }, packetHandler = pcl.lc.network.DefaultPacketHandler.class)
public class LanteaCraft {

	/**
	 * The private instance of the mod. Use {@link #getInstance()} to access
	 * this object safely
	 */
	private static LanteaCraft mod;

	/**
	 * Returns the current instance singleton of the SGCraft mod object
	 * 
	 * @return The current, if any, instance of the SGCraft mod
	 */
	public static LanteaCraft getInstance() {
		return LanteaCraft.mod;
	}

	/**
	 * The private instance of the logger used. Use {@link #getLogger()} to
	 * access this object safely
	 */
	private static Logger logger;

	public static Logger getLogger() {
		return LanteaCraft.logger;
	}

	/**
	 * Public declaration of all Block objects
	 */
	public static class Blocks {
		public static BlockStargateBase sgBaseBlock;
		public static BlockStargateRing sgRingBlock;
		public static BlockStargateController sgControllerBlock;

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

		public static ItemTokraSpawnEgg tokraSpawnEgg;

		public static ItemDebugTool debugger;
	}

	/**
	 * Public declaration of all render objects
	 */
	public static class Render {
		public static StargateControllerModel modelController;

		public static RotationOrientedBlockRenderer blockOrientedRenderer;

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

	@SidedProxy(clientSide = "pcl.lc.SGCraftClientProxy", serverSide = "pcl.lc.SGCraftCommonProxy")
	public static LanteaCraftCommonProxy proxy;

	private String assetKey = "gcewing_sg";

	public LanteaCraft() {
		LanteaCraft.mod = this;
	}

	@EventHandler
	public void preInit(FMLPreInitializationEvent e) {
		LanteaCraft.logger = e.getModLog();
		LanteaCraft.logger.setParent(FMLLog.getLogger());
		if (BuildInfo.buildNumber.equals("@" + "BUILD" + "@")) {
			LanteaCraft.logger.setLevel(Level.ALL);
			LanteaCraft.logger.log(Level.INFO,
					"You appear to be inside a development environment, switching to all logging.");
		} else
			LanteaCraft.logger.setLevel(Level.INFO);
		proxy.preInit(e);
	}

	@EventHandler
	public void init(FMLInitializationEvent e) {
		proxy.init(e);
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent e) {
		proxy.postInit(e);
	}

	@ForgeSubscribe
	public void onChunkLoad(ChunkDataEvent.Load e) {
		proxy.onChunkLoad(e);
	}

	@ForgeSubscribe
	public void onChunkSave(ChunkDataEvent.Save e) {
		proxy.onChunkSave(e);
	}

	@ForgeSubscribe
	public void onInitMapGen(InitMapGenEvent e) {
		proxy.onInitMapGen(e);
	}

	@ForgeSubscribe
	public void onWorldLoad(WorldEvent.Load e) {
		proxy.onWorldLoad(e);
	}

	@ForgeSubscribe
	public void onWorldUnload(WorldEvent.Unload e) {
		proxy.onWorldUnload(e);
	}

	@ForgeSubscribe
	public void onWorldSave(WorldEvent.Save e) {
		proxy.onWorldSave(e);
	}

	public static ResourceLocation getResource(String path) {
		return new ResourceLocation(LanteaCraft.getInstance().assetKey, path);
	}

	public static LanteaCraftCommonProxy getProxy() {
		return LanteaCraft.getInstance().proxy;
	}

	public static String getAssetKey() {
		return LanteaCraft.getInstance().assetKey;
	}

	public static CreativeTabs getCreativeTab() {
		return LanteaCraft.getInstance().sgCraftTab;
	}

	public static void handlePacket(SGCraftPacket packet, Player player) {
		proxy.handlePacket(packet, player);
	}

}
