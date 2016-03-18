package lc.server;

import lc.LCRuntime;
import lc.common.base.generation.LCChunkData;
import lc.common.base.generation.LCWorldData;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.terraingen.InitMapGenEvent;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.event.world.WorldEvent;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLServerAboutToStartEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppedEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import cpw.mods.fml.common.gameevent.TickEvent.PlayerTickEvent;

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

	@Mod.EventHandler
	public void onServerStarting(FMLServerStartingEvent event) {
		serverHints.universeMgr.loadUniverse(event);
	}

	@Mod.EventHandler
	public void onServerStopping(FMLServerStoppingEvent event) {
		serverHints.universeMgr.unloadUniverse(event);
		serverHints.stargateMgr.closeAllConnections(true);
	}

	@SubscribeEvent
	public void onWorldLoad(WorldEvent.Load event) {
		LCWorldData.forWorld(event.world);
		serverHints.universeMgr.loadGalaxy(event);
	}

	@SubscribeEvent
	public void onWorldUnload(WorldEvent.Unload event) {
		LCWorldData.forWorld(event.world).markDirty();
		serverHints.universeMgr.unloadGalaxy(event);
		serverHints.stargateMgr.closeConnectionsIn(event.world.provider.dimensionId);
	}

	@SubscribeEvent
	public void onWorldSave(WorldEvent.Save event) {
		serverHints.universeMgr.autosaveGalaxy(event);
	}

	@SubscribeEvent
	public void onInitMapGen(InitMapGenEvent e) {
		serverHints.initMapGen(e);
	}

	@Mod.EventHandler
	public void onServerStopped(FMLServerStoppedEvent event) {
		LCRuntime.runtime.network().serverShutdown();
	}

	@Mod.EventHandler
	public void onServerStarted(FMLServerStartedEvent event) {
		// TODO Auto-generated method stub

	}

	@Mod.EventHandler
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

	@SubscribeEvent
	public void onChunkLoad(ChunkDataEvent.Load event) {
		LCChunkData.onChunkLoad(event);
	}

	@SubscribeEvent
	public void onChunkSave(ChunkDataEvent.Save event) {
		LCChunkData.onChunkSave(event);
	}

}
