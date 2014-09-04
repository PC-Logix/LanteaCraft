package lc.common.network;

import net.minecraft.entity.player.EntityPlayer;

/**
 * Contract interface for classes which can handle {@link LCPacket} packets at
 * runtime.
 * 
 * @author AfterLifeLochie
 * 
 */
public interface IPacketHandler {

	public void handlePacket(LCPacket packetOf, EntityPlayer player) throws LCNetworkException;

}
