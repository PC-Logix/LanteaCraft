package pcl.lc;

import java.io.File;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import pcl.common.base.TileEntityChunkManager;
import pcl.common.helpers.ConfigurationHelper;
import pcl.common.helpers.HelperCreativeTab;
import pcl.common.helpers.SpecialBucketHandler;
import pcl.common.network.ModPacket;
import pcl.common.render.RotationOrientedBlockRenderer;
import pcl.common.worldgen.ChunkData;
import pcl.lc.blocks.BlockNaquadah;
import pcl.lc.blocks.BlockNaquadahGenerator;
import pcl.lc.blocks.BlockNaquadahOre;
import pcl.lc.blocks.BlockPortal;
import pcl.lc.blocks.BlockStargateBase;
import pcl.lc.blocks.BlockStargateController;
import pcl.lc.blocks.BlockStargateRing;
import pcl.lc.containers.ContainerStargateBase;
import pcl.lc.core.GateAddressHelper;
import pcl.lc.fluids.BlockLiquidNaquadah;
import pcl.lc.fluids.ItemSpecialBucket;
import pcl.lc.fluids.LiquidNaquadah;
import pcl.lc.items.ItemDebugTool;
import pcl.lc.items.ItemStargateRing;
import pcl.lc.items.ItemTokraSpawnEgg;
import pcl.lc.render.blocks.BlockStargateBaseRenderer;
import pcl.lc.render.blocks.BlockStargateRingRenderer;
import pcl.lc.render.models.NaquadahGeneratorModel;
import pcl.lc.render.models.StargateControllerModel;
import pcl.lc.render.tileentity.TileEntityNaquadahGeneratorRenderer;
import pcl.lc.render.tileentity.TileEntityStargateBaseRenderer;
import pcl.lc.render.tileentity.TileEntityStargateControllerRenderer;
import pcl.lc.tileentity.TileEntityNaquadahGenerator;
import pcl.lc.tileentity.TileEntityStargateBase;
import pcl.lc.tileentity.TileEntityStargateController;
import pcl.lc.tileentity.TileEntityStargateRing;
import pcl.lc.worldgen.FeatureGeneration;
import pcl.lc.worldgen.FeatureUnderDesertPyramid;
import pcl.lc.worldgen.NaquadahOreWorldGen;
import pcl.lc.worldgen.TradeHandler;
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
		+ BuildInfo.buildNumber, dependencies = "after:ComputerCraft;after:BuildCraft|Core;after:IC2;after:SGCraft")
@NetworkMod(clientSideRequired = true, serverSideRequired = true, channels = { BuildInfo.modID },
		packetHandler = pcl.lc.network.DefaultPacketHandler.class)
public class LanteaCraft {

	/**
	 * The private instance of the mod. Use {@link #getInstance()} to access this object safely
	 */
	private static LanteaCraft mod;

	/**
	 * Returns the current instance singleton of the LanteaCraft mod object
	 * 
	 * @return The current, if any, instance of the LanteaCraft mod
	 */
	public static LanteaCraft getInstance() {
		return LanteaCraft.mod;
	}

	/**
	 * The private instance of the logger used. Use {@link #getLogger()} to access this object
	 * safely
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

		public static BlockNaquadahGenerator naquadahGenerator;
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
		public static NaquadahGeneratorModel modelNaquadahGenerator;

		public static RotationOrientedBlockRenderer blockOrientedRenderer;

		public static BlockStargateBaseRenderer blockBaseRenderer;
		public static BlockStargateRingRenderer blockRingRenderer;

		public static TileEntityStargateBaseRenderer tileEntityBaseRenderer;
		public static TileEntityStargateControllerRenderer tileEntityControllerRenderer;
		public static TileEntityNaquadahGeneratorRenderer tileEntityNaquadahGeneratorRenderer;
	}

	/**
	 * Public declaration of all fluids
	 */
	public static class Fluids {
		public static LiquidNaquadah fluidLiquidNaquadah;
		public static BlockLiquidNaquadah fluidLiquidNaquadahHost;
		public static ItemSpecialBucket fluidLiquidNaquadahBucket;
	}

	public static enum EnumGUIs {
		StargateBase, StargateController, NaquadahGenerator;
	}

	/**
	 * Creative tab instance
	 */
	private static HelperCreativeTab lanteaCraftTab = new HelperCreativeTab(CreativeTabs.getNextID(), "LanteaCraft") {
		@Override
		public ItemStack getIconItemStack() {
			return new ItemStack(LanteaCraft.Items.debugger);
		}
	};

	/**
	 * Sided proxy.
	 * 
	 * @see pcl.lc.LanteaCraftCommonProxy
	 * @see pcl.lc.LanteaCraftClientProxy
	 */
	@SidedProxy(clientSide = "pcl.lc.LanteaCraftClientProxy", serverSide = "pcl.lc.LanteaCraftCommonProxy")
	public static LanteaCraftCommonProxy proxy;

	/**
	 * ItemSpecialBucket bucket collection handler object - Forge eventbus only.
	 */
	private SpecialBucketHandler bucketHandler = new SpecialBucketHandler();

	/**
	 * Declaration of asset key.
	 */
	private String assetKey = "pcl_lc";

	public LanteaCraft() {
		LanteaCraft.mod = this;
	}

	/**
	 * Get a resource location from an abstract path
	 * 
	 * @param path
	 *            The path to use
	 * @return A fully qualified {@link ResourceLocation} to the resource
	 */
	public static ResourceLocation getResource(String path) {
		return new ResourceLocation(LanteaCraft.getInstance().assetKey, path);
	}

	/**
	 * Gets the current Proxy object for LanteaCraft
	 * 
	 * @return The current Proxy object for LanteaCraft
	 */
	public static LanteaCraftCommonProxy getProxy() {
		return LanteaCraft.getInstance().proxy;
	}

	/**
	 * Gets the currently used asset key for resources and other paths
	 * 
	 * @return The current asset key
	 */
	public static String getAssetKey() {
		return LanteaCraft.getInstance().assetKey;
	}

	/**
	 * Gets the current CreativeTabs creative-tab object
	 * 
	 * @return The current creative tab
	 */
	public static CreativeTabs getCreativeTab() {
		return LanteaCraft.getInstance().lanteaCraftTab;
	}

	/**
	 * Gets the current SpecialBucketHandler
	 * 
	 * @return The current SpecialBucketHandler
	 */
	public static SpecialBucketHandler getSpecialBucketHandler() {
		return LanteaCraft.getInstance().bucketHandler;
	}

	/**
	 * Handles an incoming {@link ModPacket} with respect to the provided {@link Player}.
	 * 
	 * @param modPacket
	 *            The packet object
	 * @param player
	 *            The respective player
	 */
	public static void handlePacket(ModPacket modPacket, Player player) {
		proxy.handlePacket(modPacket, player);
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
	public void onInitMapGen(InitMapGenEvent e) {
		proxy.onInitMapGen(e);
	}
}
