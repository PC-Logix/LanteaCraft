package lc.server;

import lc.common.LCLog;
import lc.server.database.UniverseManager;
import net.minecraftforge.event.world.WorldEvent;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

/**
 * Server-side event hook implementation.
 * 
 * @author AfterLifeLochie
 *
 */
public class ServerEventHooks {

	/** The server universe manager global instance */
	private final UniverseManager universeMgr;

	/** Default constructor */
	public ServerEventHooks() {
		this.universeMgr = new UniverseManager();
	}

	public void onServerStarting(FMLServerStartingEvent event) {
		universeMgr.loadUniverse(event);
	}

	public void onServerStopping(FMLServerStoppingEvent event) {
		universeMgr.unloadUniverse(event);
	}

	@SubscribeEvent
	public void onWorldLoad(WorldEvent.Load event) {
		universeMgr.loadGalaxy(event);
	}

	@SubscribeEvent
	public void onWorldUnload(WorldEvent.Unload event) {
		universeMgr.unloadGalaxy(event);
	}

	@SubscribeEvent
	public void onWorldSave(WorldEvent.Save event) {
		universeMgr.autosaveGalaxy(event);
	}

}
