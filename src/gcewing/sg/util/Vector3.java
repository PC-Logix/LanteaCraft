package gcewing.sg.util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import gcewing.sg.network.IStreamPackable;
import gcewing.sg.network.SGCraftPacket;
import net.minecraft.util.Vec3;

public class Vector3 {

	public static Vector3 zero = new Vector3(0, 0, 0);

	public static Vector3 unitX = new Vector3(1, 0, 0);
	public static Vector3 unitY = new Vector3(0, 1, 0);
	public static Vector3 unitZ = new Vector3(0, 0, 1);

	public static Vector3 unitNX = new Vector3(-1, 0, 0);
	public static Vector3 unitNY = new Vector3(0, -1, 0);
	public static Vector3 unitNZ = new Vector3(0, 0, -1);

	public static Vector3 unitPYNZ = new Vector3(0, 0.707, -0.707);
	public static Vector3 unitPXPY = new Vector3(0.707, 0.707, 0);
	public static Vector3 unitPYPZ = new Vector3(0, 0.707, 0.707);
	public static Vector3 unitNXPY = new Vector3(-0.707, 0.707, 0);

	public double x, y, z;

	public Vector3(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Vector3(Vec3 v) {
		this(v.xCoord, v.yCoord, v.zCoord);
	}

	public Vec3 toVec3() {
		return Vec3.createVectorHelper(x, y, z);
	}

	@Override
	public String toString() {
		return "Vector3(" + x + "," + y + "," + z + ")";
	}

	public Vector3 add(double x, double y, double z) {
		return new Vector3(this.x + x, this.y + y, this.z + z);
	}

	public Vector3 add(Vector3 v) {
		return add(v.x, v.y, v.z);
	}

	public Vector3 sub(double x, double y, double z) {
		return new Vector3(this.x - x, this.y - y, this.z - z);
	}

	public Vector3 sub(Vector3 v) {
		return sub(v.x, v.y, v.z);
	}

	public Vector3 mul(double c) {
		return new Vector3(c * x, c * y, c * z);
	}

	public double dot(Vector3 v) {
		return x * v.x + y * v.y + z * v.z;
	}

	public Vector3 min(Vector3 v) {
		return new Vector3(Math.min(x, v.x), Math.min(y, v.y), Math.min(z, v.z));
	}

	public Vector3 max(Vector3 v) {
		return new Vector3(Math.max(x, v.x), Math.max(y, v.y), Math.max(z, v.z));
	}

	public double distanceTo(Vector3 v) {
		double dx = x - v.x, dy = y - v.y, dz = z - v.z;
		return Math.sqrt(dx * dx + dy * dy + dz * dz);
	}

	public double mag() {
		return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));
	}

	public int floorX() {
		return (int) Math.floor(x);
	}

	public int floorY() {
		return (int) Math.floor(y);
	}

	public int floorZ() {
		return (int) Math.floor(z);
	}
}
