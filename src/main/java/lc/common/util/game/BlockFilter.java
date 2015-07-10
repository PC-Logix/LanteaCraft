package lc.common.util.game;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.world.World;

/**
 * Block-based criteria filter
 *
 * @author AfterLifeLochie
 *
 */
public class BlockFilter {
	private Block targetBlock;
	private int targetMetadata;

	/**
	 * Create a new block filter for a type of block
	 *
	 * @param block
	 *            The block type
	 */
	public BlockFilter(Block block) {
		this(block, -1);
	}

	/**
	 * Create a new block filter for a type of block with a specified metadata
	 * value
	 *
	 * @param block
	 *            The block type
	 * @param metadata
	 *            The metadata value
	 */
	public BlockFilter(Block block, int metadata) {
		if (block == null)
			throw new IllegalArgumentException("Cannot create filter on null block type!");
		targetBlock = block;
		targetMetadata = metadata;
	}

	/**
	 * Check if a block in the world provided matches the filter.
	 *
	 * @param world
	 *            The world object.
	 * @param x
	 *            The x-coordinate to test.
	 * @param y
	 *            The y-coordinate to test.
	 * @param z
	 *            The z-coordinate to test.
	 * @return If the block at the provided coordinates in the provided world
	 *         matches this specified filter.
	 */
	public boolean matches(World world, int x, int y, int z) {
		Block blockOf = world.getBlock(x, y, z);
		if (targetBlock.getMaterial() == Material.air && blockOf.isAir(world, x, y, z))
			return true;
		if (blockOf.equals(targetBlock) && targetMetadata == -1)
			return true;
		if (blockOf.equals(targetBlock) && targetMetadata == world.getBlockMetadata(x, y, z))
			return true;
		return false;
	}

	/**
	 * Get the block type.
	 *
	 * @return The block type.
	 */
	public Block getBlock() {
		return targetBlock;
	}

	/**
	 * Get the target metadata value.
	 *
	 * @return The target metadata value.
	 */
	public int getMetadata() {
		return targetMetadata;
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("BlockFilter").append("{");
		result.append(targetBlock.getClass().getName()).append(",");
		result.append(targetMetadata).append("}");
		return result.toString();
	}
}