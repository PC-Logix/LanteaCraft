package pcl.lc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.LoadingCallback;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraftforge.event.terraingen.InitMapGenEvent;

import org.apache.logging.log4j.Level;

import pcl.lc.base.network.ClientPacketHandler;
import pcl.lc.base.network.PacketLogger;
import pcl.lc.base.network.ServerPacketHandler;
import pcl.lc.base.network.packet.ModPacket;
import pcl.lc.base.worldgen.NaquadahOreWorldGen;
import pcl.lc.cfg.ModuleList;
import pcl.lc.cfg.XMLParser;
import pcl.lc.cfg.XMLSaver;
import pcl.lc.client.audio.AudioEngine;
import pcl.lc.core.GUIHandler;
import pcl.lc.core.ModuleManager;
import pcl.lc.core.ModuleManager.Module;
import pcl.lc.core.RemoteChunkLoading;
import pcl.lc.core.ServerTickHandler;
import pcl.lc.core.WorldLog;
import pcl.lc.module.ModuleWorldGenerator;
import pcl.lc.module.compat.UpgradeHelper;
import pcl.lc.module.stargate.StargateConnectionManager;
import pcl.lc.util.AnalyticsHelper;
import pcl.lc.util.NetworkHelpers;
import pcl.lc.util.RegistrationHelper;
import pcl.lc.util.VersionHelper;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import cpw.mods.fml.common.network.NetworkRegistry;

public class LanteaCraftCommonProxy {

	protected File configFile;
	protected ModuleList moduleConfig;

	protected AnalyticsHelper analyticsHelper = new AnalyticsHelper(false, null);
	private VersionHelper versionHelper = new VersionHelper();

	protected ClientPacketHandler clientPacketHandler;
	protected PacketLogger clientPacketLogger;
	protected ServerPacketHandler serverPacketHandler;
	protected PacketLogger serverPacketLogger;
	protected GUIHandler guiHandler;

	private NetworkHelpers networkHelpers;
	private UpgradeHelper upgradeHelper;

	protected RemoteChunkLoading remoteChunkManager;
	protected StargateConnectionManager stargateConnectionManager;
	protected AudioEngine audioContext;

	protected WorldLog worldLogger;

	private ServerTickHandler serverTickHandler = new ServerTickHandler(versionHelper);
	public int tokraVillagerID;

	protected ModuleManager moduleManager;

	public LanteaCraftCommonProxy() {
		if (BuildInfo.NET_DEBUGGING)
			serverPacketLogger = new PacketLogger(new File("lc-network-server.dat"));
		serverPacketHandler = new ServerPacketHandler(serverPacketLogger);
		networkHelpers = new NetworkHelpers();
	}

	public void preInit(FMLPreInitializationEvent e) {
		if (BuildInfo.isDevelopmentEnvironment())
			LanteaCraft.getLogger().log(
					Level.WARN,
					"This doesn't appear to be an official build of LanteaCraft, or you are in a development context; "
							+ "please do not report bugs to us. We do not support external builds.");
		else
			LanteaCraft.getLogger().log(Level.INFO,
					"Hello there, I'm LanteaCraft " + BuildInfo.versionNumber + "-" + BuildInfo.getBuildNumber() + "!");

		try {
			File configDir = new File(e.getModConfigurationDirectory(), "LanteaCraft/");
			if (!configDir.exists())
				configDir.mkdir();
			configFile = new File(configDir, "config.xml");
			if (configFile.exists()) {
				XMLParser parser = new XMLParser();
				FileInputStream test = new FileInputStream(configFile);
				moduleConfig = parser.read(test);
			} else
				moduleConfig = new ModuleList();
		} catch (Throwable t) {
			throw new RuntimeException("Error configuring LanteaCraft!", t);
		}

		configure();
		moduleManager = new ModuleManager(moduleConfig);
		moduleManager.preInit(e);
	}

	public void init(FMLInitializationEvent e) {
		LanteaCraft.getLogger().log(Level.INFO, "LanteaCraft setting up...");
		MinecraftForge.EVENT_BUS.register(LanteaCraft.getInstance());
		MinecraftForge.EVENT_BUS.register(LanteaCraft.getSpecialBucketHandler());
		remoteChunkManager = new RemoteChunkLoading();
		stargateConnectionManager = new StargateConnectionManager();
		serverTickHandler.registerTickHost(remoteChunkManager);
		serverTickHandler.registerTickHost(stargateConnectionManager);
		audioContext = new AudioEngine();
		guiHandler = new GUIHandler();

		FMLCommonHandler.instance().bus().register(serverTickHandler);
		networkHelpers.init();
		moduleManager.init(e);

		ForgeChunkManager.setForcedChunkLoadingCallback(LanteaCraft.getInstance(), new LoadingCallback() {
			@Override
			public void ticketsLoaded(List<Ticket> tickets, World world) {
				Iterator<Ticket> iter = tickets.iterator();
				while (iter.hasNext())
					ForgeChunkManager.releaseTicket(iter.next());
			}
		});
	}

	public void postInit(FMLPostInitializationEvent e) {
		RegistrationHelper.flagLateRegistrationZone();
		if (moduleConfig.modified())
			try {
				XMLSaver saver = new XMLSaver();
				saver.save(moduleConfig, new FileOutputStream(configFile));
			} catch (Throwable t) {
				LanteaCraft.getLogger().log(Level.WARN, "Failed updating configuration!", t);
			}
		NetworkRegistry.INSTANCE.registerGuiHandler(LanteaCraft.getInstance(), guiHandler);
		LanteaCraft.getLogger().log(Level.INFO, "LanteaCraft done setting up!");

		LanteaCraft.getLogger().log(Level.INFO, "[COMPAT] LanteaCraft looking for other versions of SGCraft...");
		if (UpgradeHelper.detectSGCraftInstall()) {
			upgradeHelper = new UpgradeHelper();
			upgradeHelper.hookSGCraft();
		}
		LanteaCraft.getLogger().log(Level.INFO, "[COMPAT] LanteaCraft done looking for other versions.");
		moduleManager.postInit(e);
	}

	public NaquadahOreWorldGen getOreGenerator() {
		if (!Module.WORLDGEN.isLoaded())
			return null;
		return ((ModuleWorldGenerator) Module.WORLDGEN.moduleOf()).getNaquadahOreGenerator();
	}

	public VersionHelper getVersionHelper() {
		return versionHelper;
	}

	public AudioEngine getAudioEngine() {
		return audioContext;
	}

	public void onInitMapGen(InitMapGenEvent e) {
		LanteaCraft.getLogger().log(Level.DEBUG, "InitMapGenEvent fired");
	}

	void configure() {
		// TODO: From config!
		// if (version != previousVersion && enableAnalytics.getBoolean(true))
		// analyticsHelper.start();
		versionHelper.start();
	}

	public void handlePacket(ModPacket modPacket, EntityPlayer player) {
		if (modPacket.getPacketIsForServer())
			serverPacketHandler.handlePacket(modPacket, player);
		else
			return;
	}

	public void onServerStarting(FMLServerStartingEvent e) {
		if (worldLogger != null) {
			worldLogger.close();
			worldLogger = null;
		}
		try {
			File base = new File(new File(".").getCanonicalPath());
			File datadir = new File(base, "saves/" + e.getServer().getFolderName() + "/data").getAbsoluteFile();
			File logfile = new File(datadir, "LanteaCraft.log");
			worldLogger = new WorldLog(logfile);
			LanteaCraft.getLogger().log(Level.INFO, String.format("WorldLog starting: %s", logfile.toString()));
			worldLogger.open();
		} catch (IOException ioex) {
			LanteaCraft.getLogger().log(Level.WARN, "Failed to resolve paths for WorldLog.", ioex);
		}
	}

	public void onServerStopping(FMLServerStoppingEvent e) {
		if (worldLogger != null) {
			LanteaCraft.getLogger().log(Level.INFO, "WorldLog shutting down...");
			worldLogger.close();
			worldLogger = null;
		}
	}

	public WorldLog getWorldLog() {
		return worldLogger;
	}

	public RemoteChunkLoading getRemoteChunkManager() {
		return remoteChunkManager;
	}

	public StargateConnectionManager getConnectionManager() {
		return stargateConnectionManager;
	}

}
