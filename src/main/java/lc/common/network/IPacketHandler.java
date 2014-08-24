package lc.common.network;

import net.minecraft.entity.player.EntityPlayer;

/**
 * Handles an incoming packet for which the destination is this location
 * (TileEntity).
 * 
 * @author AfterLifeLochie
 * 
 */
public interface IPacketHandler {

	public void handlePacket(LCPacket packetOf, EntityPlayer player);

}
