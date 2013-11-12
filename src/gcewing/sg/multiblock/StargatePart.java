package gcewing.sg.multiblock;

import gcewing.sg.SGCraft;
import gcewing.sg.tileentity.TileEntityStargateBase;
import gcewing.sg.util.Vector3;

import java.lang.ref.WeakReference;
import java.util.logging.Level;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;

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
		if (currentHost == null)
			return true;
		return false;
	}

	@Override
	public boolean mergeWith(GenericMultiblock structure) {
		this.currentHost = structure;
		return true;
	}

	@Override
	public boolean isMerged() {
		return (currentHost != null);
	}

	@Override
	public void release() {
		this.currentHost = null;
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
