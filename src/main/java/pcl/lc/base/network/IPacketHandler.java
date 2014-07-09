package pcl.lc.base.network;

import net.minecraft.entity.player.EntityPlayer;
import pcl.lc.base.network.packet.ModPacket;

/**
 * Handles an incoming packet for which the destination is this location
 * (TileEntity).
 * 
 * @author AfterLifeLochie
 * 
 */
public interface IPacketHandler {

	public void handlePacket(ModPacket packetOf, EntityPlayer player);

}
