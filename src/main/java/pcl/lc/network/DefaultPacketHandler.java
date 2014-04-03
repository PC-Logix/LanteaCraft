package pcl.lc.network;

import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import pcl.common.network.ModPacket;
import pcl.lc.LanteaCraft;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;

public class DefaultPacketHandler implements IPacketHandler {
	
	public static final boolean NET_DEBUGGING = false;

	@Override
	public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player) {
		try {
			LanteaCraft.getProxy().handlePacket(ModPacket.parse(packet.data), player);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
