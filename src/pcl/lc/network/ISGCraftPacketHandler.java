package pcl.lc.network;

import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public interface ISGCraftPacketHandler {

	public void handlePacket(SGCraftPacket packet);

	public void dispatchUpdatePacket(Player target);

	public void requestUpdatePacket(Player target);

}
