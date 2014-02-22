package pcl.lc.network;

import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import pcl.common.network.ModPacket;
import pcl.common.network.StandardModPacket;
import pcl.lc.tileentity.TileEntityNaquadahGenerator;
import pcl.lc.tileentity.TileEntityRingPlatform;
import pcl.lc.tileentity.TileEntityStargateBase;
import cpw.mods.fml.common.network.Player;

public class ClientPacketHandler {

	public void handlePacket(ModPacket packet, Player player) {
		if (packet instanceof StandardModPacket) {
			StandardModPacket spacket = (StandardModPacket) packet;
			if (spacket.getType().equals("LanteaPacket.TileUpdate")) {
				int worldName = (Integer) spacket.getValue("DimensionID");
				int currentWorld = Minecraft.getMinecraft().theWorld.provider.dimensionId;
				if (worldName == currentWorld) {
					int x = (Integer) spacket.getValue("WorldX");
					int y = (Integer) spacket.getValue("WorldY");
					int z = (Integer) spacket.getValue("WorldZ");
					World w = Minecraft.getMinecraft().theWorld;
					TileEntity tile = w.getBlockTileEntity(x, y, z);

					if (tile instanceof TileEntityStargateBase) {
						TileEntityStargateBase base = (TileEntityStargateBase) tile;
						base.getAsStructure().unpack(packet);
						w.markBlockForRenderUpdate(x, y, z);
					} else if (tile instanceof TileEntityRingPlatform) {
						TileEntityRingPlatform platform = (TileEntityRingPlatform) tile;
						platform.getStateFromPacket(packet);
						w.markBlockForRenderUpdate(x, y, z);
					} else if (tile instanceof TileEntityNaquadahGenerator) {
						TileEntityNaquadahGenerator generator = (TileEntityNaquadahGenerator) tile;
						generator.getStateFromPacket(packet);
						w.markBlockForRenderUpdate(x, y, z);
					}
				}
			}
		}

	}

}
