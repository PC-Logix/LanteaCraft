package lc.common.util.math;

import java.util.ArrayList;
import java.util.List;

public class VectorAABB {

	private Vector3 origin;
	private Vector3 size;

	public static VectorAABB boxOf(Vector3 origin, Vector3 dim) {
		return new VectorAABB(origin.copy(), dim.copy());
	}

	public static VectorAABB boxOf(Vector3 origin, int width, int height, int length) {
		return new VectorAABB(origin.copy(), new Vector3(width, height, length));
	}

	private VectorAABB(Vector3 origin, Vector3 size) {
		this.origin = origin;
		this.size = size;
	}

	public VectorAABB expand(Vector3 size) {
		return new VectorAABB(origin, this.size.add(size));
	}

	public VectorAABB translate(Vector3 trans) {
		return new VectorAABB(origin.add(trans), size);
	}

	public VectorAABB apply(Vector3 point, Matrix3 rotation) {
		return new VectorAABB(origin, rotation.mul(size));
	}

	public List<Vector3> contents() {
		ArrayList<Vector3> result = new ArrayList<Vector3>();
		int x0 = Math.min(0, size.floorX()), x1 = Math.max(0, size.floorX());
		int y0 = Math.min(0, size.floorY()), y1 = Math.max(0, size.floorY());
		int z0 = Math.min(0, size.floorZ()), z1 = Math.max(0, size.floorZ());
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
