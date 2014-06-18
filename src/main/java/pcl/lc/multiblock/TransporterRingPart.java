package pcl.lc.multiblock;

import pcl.common.multiblock.GenericMultiblock;
import pcl.common.multiblock.MultiblockPart;
import pcl.common.util.Vector3;
import pcl.lc.tileentity.TileEntityTransporterRing;

public class TransporterRingPart extends MultiblockPart {

	public TransporterRingPart(TileEntityTransporterRing host) {
		super(host);
	}

	@Override
	public GenericMultiblock findHostMultiblock(boolean allowScanning) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean canMergeWith(GenericMultiblock structure) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mergeWith(GenericMultiblock structure) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isMerged() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void release() {
		// TODO Auto-generated method stub

	}

	@Override
	public String getType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Vector3 getVectorLoc() {
		// TODO Auto-generated method stub
		return null;
	}

}
