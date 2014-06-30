package pcl.lc.render.blocks;

import pcl.common.util.Trans3;
import pcl.lc.base.OrientedBlock;

public class RotationOrientedBlockRenderer extends GenericBlockRenderer {

	@Override
	public Trans3 localToInventoryTransformation(int metadata) {
		if (block instanceof OrientedBlock)
			return ((OrientedBlock) block).localToInventoryTransformation(metadata);
		else
			throw new IllegalArgumentException("Weird state: BaseOrientedRenderer expects an Oriented block, wat?");
	}

	@Override
	public Trans3 localToGlobalTransformation() {
		if (block instanceof OrientedBlock)
			return ((OrientedBlock) block).localToGlobalTransformation(world, blockX, blockY, blockZ);
		else
			throw new IllegalArgumentException("Weird state: BaseOrientedRenderer expects an Oriented block, wat?");
	}
}
