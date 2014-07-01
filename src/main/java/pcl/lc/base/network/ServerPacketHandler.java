package pcl.lc.base.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import pcl.common.util.WorldLocation;
import pcl.lc.module.stargate.tile.TileStargateBase;

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
				if (tile instanceof TileStargateBase) {
					TileStargateBase base = (TileStargateBase) tile;
					base.getDescriptionPacket();
				}
			} else if (spacket.getType().equals("LanteaPacket.DialRequest")) {
				World w = DimensionManager.getWorld(target.dimension);
				TileEntity tile = w.getTileEntity(target.x, target.y, target.z);
				if (tile instanceof TileStargateBase) {
					String address = (String) spacket.getValue("Address");
					TileStargateBase base = (TileStargateBase) tile;
					base.connectOrDisconnect(address);
				}
			}
		}
	}

}
