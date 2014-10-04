package lc.common.util.game;

import net.minecraft.block.Block;
import net.minecraft.world.World;

public class BlockFilter {
	private Block targetBlock;
	private int targetMetadata;

	public BlockFilter(Block block) {
		new BlockFilter(block, -1);
	}

	public BlockFilter(Block block, int metadata) {
		targetBlock = block;
		targetMetadata = metadata;
	}

	public boolean matches(World world, Block blockOf, int x, int y, int z) {
		if (blockOf.equals(targetBlock) && targetMetadata == -1)
			return true;
		if (blockOf.equals(targetBlock) && targetMetadata == world.getBlockMetadata(x, y, z))
			return true;
		return false;
	}

	public Block getBlock() {
		return targetBlock;
	}

	public int getMetadata() {
		return targetMetadata;
	}
}