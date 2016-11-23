package lc.common.util.math;

import static java.lang.Math.max;
import static java.lang.Math.min;
import net.minecraft.util.AxisAlignedBB;

/**
 * Represents a Transformation in three-dimensional space.
 *
 * @author AfterLifeLochie
 *
 */
public class Trans3 {

	/**
	 * The identity Transformation.
	 */
	public static Trans3 ident = new Trans3(Vector3.zero, Matrix3.ident, 1.0);

	/**
	 * A vector representing an origin-based offset.
	 */
	public Vector3 offset;

	/**
	 * A matrix representing a canRotate.
	 */
	public Matrix3 rotation;

	/**
	 * A double representing a scaling factor.
	 */
	public double scaling;

	/**
	 * Creates a new Trans3.
	 *
	 * @param v
	 *            The offset vector.
	 * @param m
	 *            The canRotate matrix.
	 * @param s
	 *            The scaling factor.
	 */
	Trans3(Vector3 v, Matrix3 m, double s) {
		offset = v;
		rotation = m;
		scaling = s;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Trans3{");
		sb.append("Offset:(").append(offset).append("),");
		sb.append("Scaling:(").append(scaling).append("),");
		sb.append("Rotation:(").append(rotation).append(")}");
		return sb.toString();
	}

	/**
	 * Creates a new Trans3.
	 *
	 * @param dx
	 *            The offset on the x-axis.
	 * @param dy
	 *            The offset on the y-axis.
	 * @param dz
	 *            The offset on the z-axis.
	 */
	public Trans3(double dx, double dy, double dz) {
		this(new Vector3(dx, dy, dz), Matrix3.ident, 1.0);
	}

	/**
	 * Translates this Trans3 into a new Trans3, applying the local canRotate
	 * and local offset factors.
	 *
	 * @param dx
	 *            The offset on the x-axis.
	 * @param dy
	 *            The offset on the y-axis.
	 * @param dz
	 *            The offset on the z-axis.
	 * @return The Trans3 product.
	 */
	public Trans3 translate(double dx, double dy, double dz) {
		return new Trans3(offset.add(rotation.mul(dx * scaling, dy * scaling, dz * scaling)), rotation, scaling);
	}

	/**
	 * Rotates this Trans3 around a Matrix3.
	 *
	 * @param m
	 *            The canRotate matrix.
	 * @return The new Trans3.
	 */
	public Trans3 rotate(Matrix3 m) {
		return new Trans3(offset, rotation.mul(m), scaling);
	}

	/**
	 * Scales this Trans3 by a factor s.
	 *
	 * @param s
	 *            The scaling factor.
	 * @return The new Trans3.
	 */
	public Trans3 scale(double s) {
		return new Trans3(offset, rotation, scaling * s);
	}

	/**
	 * Rotate on side i
	 *
	 * @param i
	 *            The side
	 * @return The rotation matrix
	 */
	public Trans3 side(int i) {
		return rotate(Matrix3.sideRotations[i]);
	}

	/**
	 * Turn on side i
	 *
	 * @param i
	 *            The side
	 * @return The rotation matrix
	 */
	public Trans3 turn(int i) {
		return rotate(Matrix3.turnRotations[i]);
	}

	/**
	 * Apply all transformations
	 *
	 * @param t
	 *            The input trans
	 * @return The result trans
	 */
	public Trans3 t(Trans3 t) {
		return new Trans3(offset.add(rotation.mul(t.offset).mul(scaling)), rotation.mul(t.rotation), scaling
				* t.scaling);
	}

	/**
	 * Apply the offset, rotation and scaling of this Translation to the xyz
	 * coordinate pair
	 *
	 * @param x
	 *            The x-coordinate
	 * @param y
	 *            The y-coordinate
	 * @param z
	 *            The z-coordinate
	 * @return The resultant Vector3
	 */
	public Vector3 p(double x, double y, double z) {
		return p(new Vector3(x, y, z));
	}

	/**
	 * Apply the offset, rotation and scaling of this Translation to the Vector
	 * provided.
	 *
	 * @param u
	 *            The input Vector2
	 * @return The resultant Vector3
	 */
	public Vector3 p(Vector3 u) {
		return offset.add(rotation.mul(u.mul(scaling)));
	}

	public Vector3 ip(double x, double y, double z) {
		return ip(new Vector3(x, y, z));
	}

	public Vector3 ip(Vector3 u) {
		return rotation.imul(u.sub(offset)).mul(1.0 / scaling);
	}

	public Vector3 v(double x, double y, double z) {
		return v(new Vector3(x, y, z));
	}

	public Vector3 iv(double x, double y, double z) {
		return iv(new Vector3(x, y, z));
	}

	public Vector3 v(Vector3 u) {
		return rotation.mul(u.mul(scaling));
	}

	public Vector3 iv(Vector3 u) {
		return rotation.imul(u).mul(1.0 / scaling);
	}

	public AxisAlignedBB t(AxisAlignedBB box) {
		return boxEnclosing(p(box.minX, box.minY, box.minZ), p(box.maxX, box.maxY, box.maxZ));
	}

	public AxisAlignedBB box(Vector3 p0, Vector3 p1) {
		return boxEnclosing(p(p0), p(p1));
	}

	static AxisAlignedBB boxEnclosing(Vector3 p, Vector3 q) {
		return AxisAlignedBB.getBoundingBox(min(p.x, q.x), min(p.y, q.y), min(p.z, q.z), max(p.x, q.x), max(p.y, q.y),
				max(p.z, q.z));
	}

}
