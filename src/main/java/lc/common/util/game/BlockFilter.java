package lc.common.util.game;

import lc.common.LCLog;
import net.minecraft.block.Block;
import net.minecraft.world.World;

public class BlockFilter {
	private Block targetBlock;
	private int targetMetadata;

	public BlockFilter(Block block) {
		new BlockFilter(block, -1);
	}

	public BlockFilter(Block block, int metadata) {
		if (block == null)
			throw new IllegalArgumentException("Cannot create filter on null block type!");
		targetBlock = block;
		targetMetadata = metadata;
	}

	public boolean matches(World world, int x, int y, int z) {
		Block blockOf = world.getBlock(x, y, z);
		if (blockOf.equals(targetBlock) && targetMetadata == -1)
			return true;
		if (blockOf.equals(targetBlock) && targetMetadata == world.getBlockMetadata(x, y, z))
			return true;

		if (blockOf != null && world != null && targetBlock != null)
			LCLog.info("No match: got (%s %s) expected (%s %s)", blockOf.getClass().getName(),
					world.getBlockMetadata(x, y, z), targetBlock.getClass().getName(), targetMetadata);
		return false;
	}

	public Block getBlock() {
		return targetBlock;
	}

	public int getMetadata() {
		return targetMetadata;
	}

	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("(b: ").append(targetBlock.getClass().getName()).append(",");
		result.append(" m: ").append(targetMetadata).append(")");
		return result.toString();
	}
}