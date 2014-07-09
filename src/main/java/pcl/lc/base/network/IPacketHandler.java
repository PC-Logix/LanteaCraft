package pcl.lc.base.network;

import pcl.lc.base.network.packet.ModPacket;

/**
 * Handles an incoming packet for which the destination is this location
 * (TileEntity).
 * 
 * @author AfterLifeLochie
 * 
 */
public interface IPacketHandler {

	public void handlePacket(ModPacket packetOf);

}
