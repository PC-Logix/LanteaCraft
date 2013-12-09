package pcl.lc.multiblock;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import pcl.common.multiblock.GenericMultiblock;
import pcl.common.multiblock.MultiblockPart;
import pcl.common.multiblock.ScanningHelper;
import pcl.common.util.Vector3;
import pcl.lc.tileentity.TileEntityStargateBase;

public class StargatePart extends MultiblockPart {

	private String typeof;

	private GenericMultiblock currentHost;

	public StargatePart(TileEntity host) {
		super(host);
	}

	public void setType(String typeof) {
		this.typeof = typeof;
	}

	@Override
	public GenericMultiblock findHostMultiblock(boolean allowScanning) {
		if (currentHost != null)
			return currentHost;

		if (!allowScanning)
			return null;
		AxisAlignedBB bounds = AxisAlignedBB.getBoundingBox(-5, -5, -5, 5, 5, 5);
		TileEntity entity = ScanningHelper.findNearestTileEntityOf(host.worldObj, TileEntityStargateBase.class,
				host.xCoord, host.yCoord, host.zCoord, bounds);
		if (entity == null)
			return null;
		TileEntityStargateBase baseObj = (TileEntityStargateBase) entity;
		StargateMultiblock stargateStruct = baseObj.getAsStructure();
		return stargateStruct;
	}

	@Override
	public boolean canMergeWith(GenericMultiblock structure) {
		if (isClient)
			return true;

		if (currentHost == null)
			return true;
		return false;
	}

	@Override
	public boolean mergeWith(GenericMultiblock structure) {
		currentHost = structure;
		return true;
	}

	@Override
	public boolean isMerged() {
		return (currentHost != null);
	}

	@Override
	public void release() {
		currentHost = null;
	}

	@Override
	public String getType() {
		return typeof;
	}

	@Override
	public Vector3 getVectorLoc() {
		return new Vector3(host.xCoord, host.yCoord, host.zCoord);
	}

}
