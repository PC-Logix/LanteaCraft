package lc.server;

import java.util.HashMap;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLFingerprintViolationEvent;
import cpw.mods.fml.common.event.FMLInterModComms.IMCEvent;
import cpw.mods.fml.common.event.FMLServerAboutToStartEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppedEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraftforge.common.MinecraftForge;
import lc.api.audio.ISoundController;
import lc.api.components.IConfigurationProvider;
import lc.api.defs.IContainerDefinition;
import lc.api.defs.IRecipeDefinition;
import lc.common.IHintProvider;
import lc.common.LCLog;
import lc.common.base.generation.LCMasterWorldGen;
import lc.common.crypto.KeyTrustRegistry;
import lc.common.util.BeaconStreamThread;
import lc.common.util.StatsProvider;
import lc.server.database.UniverseManager;
import lc.server.openal.VoidSoundController;

/**
 * Server-side hint provider implementation
 *
 * @author AfterLifeLochie
 *
 */
public class HintProviderServer implements IHintProvider {

	/** The server hook bus */
	final ServerEventHooks serverHookBus;

	/** The metadata beacon controller */
	final BeaconStreamThread beaconMgr;

	/** The server universe manager global instance */
	final UniverseManager universeMgr;

	/** The server Stargate manager */
	final StargateManager stargateMgr;
	
	/** The server key-trust chain */
	final KeyTrustRegistry trustChain;

	/** The master world generator service */
	LCMasterWorldGen worldGenerator;

	/** Default constructor */
	public HintProviderServer() {
		LCLog.debug("HintProviderServer providing server-side hints");
		this.serverHookBus = new ServerEventHooks(this);
		this.beaconMgr = new BeaconStreamThread(this);
		this.stargateMgr = new StargateManager(this);
		this.universeMgr = new UniverseManager();
		this.trustChain = new KeyTrustRegistry();
	}

	@Override
	public void preInit() {
		LCLog.debug("Providing base initialization helpers.");
		FMLCommonHandler.instance().bus().register(serverHookBus);
		MinecraftForge.EVENT_BUS.register(serverHookBus);
	}

	@Override
	public void init() {
		LCLog.debug("Providing base initialization.");
		worldGenerator = new LCMasterWorldGen();
		GameRegistry.registerWorldGenerator(worldGenerator, 0);
		MinecraftForge.TERRAIN_GEN_BUS.register(worldGenerator);
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

	public StargateManager stargates() {
		return stargateMgr;
	}

	@Override
	public ISoundController audio() {
		return VoidSoundController.controller;
	}

	@Override
	public IConfigurationProvider config() {
		return null;
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

}
