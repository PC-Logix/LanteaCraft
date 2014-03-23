package pcl.lc.tileentity;

import java.util.logging.Level;

import pcl.common.base.GenericTileEntity;
import pcl.lc.LanteaCraft;
import pcl.lc.multiblock.StargatePart;

public class TileEntityStargateRing extends GenericTileEntity {
	private StargatePart thisPart = new StargatePart(this);

	public StargatePart getAsPart() {
		return thisPart;
	}

	@Override
	public boolean canUpdate() {
		return (thisPart.getType() == null && worldObj != null && !worldObj.isRemote);
	}

	@Override
	public void updateEntity() {
		if (thisPart.getType() == null)
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
		if (thisPart.getType() == null) {
			int ord = (worldObj.getBlockMetadata(xCoord, yCoord, zCoord) & 0x1);
			thisPart.setType((ord == 0) ? "partStargateBlock" : "partStargateChevron");
		}
		thisPart.devalidateHostMultiblock();
	}

}
