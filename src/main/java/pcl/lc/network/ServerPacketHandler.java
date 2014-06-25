package pcl.lc.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import pcl.common.network.ModPacket;
import pcl.common.network.StandardModPacket;
import pcl.common.util.WorldLocation;
import pcl.lc.tileentity.TileEntityStargateBase;

public class ServerPacketHandler {

	private PacketLogger logger;

	public ServerPacketHandler(PacketLogger logger) {
		this.logger = logger;
	}

	public void handlePacket(ModPacket modPacket, EntityPlayer player) {
		if (logger != null)
			logger.logPacket(modPacket);
		WorldLocation target = modPacket.getOriginLocation();
		if (modPacket instanceof StandardModPacket) {
			StandardModPacket spacket = (StandardModPacket) modPacket;
			if (spacket.getType().equals("LanteaPacket.UpdateRequest")) {
				World w = DimensionManager.getWorld(target.dimension);
				TileEntity tile = w.getTileEntity(target.x, target.y, target.z);
				if (tile instanceof TileEntityStargateBase) {
					TileEntityStargateBase base = (TileEntityStargateBase) tile;
					base.getDescriptionPacket();
				}
			} else if (spacket.getType().equals("LanteaPacket.DialRequest")) {
				World w = DimensionManager.getWorld(target.dimension);
				TileEntity tile = w.getTileEntity(target.x, target.y, target.z);
				if (tile instanceof TileEntityStargateBase) {
					String address = (String) spacket.getValue("Address");
					TileEntityStargateBase base = (TileEntityStargateBase) tile;
					base.connectOrDisconnect(address);
				}
			}
		}
	}

}
