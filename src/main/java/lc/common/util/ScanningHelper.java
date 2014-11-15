package lc.common.util;

import java.util.ArrayList;

import lc.common.util.math.Vector3;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

/**
 * World scanning helpers
 *
 * @author AfterLifeLochie
 *
 */
public class ScanningHelper {

	/**
	 * Scans a given AxisAlignedBB bound zone for all matching TileEntity of the
	 * specified class. If no matching tile-entities are found, this will return
	 * an empty ArrayList
	 *
	 * @param world
	 *            The fully qualified world object.
	 * @param clazz
	 *            The class to find an instance of.
	 * @param x
	 *            The origin-x coordinate.
	 * @param y
	 *            The origin-y coordinate.
	 * @param z
	 *            The origin-z coordinate.
	 * @param bounds
	 *            The {@link AxisAlignedBB} bounds to search inside.
	 * @return All matching TileEntity to the origin, or an empty ArrayList if
	 *         no such tile entities are found in the bounds area
	 */
	public static ArrayList<Vector3> findAllTileEntitesOf(World world, Class<? extends TileEntity> clazz, int x, int y,
			int z, AxisAlignedBB bounds) {
		ArrayList<Vector3> poolMatching = new ArrayList<Vector3>();
		for (int ix = x + (int) Math.floor(bounds.minX); ix < x + bounds.maxX; ix++)
			for (int iy = y + (int) Math.floor(bounds.minY); iy < y + bounds.maxY; iy++)
				for (int iz = z + (int) Math.floor(bounds.minZ); iz < z + bounds.maxZ; iz++) {
					TileEntity object = world.getTileEntity(ix, iy, iz);
					if (object != null && object.getClass().equals(clazz))
						poolMatching.add(new Vector3(ix - x, iy - y, iz - z));
				}
		return poolMatching;
	}

	/**
	 * Scans a given AxisAlignedBB bound zone for the best matching TileEntity
	 * of the specified class. If no matching tile-entities are found, this will
	 * return null.
	 *
	 * @param world
	 *            The fully qualified world object.
	 * @param clazz
	 *            The class to find an instance of.
	 * @param x
	 *            The origin-x coordinate.
	 * @param y
	 *            The origin-y coordinate.
	 * @param z
	 *            The origin-z coordinate.
	 * @param bounds
	 *            The {@link AxisAlignedBB} bounds to search inside.
	 * @return The best matching TileEntity to the origin, or null if no such
	 *         tile entity is found in the bounds area
	 */
	public static TileEntity findNearestTileEntityOf(World world, Class<? extends TileEntity> clazz, int x, int y,
			int z, AxisAlignedBB bounds) {
		ArrayList<Vector3> poolMatching = findAllTileEntitesOf(world, clazz, x, y, z, bounds);
		Vector3 best = new Vector3(9999D, 9999D, 9999D);
		for (Vector3 item : poolMatching)
			if (best.mag() > item.mag())
				best = item;
		return world.getTileEntity((int) Math.floor(x + best.x), (int) Math.floor(y + best.y),
				(int) Math.floor(z + best.z));
	}

}
