package pcl.lc.network;

import net.afterlifelochie.minecore.network.ModPacket;
import net.afterlifelochie.minecore.network.StandardModPacket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import pcl.lc.tileentity.TileEntityStargateBase;
import cpw.mods.fml.common.network.Player;

public class ServerPacketHandler {

	public void handlePacket(ModPacket modPacket, Player player) {
		if (modPacket instanceof StandardModPacket) {
			StandardModPacket spacket = (StandardModPacket) modPacket;
			if (spacket.getType().equals("LanteaPacket.UpdateRequest")) {
				int worldName = (Integer) spacket.getValue("DimensionID");
				int x = (Integer) spacket.getValue("WorldX");
				int y = (Integer) spacket.getValue("WorldY");
				int z = (Integer) spacket.getValue("WorldZ");
				World w = DimensionManager.getWorld(worldName);
				TileEntity tile = w.getBlockTileEntity(x, y, z);
				if (tile instanceof TileEntityStargateBase) {
					TileEntityStargateBase base = (TileEntityStargateBase) tile;
					base.getDescriptionPacket();
				}
			}

			if (spacket.getType().equals("LanteaPacket.DialRequest")) {
				int worldName = (Integer) spacket.getValue("DimensionID");
				int x = (Integer) spacket.getValue("WorldX");
				int y = (Integer) spacket.getValue("WorldY");
				int z = (Integer) spacket.getValue("WorldZ");
				World w = DimensionManager.getWorld(worldName);
				TileEntity tile = w.getBlockTileEntity(x, y, z);
				if (tile instanceof TileEntityStargateBase) {
					String address = (String) spacket.getValue("Address");
					TileEntityStargateBase base = (TileEntityStargateBase) tile;
					base.connectOrDisconnect(address, (EntityPlayer) player);
				}
			}
		}
	}

}
