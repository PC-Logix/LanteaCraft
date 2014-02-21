package pcl.lc.multiblock;

import java.lang.ref.WeakReference;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import pcl.common.multiblock.GenericMultiblock;
import pcl.common.multiblock.MultiblockPart;
import pcl.common.multiblock.ScanningHelper;
import pcl.common.util.Vector3;
import pcl.lc.tileentity.TileEntityStargateBase;

public class StargatePart extends MultiblockPart {

	private String typeof;

	private WeakReference<GenericMultiblock> currentHost;

	public StargatePart(TileEntity host) {
		super(host);
	}

	public void setType(String typeof) {
		this.typeof = typeof;
	}

	@Override
	public GenericMultiblock findHostMultiblock(boolean allowScanning) {
		if (currentHost != null && currentHost.get() != null)
			return currentHost.get();

		if (!allowScanning)
			return null;
		AxisAlignedBB bounds = AxisAlignedBB.getBoundingBox(-7, -7, -7, 7, 7, 7);
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
		return isClient || currentHost == null || currentHost.get() == null;
	}

	@Override
	public boolean mergeWith(GenericMultiblock structure) {
		currentHost = new WeakReference<GenericMultiblock>(structure);
		return true;
	}

	@Override
	public boolean isMerged() {
		return (currentHost != null && currentHost.get() != null);
	}

	@Override
	public void release() {
		currentHost = null;
		host.worldObj.markBlockForUpdate(host.xCoord, host.yCoord, host.zCoord);
		host.worldObj.markBlockForRenderUpdate(host.xCoord, host.yCoord, host.zCoord);
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
