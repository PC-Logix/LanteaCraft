package pcl.lc.tileentity;

import net.minecraft.network.Packet;
import pcl.common.network.IPacketHandler;
import pcl.common.network.ModPacket;
import pcl.lc.LanteaCraft;
import pcl.lc.base.GenericTileEntity;
import pcl.lc.multiblock.StargatePart;

public class TileEntityStargateRing extends GenericTileEntity implements IPacketHandler {
	private StargatePart part = new StargatePart(this);

	@Override
	public Packet getDescriptionPacket() {
		ModPacket packet = part.pack();
		LanteaCraft.getNetPipeline().sendToAll(packet);
		return null;
	}

	@Override
	public void handlePacket(ModPacket packetOf) {
		part.unpack(packetOf);
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	public StargatePart getAsPart() {
		return part;
	}

	@Override
	public boolean canUpdate() {
		return true;
	}

	@Override
	public void updateEntity() {
		part.tick();
		if (part.getType() == null)
			flagDirty();
	}

	public void hostBlockPlaced() {
		if (!worldObj.isRemote)
			flagDirty();
	}

	public void hostBlockDestroyed() {
		if (!worldObj.isRemote)
			flagDirty();
	}

	public void flagDirty() {
		if (!worldObj.isRemote) {
			if (part.getType() == null) {
				int ord = (worldObj.getBlockMetadata(xCoord, yCoord, zCoord) & 0x1);
				part.setType((ord == 0) ? "partStargateBlock" : "partStargateChevron");
			}
			part.devalidateHostMultiblock();
		}
	}

}
