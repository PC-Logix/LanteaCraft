package lc.common.base.multiblock;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import lc.common.LCLog;
import lc.common.util.game.BlockFilter;
import lc.common.util.math.Matrix3;
import lc.common.util.math.Orientations;
import lc.common.util.math.Vector3;
import lc.common.util.math.VectorAABB;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

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
		BlockFilter[] mappings = getBlockMappings();
		Matrix3 rotation = (orientation != null) ? orientation.rotation() : Matrix3.ident;
		Vector3 origin = new Vector3(x, y, z).sub(rotation.mul(getStructureCenter()));
		VectorAABB box = VectorAABB.boxOf(origin, getStructureDimensions());
		List<Vector3> elems = box.contents();
		Iterator<Vector3> each = elems.iterator();
		while (each.hasNext()) {
			Vector3 mapping = each.next();
			Vector3 tile = origin.add(rotation.mul(mapping));

			try {
				int cell = getStructureLayout()[mapping.rx()][mapping.ry()][mapping.rz()];
				BlockFilter filter = mappings[cell];
				if (!filter.matches(world, tile.rx(), tile.ry(), tile.rz()))
					return false;
			} catch (IndexOutOfBoundsException bounds) {
				LCLog.fatal("Access out of bounds: " + bounds.getMessage() + ": "
						+ String.format("%s %s %s", mapping.rx(), mapping.ry(), mapping.rz()));
				return false;
			}
		}
		return true;
	}

	/**
	 * Generate a map of all types specified in the structure configuration.
	 * 
	 * @param x
	 *            The x-coordinate to rotate around
	 * @param y
	 *            The y-coordinate to rotate around
	 * @param z
	 *            The z-coordinate to rotate around
	 * @param typeof
	 *            The type of filter path
	 * @param orientation
	 *            The rotation
	 * @return A list of all Vector3 paths which reuslt in a typeof specified.
	 */
	public Vector3[] mapType(int x, int y, int z, int typeof, Orientations orientation) {
		ArrayList<Vector3> vectors = new ArrayList<Vector3>();
		Matrix3 rotation = (orientation != null) ? orientation.rotation() : Matrix3.ident;
		Vector3 origin = new Vector3(x, y, z).sub(rotation.mul(getStructureCenter()));
		VectorAABB box = VectorAABB.boxOf(origin, getStructureDimensions());
		List<Vector3> elems = box.contents();
		Iterator<Vector3> each = elems.iterator();
		while (each.hasNext()) {
			Vector3 mapping = each.next();
			Vector3 tile = origin.add(rotation.mul(mapping.add(0.5f, 0.5f, 0.5f)));
			try {
				int cell = getStructureLayout()[mapping.rx()][mapping.ry()][mapping.rz()];
				if (cell == typeof)
					vectors.add(tile);
			} catch (IndexOutOfBoundsException bounds) {
				LCLog.fatal("Access out of bounds: " + bounds.getMessage() + ": "
						+ String.format("%s %s %s", mapping.rx(), mapping.ry(), mapping.rz()));
			}
		}
		return vectors.toArray(new Vector3[0]);
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
	 * @param owner
	 *            The owner tile
	 */
	public void apply(World world, int x, int y, int z, Orientations orientation, LCMultiblockTile owner) {
		Vector3 ownerVec = (owner != null) ? new Vector3(owner) : null;
		Matrix3 rotation = (orientation != null) ? orientation.rotation() : Matrix3.ident;
		Vector3 origin = new Vector3(x, y, z).sub(rotation.mul(getStructureCenter()));
		VectorAABB box = VectorAABB.boxOf(origin, getStructureDimensions());
		List<Vector3> elems = box.contents();
		Iterator<Vector3> each = elems.iterator();
		while (each.hasNext()) {
			Vector3 mapping = each.next();
			Vector3 tile = origin.add(rotation.mul(mapping));
			try {
				TileEntity wTile = world.getTileEntity(tile.rx(), tile.ry(), tile.rz());
				if (wTile != null && wTile instanceof LCMultiblockTile) {
					LCMultiblockTile multiTile = (LCMultiblockTile) wTile;
					if (multiTile.isSlave())
						multiTile.setOwner(ownerVec);
				}
			} catch (IndexOutOfBoundsException bounds) {
				LCLog.fatal("Access out of bounds: " + bounds.getMessage() + ": "
						+ String.format("%s %s %s", mapping.rx(), mapping.ry(), mapping.rz()));
			}
		}
	}

}