package pcl.lc.core;

import pcl.lc.base.TickHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientTickHandler extends TickHandler {

	public ClientTickHandler() {

	}

	@SubscribeEvent
	public void onClientTick(ClientTickEvent tick) {
		updateChildren();
		tickChildren();
	}
}
