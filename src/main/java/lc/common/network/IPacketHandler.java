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

	/**
	 * Handle an incoming packet from the server or client.
	 *
	 * @param packetOf
	 *            The network packet object.
	 * @param player
	 *            The player object.
	 * @throws LCNetworkException
	 *             If the packet cannot be handled or a problem occurs which
	 *             prevents the packet from being handled, a
	 *             {@link LCNetworkException} should be thrown.
	 */
	public void handlePacket(LCPacket packetOf, EntityPlayer player) throws LCNetworkException;

}
