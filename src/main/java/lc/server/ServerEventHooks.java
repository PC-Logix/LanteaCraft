package lc.server;

import lc.LCRuntime;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.event.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.common.event.FMLServerStartedEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;

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
		serverHints.stargateMgr.closeAllConnections(true);
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
		LCRuntime.runtime.network().serverShutdown();

	}

	public void onServerStarted(FMLServerStartedEvent event) {
		// TODO Auto-generated method stub

	}

	public void beforeServerStarted(FMLServerAboutToStartEvent event) {
		serverHints.trustChain.purge();
		// TODO: Load the keys from /config/LanteaCraft/trust/ into the chain
	}

	@SubscribeEvent
	public void onPlayerConnected(PlayerLoggedInEvent event) {
		LCRuntime.runtime.network().playerConnected((EntityPlayerMP) event.player);
	}

	@SubscribeEvent
	public void onPlayerDisconnected(PlayerLoggedOutEvent event) {
		LCRuntime.runtime.network().playerDisconnected((EntityPlayerMP) event.player);
	}

}
