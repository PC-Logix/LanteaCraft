package lc.server;

import java.util.HashMap;

import lc.LCRuntime;
import lc.LanteaCraft;
import lc.api.audio.ISoundController;
import lc.api.components.ComponentType;
import lc.api.components.DriverMap;
import lc.api.components.IConfigurationProvider;
import lc.api.defs.IContainerDefinition;
import lc.api.defs.IRecipeDefinition;
import lc.api.rendering.IParticleMachine;
import lc.common.IHintProvider;
import lc.common.LCLog;
import lc.common.base.generation.LCMasterWorldGen;
import lc.common.configuration.xml.ComponentConfig;
import lc.common.crypto.KeyTrustRegistry;
import lc.common.util.BeaconStreamThread;
import lc.common.util.StatsProvider;
import lc.server.database.UniverseManager;
import lc.server.openal.VoidSoundController;
import lc.server.world.LCChunkLoadCallback;
import lc.server.world.LCLoadedChunkManager;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLFingerprintViolationEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms.IMCEvent;
import net.minecraftforge.fml.common.event.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.common.event.FMLServerStartedEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * Server-side hint provider implementation
 *
 * @author AfterLifeLochie
 *
 */
public class HintProviderServer implements IHintProvider {

	/** The server hook bus */
	ServerEventHooks serverHookBus;

	/** The server chunk-loading callback */
	LCChunkLoadCallback chunkLoadCallback;

	/** The server chunk-loader manager */
	LCLoadedChunkManager chunkLoadManager;

	/** The metadata beacon controller */
	BeaconStreamThread beaconMgr;

	/** The server universe manager global instance */
	UniverseManager universeMgr;

	/** The server Stargate manager */
	StargateManager stargateMgr;

	/** The server key-trust chain */
	KeyTrustRegistry trustChain;

	/** The master world generator service */
	LCMasterWorldGen worldGenerator;

	ComponentConfig renderConfiguration;

	/** Default constructor */
	public HintProviderServer() {
		LCLog.debug("HintProviderServer providing server-side hints");
	}

	@Override
	public void preInit() {
		LCLog.debug("Providing base initialization helpers.");
		serverHookBus = new ServerEventHooks(this);
		beaconMgr = new BeaconStreamThread(this);
		stargateMgr = new StargateManager(this);
		universeMgr = new UniverseManager();
		trustChain = new KeyTrustRegistry();
		chunkLoadCallback = new LCChunkLoadCallback();
		chunkLoadManager = new LCLoadedChunkManager();
		renderConfiguration = LCRuntime.runtime.config().config(ComponentType.CLIENT);
		FMLCommonHandler.instance().bus().register(serverHookBus);
		MinecraftForge.EVENT_BUS.register(serverHookBus);
		ForgeChunkManager.setForcedChunkLoadingCallback(LanteaCraft.instance, chunkLoadCallback);
	}

	@Override
	public void init() {
		LCLog.debug("Providing base initialization.");
		worldGenerator = new LCMasterWorldGen();
		GameRegistry.registerWorldGenerator(worldGenerator, 0);
		MinecraftForge.TERRAIN_GEN_BUS.register(worldGenerator);
		DriverMap.trySpinUpAll();
	}

	@Override
	public void postInit() {
		HashMap<String, String> stats = new HashMap<String, String>();
		StatsProvider.generateStats(stats);
		beaconMgr.beacon(stats);
	}

	@Override
	public void provideHints(IContainerDefinition definition) {
		// TODO Auto-generated method stub

	}

	@Override
	public void provideHints(IRecipeDefinition definition) {
		// TODO Auto-generated method stub

	}

	@Override
	public void serverStarting(FMLServerStartingEvent event) {
		serverHookBus.onServerStarting(event);
	}

	@Override
	public void serverStopping(FMLServerStoppingEvent event) {
		serverHookBus.onServerStopping(event);
	}

	/**
	 * Gets the active Stargate manager
	 * 
	 * @return The active Stargate manager
	 */
	public StargateManager stargates() {
		return stargateMgr;
	}

	/**
	 * Gets the active Chunk-loader manager
	 *
	 * @return The active Chunk-loader manager
	 */
	public LCLoadedChunkManager chunkLoaders() {
		return chunkLoadManager;
	}

	@Override
	public ISoundController audio() {
		return VoidSoundController.controller;
	}

	@Override
	public IConfigurationProvider config() {
		return renderConfiguration;
	}

	/**
	 * Gets the current key trust chain
	 * 
	 * @return The key trust chain
	 */
	public KeyTrustRegistry getTrustChain() {
		return trustChain;
	}

	@Override
	public void signatureViolation(FMLFingerprintViolationEvent event) {
		// TODO Auto-generated method stub
	}

	@Override
	public void receiveIMC(IMCEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public void serverStopped(FMLServerStoppedEvent event) {
		serverHookBus.onServerStopped(event);
	}

	@Override
	public void serverStarted(FMLServerStartedEvent event) {
		serverHookBus.onServerStarted(event);
	}

	@Override
	public void beforeServerStarting(FMLServerAboutToStartEvent event) {
		serverHookBus.beforeServerStarted(event);
	}

	@Override
	public IParticleMachine particles() {
		throw new RuntimeException("Particles not permitted on server.");
	}

}
