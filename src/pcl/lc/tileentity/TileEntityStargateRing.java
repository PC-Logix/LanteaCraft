package pcl.lc.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import pcl.common.base.GenericTileEntity;
import pcl.lc.multiblock.StargatePart;

public class TileEntityStargateRing extends GenericTileEntity {

	public boolean isMerged;
	public int baseX, baseY, baseZ;

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

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		// System.out.printf("SGRingTE.readFromNBT\n");
		super.readFromNBT(nbt);
		isMerged = nbt.getBoolean("isMerged");
		baseX = nbt.getInteger("baseX");
		baseY = nbt.getInteger("baseY");
		baseZ = nbt.getInteger("baseZ");
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		// System.out.printf("SGRingTE.writeToNBT\n");
		super.writeToNBT(nbt);
		nbt.setBoolean("isMerged", isMerged);
		nbt.setInteger("baseX", baseX);
		nbt.setInteger("baseY", baseY);
		nbt.setInteger("baseZ", baseZ);
	}

}
