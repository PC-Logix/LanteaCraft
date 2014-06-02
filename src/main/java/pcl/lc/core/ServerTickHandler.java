package pcl.lc.core;

import net.minecraft.util.ChatComponentText;
import pcl.common.base.TickHandler;
import pcl.common.helpers.VersionHelper;
import pcl.lc.LanteaCraft;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.PlayerTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.WorldTickEvent;

public class ServerTickHandler extends TickHandler {

	private boolean messageSent;
	private VersionHelper versioning;

	public ServerTickHandler() {
		versioning = LanteaCraft.getProxy().getVersionHelper();
	}

	@SubscribeEvent
	public void onWorldTick(WorldTickEvent tick) {
		updateChildren();
		tickChildren();
	}

	@SubscribeEvent
	public void onPlayerTick(PlayerTickEvent tick) {
		if (versioning.finished && !messageSent) {
			if (versioning.requiresNotify) {
				tick.player.addChatMessage(new ChatComponentText("LanteaCraft " + versioning.remoteVersion + "-"
						+ versioning.remoteBuild + " is available: " + versioning.remoteLabel));
			}
			messageSent = true;
		}
	}
}
