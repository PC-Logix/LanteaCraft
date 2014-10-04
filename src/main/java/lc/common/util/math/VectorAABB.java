package lc.common.util.math;

import java.util.ArrayList;
import java.util.List;

public class VectorAABB {

	private Vector3 min;
	private Vector3 max;

	public static VectorAABB box(Vector3 min, Vector3 max) {
		return new VectorAABB(min, max);
	}

	public static VectorAABB boxOf(Vector3 origin, Vector3 dim) {
		return new VectorAABB(origin, origin.add(dim));
	}

	public static VectorAABB boxOf(Vector3 origin, int width, int height, int length) {
		return new VectorAABB(origin, origin.add(new Vector3(width, height, length)));
	}

	private VectorAABB(Vector3 min, Vector3 max) {
		this.min = min;
		this.max = max;
	}

	public VectorAABB expand(Vector3 size) {
		return new VectorAABB(min, max.add(size));
	}

	public List<Vector3> contents() {
		ArrayList<Vector3> result = new ArrayList<Vector3>();
		for (int x = min.floorX(); x < max.floorX(); x++)
			for (int z = min.floorZ(); z < max.floorZ(); z++)
				for (int y = min.floorY(); y < max.floorY(); y++)
					result.add(new Vector3(x, y, z));
		return result;
	}

	public void apply(Vector3 point, Matrix3 rotation) {
		// TODO Auto-generated method stub
	}
}
