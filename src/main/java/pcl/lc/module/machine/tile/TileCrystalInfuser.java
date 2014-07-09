package pcl.lc.module.machine.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import pcl.lc.base.TileManaged;
import pcl.lc.base.network.packet.ModPacket;

public class TileCrystalInfuser extends TileManaged {

	@Override
	public void think() {
		// TODO Auto-generated method stub

	}

	@Override
	public void thinkPacket(ModPacket packet, EntityPlayer player) {
		// TODO Auto-generated method stub

	}

	@Override
	public void detectAndSendChanges() {
		// TODO Auto-generated method stub

	}

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return AxisAlignedBB.getAABBPool().getAABB(xCoord - 1, yCoord - 1, zCoord - 1, xCoord + 1, yCoord + 1,
				zCoord + 1);
	}

}
