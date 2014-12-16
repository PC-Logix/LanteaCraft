package pcl.lc;

import net.minecraftforge.event.terraingen.InitMapGenEvent;
import net.minecraftforge.event.world.WorldEvent;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

import pcl.lc.base.network.PCLPacketPipeline;
import pcl.lc.util.SpecialBucketHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = BuildInfo.modID, name = BuildInfo.modName, version = BuildInfo.versionNumber + "-" + BuildInfo.buildNumber, dependencies = "after:ComputerCraft;after:OpenComputers;after:CoFHCore;after:IC2;after:SGCraft")
public class LanteaCraft {

	/**
	 * The private instance of the mod. Use {@link #getInstance()} to access
	 * this object safely
	 */
	private static LanteaCraft mod;

	/**
	 * Sided proxy.
	 * 
	 * @see pcl.lc.LanteaCraftCommonProxy
	 * @see pcl.lc.LanteaCraftClientProxy
	 */
	@SidedProxy(clientSide = "pcl.lc.LanteaCraftClientProxy", serverSide = "pcl.lc.LanteaCraftCommonProxy")
	public static LanteaCraftCommonProxy proxy;

	/**
	 * The private instance of the logger used. Use {@link #getLogger()} to
	 * access this object safely
	 */
	private static Logger logger;

	/**
	 * The network pipeline
	 */
	private static final PCLPacketPipeline pipeline = new PCLPacketPipeline();

	/**
	 * Returns the current instance singleton of the LanteaCraft mod object
	 * 
	 * @return The current, if any, instance of the LanteaCraft mod
	 */
	public static LanteaCraft getInstance() {
		return LanteaCraft.mod;
	}

	/**
	 * Gets the global logger.
	 * 
	 * @return The current global logger.
	 */
	public static Logger getLogger() {
		return LanteaCraft.logger;
	}

	/**
	 * Gets the global network pipeline.
	 * 
	 * @return The current network pipeline.
	 */
	public static PCLPacketPipeline getNetPipeline() {
		return LanteaCraft.pipeline;
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
	 * Gets the current SpecialBucketHandler
	 * 
	 * @return The current SpecialBucketHandler
	 */
	public static SpecialBucketHandler getSpecialBucketHandler() {
		return LanteaCraft.getInstance().bucketHandler;
	}

	public static enum EnumGUIs {
		StargateBase, StargateDHD, StargateDHDEnergy, NaquadahGenerator, CrystalInfuser;
	}

	/**
	 * ItemSpecialBucket bucket collection handler object - Forge eventbus only.
	 */
	private SpecialBucketHandler bucketHandler = new SpecialBucketHandler();

	public LanteaCraft() {
		LanteaCraft.mod = this;
	}

	@EventHandler
	public void preInit(FMLPreInitializationEvent e) {
		LanteaCraft.logger = e.getModLog();
		proxy.preInit(e);
	}

	@EventHandler
	public void init(FMLInitializationEvent e) {
		pipeline.init(BuildInfo.modID);
		proxy.init(e);
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent e) {
		proxy.postInit(e);
	}

	@SubscribeEvent
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

	@SubscribeEvent
	public void onWorldUnload(WorldEvent.Unload evt) {
	}

	@SubscribeEvent
	public void onWorldLoad(WorldEvent.Load evt) {
	}
}
