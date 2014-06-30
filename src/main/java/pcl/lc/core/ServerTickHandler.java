package pcl.lc.core;

import net.minecraft.util.ChatComponentText;
import pcl.common.helpers.VersionHelper;
import pcl.lc.base.TickHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.PlayerTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ServerTickEvent;

public class ServerTickHandler extends TickHandler {

	private boolean messageSent;
	private VersionHelper versioning;

	public ServerTickHandler(VersionHelper version) {
		versioning = version;
	}

	@SubscribeEvent
	public void onWorldTick(ServerTickEvent tick) {
		if (tick.phase != Phase.START)
			return;
		updateChildren();
		tickChildren();
	}

	@SubscribeEvent
	public void onPlayerTick(PlayerTickEvent tick) {
		if (versioning.finished && !messageSent) {
			if (versioning.requiresNotify)
				tick.player.addChatMessage(new ChatComponentText("LanteaCraft " + versioning.remoteVersion + "-"
						+ versioning.remoteBuild + " is available: " + versioning.remoteLabel));
			messageSent = true;
		}
	}
}
