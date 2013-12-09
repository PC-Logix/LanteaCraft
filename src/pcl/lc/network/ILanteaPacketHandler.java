package pcl.lc.network;

import pcl.common.network.ModPacket;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public interface ILanteaPacketHandler {

	public void handlePacket(ModPacket packet);

	public void dispatchUpdatePacket(Player target);

	public void requestUpdatePacket(Player target);

}
