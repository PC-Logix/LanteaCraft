package pcl.lc.network;

import net.afterlifelochie.minecore.network.ModPacket;
import cpw.mods.fml.common.network.Player;

public interface ILanteaPacketHandler {

	public void handlePacket(ModPacket packet);

	public void dispatchUpdatePacket(Player target);

	public void requestUpdatePacket(Player target);

}
