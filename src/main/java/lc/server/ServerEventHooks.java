package lc.server;

import net.minecraftforge.event.world.WorldEvent;
import cpw.mods.fml.common.event.FMLServerAboutToStartEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppedEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

/**
 * Server-side event hook implementation.
 * 
 * @author AfterLifeLochie
 *
 */
public class ServerEventHooks {

	private final HintProviderServer serverHints;

	/**
	 * Default constructor
	 * 
	 * @param serverHints
	 *            The server hint provider
	 */
	public ServerEventHooks(HintProviderServer serverHints) {
		this.serverHints = serverHints;
	}

	public void onServerStarting(FMLServerStartingEvent event) {
		serverHints.universeMgr.loadUniverse(event);
	}

	public void onServerStopping(FMLServerStoppingEvent event) {
		serverHints.universeMgr.unloadUniverse(event);
		serverHints.stargateMgr.closeAllConnections();
	}

	@SubscribeEvent
	public void onWorldLoad(WorldEvent.Load event) {
		serverHints.universeMgr.loadGalaxy(event);
	}

	@SubscribeEvent
	public void onWorldUnload(WorldEvent.Unload event) {
		serverHints.universeMgr.unloadGalaxy(event);
		serverHints.stargateMgr.closeConnectionsIn(event.world.provider.dimensionId);
	}

	@SubscribeEvent
	public void onWorldSave(WorldEvent.Save event) {
		serverHints.universeMgr.autosaveGalaxy(event);
	}

	public void onServerStopped(FMLServerStoppedEvent event) {
		// TODO Auto-generated method stub

	}

	public void onServerStarted(FMLServerStartedEvent event) {
		// TODO Auto-generated method stub

	}

	public void beforeServerStarted(FMLServerAboutToStartEvent event) {
		// TODO Auto-generated method stub

	}

}
