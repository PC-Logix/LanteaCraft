package lc.server;

import net.minecraftforge.event.world.WorldEvent;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class ServerEventHooks {

	@EventHandler
	public void onServerStarting(FMLServerStartingEvent event) {
	}

	@EventHandler
	public void onServerStopping(FMLServerStoppingEvent event) {
	}

	@SubscribeEvent
	public void onWorldLoad(WorldEvent.Load event) {
	}

	@SubscribeEvent
	public void onWorldUnload(WorldEvent.Unload event) {
	}

	@SubscribeEvent
	public void onWorldSave(WorldEvent.Save event) {
	}

}
