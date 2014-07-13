package pcl.lc.base.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import pcl.lc.base.network.packet.ModPacket;
import pcl.lc.base.network.packet.StandardModPacket;
import pcl.lc.module.stargate.tile.TileStargateBase;
import pcl.lc.util.WorldLocation;

public class ServerPacketHandler {

	private PacketLogger logger;

	public ServerPacketHandler(PacketLogger logger) {
		this.logger = logger;
	}

	public void handlePacket(ModPacket modPacket, EntityPlayer player) {
		if (logger != null)
			logger.logPacket(modPacket);
		WorldLocation target = modPacket.getOriginLocation();
		World w = DimensionManager.getWorld(target.dimension);
		if (modPacket instanceof StandardModPacket) {
			StandardModPacket spacket = (StandardModPacket) modPacket;
			if (spacket.getType().equals("LanteaPacket.UpdateRequest")) {
				TileEntity tile = w.getTileEntity(target.x, target.y, target.z);
				if (tile instanceof TileStargateBase) {
					TileStargateBase base = (TileStargateBase) tile;
					base.getDescriptionPacket();
				}
			} else if (spacket.getType().equals("LanteaPacket.DialRequest")) {
				TileEntity tile = w.getTileEntity(target.x, target.y, target.z);
				if (tile instanceof TileStargateBase) {
					String address = (String) spacket.getValue("Address");
					TileStargateBase base = (TileStargateBase) tile;
					base.connectOrDisconnect(address);
				}
			}
		}
		
		TileEntity tile = w.getTileEntity(target.x, target.y, target.z);
		if (tile instanceof IPacketHandler) {
			IPacketHandler handler = (IPacketHandler) tile;
			handler.handlePacket(modPacket, player);
		}
	}

}
