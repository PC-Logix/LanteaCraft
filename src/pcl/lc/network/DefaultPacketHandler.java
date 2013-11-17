package pcl.lc.network;

import pcl.lc.LanteaCraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.Side;

public class DefaultPacketHandler implements IPacketHandler {

	@Override
	public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player) {
		try {
			SGCraftPacket packetAsPeripheralPacket = SGCraftPacket.parse(packet.data);
			LanteaCraft.handlePacket(packetAsPeripheralPacket, player);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
