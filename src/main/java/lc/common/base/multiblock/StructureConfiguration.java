package lc.common.base.multiblock;

import java.util.Iterator;
import java.util.List;

import net.minecraft.world.World;
import lc.common.LCLog;
import lc.common.util.game.BlockFilter;
import lc.common.util.math.Orientations;
import lc.common.util.math.Vector3;
import lc.common.util.math.VectorAABB;
import lc.common.base.multiblock.LCMultiblockTile;

/**
 * Represents a configuration setup for a particular multiblock structure.
 * 
 * @author AfterLifeLochie
 * 
 */
public abstract class StructureConfiguration {

	/**
	 * Get the absolute XYZ dimensions of the structure layout. The values of
	 * this tuple should be immutable and should represent the absolute
	 * dimensions of the structure. Dimensions should be real, positive numbers,
	 * such that the dimensions represent the magnitude of the structure's
	 * dimension vector.
	 * 
	 * @return The XYZ dimensions of the structure layout.
	 */
	public abstract Vector3 getStructureDimensions();

	/**
	 * Get the absolute XYZ center of the structure layout. In the event the
	 * structure has a center orientation (eg, the center of the block is the
	 * middle of the structure), this should return a vector dimension from the
	 * normal (0, 0, 0) to the center of the structure. If the structure is
	 * baseline (has no center), return the normal instead.
	 * 
	 * @return The XYZ coordinate of the structure layout.
	 */
	public abstract Vector3 getStructureCenter();

	/**
	 * Get the layout of the structure. Returns a three-dimensional collection
	 * of integers which represent identifiers, not block identifiers, for the
	 * structure. The number of entries in each dimension should match that of @link
	 * {@link StructureConfiguration#getStructureDimensions()}.
	 * 
	 * @return The structure layout
	 */
	public abstract int[][][] getStructureLayout();

	/**
	 * Get the mappings between IDs and Block objects
	 * 
	 * @return The mappings of the structure
	 */
	public abstract BlockFilter[] getBlockMappings();

	/**
	 * Test to see if this structure configuration is valid in a world at a
	 * particular set of coordinates.
	 * 
	 * @param world
	 *            The world object.
	 * @param x
	 *            The x-coordinate to test
	 * @param y
	 *            The y-coordinate to test
	 * @param z
	 *            The z-coordinate to test
	 * @param orientation
	 *            The orientation
	 * @return If the configuration is valid.
	 */
	public boolean test(World world, int x, int y, int z, Orientations orientation) {
		VectorAABB box = VectorAABB.boxOf(new Vector3(x, y, z), getStructureDimensions());
		box = box.translate(Vector3.zero.sub(getStructureCenter()));
		// box.apply(getStructureCenter(), orientation.rotation());

		BlockFilter[] mappings = getBlockMappings();

		List<Vector3> elems = box.contents();
		Iterator<Vector3> each = elems.iterator();
		while (each.hasNext()) {
			Vector3 me = each.next();
			Vector3 mapping = me.sub(new Vector3(x, y, z)).add(getStructureCenter());
			int cell = getStructureLayout()[mapping.floorX()][mapping.floorY()][mapping.floorZ()];
			BlockFilter filter = mappings[cell];
			if (!filter.matches(world, me.floorX(), me.floorY(), me.floorZ())) {
				LCLog.info("Failed match on %s at %s %s %s", filter, me.floorX(), me.floorY(), me.floorZ());
				return false;
			}
		}
		LCLog.info("All is OK: checked %s items", elems.size());
		return true;
	}

	/**
	 * Apply a configuration to a set of blocks in the structure.
	 * 
	 * @param world
	 *            The world object.
	 * @param x
	 *            The x-coordinate to test
	 * @param y
	 *            The y-coordinate to test
	 * @param z
	 *            The z-coordinate to test
	 * @param orientation
	 *            The orientation
	 */
	public void apply(World world, int x, int y, int z, Orientations orientation, LCMultiblockTile owner) {
		// FIXME
	}

}