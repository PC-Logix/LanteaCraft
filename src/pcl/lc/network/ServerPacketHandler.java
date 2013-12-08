package pcl.lc.network;

import pcl.lc.tileentity.TileEntityStargateBase;
import cpw.mods.fml.common.network.Player;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

public class ServerPacketHandler {

	public void handlePacket(LanteaPacket packet, Player player) {
		if (packet.getType().equals(LanteaPacket.PacketType.UpdateRequest)) {
			int worldName = (Integer) packet.getValue("DimensionID");
			int x = (Integer) packet.getValue("WorldX");
			int y = (Integer) packet.getValue("WorldY");
			int z = (Integer) packet.getValue("WorldZ");
			World w = DimensionManager.getWorld(worldName);
			TileEntity tile = w.getBlockTileEntity(x, y, z);
			if (tile instanceof TileEntityStargateBase) {
				TileEntityStargateBase base = (TileEntityStargateBase) tile;
				base.getDescriptionPacket();
			}
		}

		if (packet.getType().equals(LanteaPacket.PacketType.DialRequest)) {
			int worldName = (Integer) packet.getValue("DimensionID");
			int x = (Integer) packet.getValue("WorldX");
			int y = (Integer) packet.getValue("WorldY");
			int z = (Integer) packet.getValue("WorldZ");
			World w = DimensionManager.getWorld(worldName);
			TileEntity tile = w.getBlockTileEntity(x, y, z);
			if (tile instanceof TileEntityStargateBase) {
				String address = (String) packet.getValue("Address");
				TileEntityStargateBase base = (TileEntityStargateBase) tile;
				base.connectOrDisconnect(address, (EntityPlayer) player);
			}
		}
	}

}
