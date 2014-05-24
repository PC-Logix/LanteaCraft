package pcl.lc;

import java.util.logging.Level;
import java.util.logging.Logger;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.terraingen.InitMapGenEvent;
import net.minecraftforge.event.world.WorldEvent;
import pcl.common.helpers.CreativeTabHelper;
import pcl.common.helpers.SpecialBucketHandler;
import pcl.common.render.RotationOrientedBlockRenderer;
import pcl.lc.blocks.BlockLanteaOre;
import pcl.lc.blocks.BlockNaquadahGenerator;
import pcl.lc.blocks.BlockOfLanteaOre;
import pcl.lc.blocks.BlockRingPlatform;
import pcl.lc.blocks.BlockStargateBase;
import pcl.lc.blocks.BlockStargateController;
import pcl.lc.blocks.BlockStargateRing;
import pcl.lc.entity.EntityReplicator;
import pcl.lc.entity.EntityTokra;
import pcl.lc.fluids.BlockLiquidNaquadah;
import pcl.lc.fluids.ItemSpecialBucket;
import pcl.lc.fluids.LiquidNaquadah;
import pcl.lc.items.ItemControllerCrystal;
import pcl.lc.items.ItemCoreCrystal;
import pcl.lc.items.ItemDebugTool;
import pcl.lc.items.ItemEnergyCrystal;
import pcl.lc.items.ItemIris;
import pcl.lc.items.ItemIrisController;
import pcl.lc.items.ItemLanteaOre;
import pcl.lc.items.ItemLanteaOreIngot;
import pcl.lc.items.ItemTokraSpawnEgg;
import pcl.lc.items.ItemTransportRingActivator;
import pcl.lc.items.ItemZPM;
import pcl.lc.render.blocks.BlockNaquadahGeneratorRenderer;
import pcl.lc.render.blocks.BlockStargateBaseRenderer;
import pcl.lc.render.blocks.BlockStargateControllerRenderer;
import pcl.lc.render.blocks.BlockStargateRingRenderer;
import pcl.lc.render.blocks.BlockVoidRenderer;
import pcl.lc.render.entities.EntityReplicatorRenderer;
import pcl.lc.render.entities.EntityTokraRenderer;
import pcl.lc.render.models.NaquadahGeneratorModel;
import pcl.lc.render.models.RingPlatformBaseModel;
import pcl.lc.render.models.RingPlatformRingModel;
import pcl.lc.render.models.StargateControllerModel;
import pcl.lc.render.tileentity.TileEntityLanteaDecorGlassRenderer;
import pcl.lc.render.tileentity.TileEntityNaquadahGeneratorRenderer;
import pcl.lc.render.tileentity.TileEntityRingPlatformRenderer;
import pcl.lc.render.tileentity.TileEntityStargateBaseRenderer;
import pcl.lc.render.tileentity.TileEntityStargateControllerRenderer;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import cpw.mods.fml.common.network.NetworkMod;

@Mod(modid = BuildInfo.modID, name = BuildInfo.modName, version = BuildInfo.versionNumber + "-" + BuildInfo.buildNumber, dependencies = "after:ComputerCraft;after:OpenComputers;after:BuildCraft|Core;after:IC2;after:SGCraft")
@NetworkMod(clientSideRequired = true, serverSideRequired = true, channels = { BuildInfo.modID }, packetHandler = pcl.lc.network.DefaultPacketHandler.class)
public class LanteaCraft {

	/**
	 * The private instance of the mod. Use {@link #getInstance()} to access
	 * this object safely
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
		public static BlockStargateBase stargateBaseBlock;
		public static BlockStargateRing stargateRingBlock;
		public static BlockStargateController stargateControllerBlock;

		public static BlockLanteaOre lanteaOre;
		public static BlockOfLanteaOre lanteaOreAsBlock;

		public static BlockRingPlatform ringPlatform;
		public static BlockNaquadahGenerator naquadahGenerator;
	}

	/**
	 * Public declaration of all Item objects
	 */
	public static class Items {
		public static ItemLanteaOre lanteaOreItem;
		public static ItemLanteaOreIngot lanteaOreIngot;

		public static ItemCoreCrystal coreCrystal;
		public static ItemControllerCrystal controllerCrystal;

		public static ItemEnergyCrystal energyCrystal;
		public static ItemZPM zpm;

		public static ItemIris iris;
		public static ItemIrisController irisController;

		public static ItemTransportRingActivator transportRingActivator;

		public static ItemTokraSpawnEgg tokraSpawnEgg;

		public static ItemDebugTool debugger;
	}

	/**
	 * Public declaration of all render objects
	 */
	public static class Render {
		public static StargateControllerModel modelController;
		public static NaquadahGeneratorModel modelNaquadahGenerator;
		public static RingPlatformBaseModel modelRingPlatformBase;
		public static RingPlatformRingModel modelRingPlatformRing;

		public static RotationOrientedBlockRenderer blockOrientedRenderer;
		public static BlockVoidRenderer blockVoidRenderer;

		public static BlockStargateBaseRenderer blockStargateBaseRenderer;
		public static BlockStargateRingRenderer blockStargateRingRenderer;
		public static BlockStargateControllerRenderer blockControllerRenderer;
		public static BlockNaquadahGeneratorRenderer blockNaquadahGeneratorRenderer;

		public static TileEntityStargateBaseRenderer tileEntityBaseRenderer;
		public static TileEntityStargateControllerRenderer tileEntityControllerRenderer;
		public static TileEntityNaquadahGeneratorRenderer tileEntityNaquadahGeneratorRenderer;
		public static TileEntityRingPlatformRenderer tileEntityRingPlatformRenderer;
		public static TileEntityLanteaDecorGlassRenderer tileEntityLanteaDecorGlassRenderer;

		public static EntityTokraRenderer entityTokraRenderer;
		public static EntityReplicatorRenderer entityReplicatorRenderer;
	}

	/**
	 * Public declaration of all fluids
	 */
	public static class Fluids {
		public static LiquidNaquadah fluidLiquidNaquadah;
		public static BlockLiquidNaquadah fluidLiquidNaquadahHost;
		public static ItemSpecialBucket fluidLiquidNaquadahBucket;
	}

	/**
	 * Public declaration of all entities
	 */
	public static class Entities {
		public static EntityTokra entityTokra;
		public static EntityReplicator entityReplicator;
	}

	public static enum EnumGUIs {
		StargateBase, StargateController, StargateControllerEnergy, NaquadahGenerator;
	}

	/**
	 * Creative tab instance
	 */
	private static CreativeTabHelper lanteaCraftTab = new CreativeTabHelper(CreativeTabs.getNextID(), "LanteaCraft") {
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
		return getProxy().fetchResource(path);
	}

	/**
	 * Gets the current Proxy object for LanteaCraft
	 * 
	 * @return The current Proxy object for LanteaCraft
	 */
	public static LanteaCraftCommonProxy getProxy() {
		return LanteaCraft.proxy;
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
		LanteaCraft.getInstance();
		return LanteaCraft.lanteaCraftTab;
	}

	/**
	 * Gets the current SpecialBucketHandler
	 * 
	 * @return The current SpecialBucketHandler
	 */
	public static SpecialBucketHandler getSpecialBucketHandler() {
		return LanteaCraft.getInstance().bucketHandler;
	}

	@EventHandler
	public void preInit(FMLPreInitializationEvent e) {
		LanteaCraft.logger = e.getModLog();
		LanteaCraft.logger.setParent(FMLLog.getLogger());
		LanteaCraft.logger.setLevel(Level.INFO);
		if (BuildInfo.isDevelopmentEnvironment())
			LanteaCraft.logger.setLevel(Level.ALL);
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

	@EventHandler
	public void onServerStarting(FMLServerStartingEvent e) {
		proxy.onServerStarting(e);
	}

	@EventHandler
	public void onServerStopping(FMLServerStoppingEvent e) {
		proxy.onServerStopping(e);
	}

	@ForgeSubscribe
	public void onWorldUnload(WorldEvent.Unload evt) {
		LanteaCraft.getLogger().log(Level.INFO, String.format("World unloading: %s", evt.world.provider.dimensionId));
	}

	@ForgeSubscribe
	public void onWorldLoad(WorldEvent.Load evt) {
		LanteaCraft.getLogger().log(Level.INFO, String.format("World loading: %s", evt.world.provider.dimensionId));
	}
}
