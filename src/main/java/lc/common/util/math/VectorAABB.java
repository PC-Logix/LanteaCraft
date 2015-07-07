package lc.common.util.math;

import java.util.ArrayList;
import java.util.List;

/**
 * Vector axis-aligned bounding box implementation.
 *
 * @author AfterLifeLochie
 *
 */
public class VectorAABB {

	private Vector3 origin;
	private Vector3 size;

	/**
	 * Create a new box
	 *
	 * @param origin
	 *            The origin vector
	 * @param dim
	 *            The size vector
	 * @return The box
	 */
	public static VectorAABB boxOf(Vector3 origin, Vector3 dim) {
		return new VectorAABB(origin.copy(), dim.copy());
	}

	/**
	 * Create a new box
	 *
	 * @param origin
	 *            The origin vector
	 * @param width
	 *            The width (x)
	 * @param height
	 *            The height (y)
	 * @param length
	 *            The length (z)
	 * @return The box
	 */
	public static VectorAABB boxOf(Vector3 origin, int width, int height, int length) {
		return new VectorAABB(origin.copy(), new Vector3(width, height, length));
	}

	private VectorAABB(Vector3 origin, Vector3 size) {
		this.origin = origin;
		this.size = size;
	}

	/**
	 * Expand the VectorAABB by a vector size
	 *
	 * @param size
	 *            The size
	 * @return A copy of the newly transformed VectorAABB
	 */
	public VectorAABB expand(Vector3 size) {
		return new VectorAABB(origin, this.size.add(size));
	}

	/**
	 * Translate the VectorAABB by a vector size
	 *
	 * @param trans
	 *            The size
	 * @return A copy of the newly transformed VectorAABB
	 */
	public VectorAABB translate(Vector3 trans) {
		return new VectorAABB(origin.add(trans), size);
	}

	/**
	 * Rotate the box around a point
	 *
	 * @param point
	 *            The point
	 * @param rotation
	 *            The rotation
	 * @return A copy of the newly transformed VectorAABB
	 */
	public VectorAABB apply(Vector3 point, Matrix3 rotation) {
		return new VectorAABB(origin, rotation.mul(size));
	}

	/**
	 * Get a list of all Vector3 points in the box
	 *
	 * @return A list of all Vector3 points in bounds
	 */
	public List<Vector3> contents() {
		ArrayList<Vector3> result = new ArrayList<Vector3>();
		int x0 = Math.min(0, size.fx()), x1 = Math.max(0, size.fx());
		int y0 = Math.min(0, size.fy()), y1 = Math.max(0, size.fy());
		int z0 = Math.min(0, size.fz()), z1 = Math.max(0, size.fz());
		for (int x = x0; x < x1; x++)
			for (int z = z0; z < z1; z++)
				for (int y = y0; y < y1; y++)
					result.add(new Vector3(x, y, z));
		return result;
	}

	@Override
	public String toString() {
		return "VectorAABB (" + origin.toString() + ", " + size.toString() + ")";
	}
}
