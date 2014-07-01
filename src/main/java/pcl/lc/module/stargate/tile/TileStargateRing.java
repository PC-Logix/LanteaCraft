package pcl.lc.module.stargate.tile;

import net.minecraft.network.Packet;
import pcl.common.util.WorldLocation;
import pcl.lc.LanteaCraft;
import pcl.lc.base.GenericTileEntity;
import pcl.lc.base.network.IPacketHandler;
import pcl.lc.base.network.ModPacket;
import pcl.lc.module.stargate.StargatePart;

public class TileStargateRing extends GenericTileEntity implements IPacketHandler {
	private StargatePart part = new StargatePart(this);

	@Override
	public Packet getDescriptionPacket() {
		ModPacket packet = part.pack();
		LanteaCraft.getNetPipeline().sendToAllAround(packet, new WorldLocation(this), 128.0d);
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
