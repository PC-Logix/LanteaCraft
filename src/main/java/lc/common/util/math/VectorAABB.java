package lc.common.util.math;

import java.util.ArrayList;
import java.util.List;

public class VectorAABB {

	private Vector3 origin;
	private Vector3 size;

	public static VectorAABB boxOf(Vector3 origin, Vector3 dim) {
		return new VectorAABB(origin, dim);
	}

	public static VectorAABB boxOf(Vector3 origin, int width, int height, int length) {
		return new VectorAABB(origin, new Vector3(width, height, length));
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

	public List<Vector3> contents() {
		ArrayList<Vector3> result = new ArrayList<Vector3>();
		for (int x = 0; x < size.floorX(); x++)
			for (int z = 0; z < size.floorZ(); z++)
				for (int y = 0; y < size.floorY(); y++)
					result.add(new Vector3(origin.floorX() + x, origin.floorY() + y, origin.floorZ() + z));
		return result;
	}

	public void apply(Vector3 point, Matrix3 rotation) {
		// TODO Auto-generated method stub
	}
}
