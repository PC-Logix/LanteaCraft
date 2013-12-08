package pcl.lc.multiblock;

import net.minecraft.block.Block;
import net.minecraft.world.World;

public class BlockFilter {
	private int targetId;
	private int targetMetadata;

	public BlockFilter(int id) {
		new BlockFilter(id, -1);
	}

	public BlockFilter(int id, int metadata) {
		this.targetId = id;
		this.targetMetadata = metadata;
	}

	public boolean matches(World world, Block blockOf, int x, int y, int z) {
		// test for immediate id match, metadata ignored
		if (blockOf.blockID == targetId && targetMetadata == -1) return true;
		// test for immediate id match & metadata match
		if (blockOf.blockID == targetId && targetMetadata == world.getBlockMetadata(x, y, z)) return true;
		return false;
	}

	public int getId() {
		return targetId;
	}

	public int getMetadata() {
		return targetMetadata;
	}
}
