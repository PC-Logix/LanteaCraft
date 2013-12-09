package pcl.lc.network;

import pcl.common.network.ModPacket;
import pcl.lc.tileentity.TileEntityStargateBase;
import cpw.mods.fml.common.network.Player;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

public class ServerPacketHandler {

	public void handlePacket(ModPacket modPacket, Player player) {
		if (modPacket.getType().equals("LanteaPacket.UpdateRequest")) {
			int worldName = (Integer) modPacket.getValue("DimensionID");
			int x = (Integer) modPacket.getValue("WorldX");
			int y = (Integer) modPacket.getValue("WorldY");
			int z = (Integer) modPacket.getValue("WorldZ");
			World w = DimensionManager.getWorld(worldName);
			TileEntity tile = w.getBlockTileEntity(x, y, z);
			if (tile instanceof TileEntityStargateBase) {
				TileEntityStargateBase base = (TileEntityStargateBase) tile;
				base.getDescriptionPacket();
			}
		}

		if (modPacket.getType().equals("LanteaPacket.DialRequest")) {
			int worldName = (Integer) modPacket.getValue("DimensionID");
			int x = (Integer) modPacket.getValue("WorldX");
			int y = (Integer) modPacket.getValue("WorldY");
			int z = (Integer) modPacket.getValue("WorldZ");
			World w = DimensionManager.getWorld(worldName);
			TileEntity tile = w.getBlockTileEntity(x, y, z);
			if (tile instanceof TileEntityStargateBase) {
				String address = (String) modPacket.getValue("Address");
				TileEntityStargateBase base = (TileEntityStargateBase) tile;
				base.connectOrDisconnect(address, (EntityPlayer) player);
			}
		}
	}

}
