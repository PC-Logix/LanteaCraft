package pcl.lc.module.core.render;

import pcl.lc.base.GenericBlockRenderer;
import pcl.lc.base.OrientedBlock;
import pcl.lc.util.Trans3;

public class BlockRotationOrientedRenderer extends GenericBlockRenderer {

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
