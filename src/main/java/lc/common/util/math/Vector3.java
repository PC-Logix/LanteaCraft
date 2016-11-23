package lc.common.util.math;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * Represents a Vector in three-dimensional space.
 *
 * @author AfterLifeLochie
 */
public class Vector3 {

	/**
	 * The zero vector.
	 */
	public static final Vector3 zero = new Vector3(0, 0, 0);

	/**
	 * The identity vector.
	 */
	public static Vector3 ident = new Vector3(1, 1, 1);

	/**
	 * The unit vector in the x-axis.
	 */

	public static final Vector3 unitX = new Vector3(1, 0, 0);
	/**
	 * The unit vector in the y-axis.
	 */

	public static final Vector3 unitY = new Vector3(0, 1, 0);
	/**
	 * The unit vector in the y-axis.
	 */
	public static final Vector3 unitZ = new Vector3(0, 0, 1);

	/**
	 * The unit vector in the negative x-axis.
	 */
	public static final Vector3 unitNX = new Vector3(-1, 0, 0);

	/**
	 * The unit vector in the negative y-axis.
	 */
	public static final Vector3 unitNY = new Vector3(0, -1, 0);

	/**
	 * The unit vector in the negative z-axis.
	 */
	public static final Vector3 unitNZ = new Vector3(0, 0, -1);

	/**
	 * The unit vector in the x-axis and the y-axis.
	 */
	public static final Vector3 unitPXPY = new Vector3(0.707, 0.707, 0);

	/**
	 * The unit vector in the y-axis and the z-axis.
	 */
	public static final Vector3 unitPYPZ = new Vector3(0, 0.707, 0.707);

	/**
	 * The unit vector in the y-axis and the negative z-axis.
	 */
	public static final Vector3 unitPYNZ = new Vector3(0, 0.707, -0.707);

	/**
	 * The unit vector in the y-axis and the negative x-axis.
	 */
	public static final Vector3 unitPYNX = new Vector3(-0.707, 0.707, 0);

	/**
	 * The x-component of the vector.
	 */
	public final double x;

	/**
	 * The y-component of the vector.
	 */
	public final double y;

	/**
	 * The z-component of the vector.
	 */
	public final double z;

	/**
	 * Creates a new vector.
	 *
	 * @param x
	 *            The x-component of the vector.
	 * @param y
	 *            The y-component of the vector.
	 * @param z
	 *            The z-component of the vector.
	 */
	public Vector3(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/**
	 * Creates a new vector.
	 *
	 * @param v
	 *            The Minecraft Vec3 object.
	 */
	public Vector3(Vec3 v) {
		this(v.xCoord, v.yCoord, v.zCoord);
	}

	/**
	 * Creates a new vector from an entity position.
	 *
	 * @param entity
	 *            The entity.
	 */
	public Vector3(Entity entity) {
		this(entity.posX, entity.posY, entity.posZ);
	}

	/**
	 * Creates a new vector from a tile entity position.
	 *
	 * @param tileentity
	 *            The tile entity.
	 */
	public Vector3(TileEntity tileentity) {
		this(tileentity.xCoord, tileentity.yCoord, tileentity.zCoord);
	}

	/**
	 * Creates a new vector from a Forge direction.
	 *
	 * @param direction
	 *            The forge direction.
	 */
	public Vector3(ForgeDirection direction) {
		this(direction.offsetX, direction.offsetY, direction.offsetZ);
	}

	/**
	 * Creates a new vector from an NBT Compound.
	 *
	 * @param compound
	 *            The NBT Compound.
	 * @return The resulting vector element.
	 */
	public static Vector3 from(NBTTagCompound compound) {
		if (!compound.hasKey("x") || !compound.hasKey("y") || !compound.hasKey("z"))
			throw new IllegalArgumentException("Compound is not a packed Vector3!");
		return new Vector3(compound.getDouble("x"), compound.getDouble("y"), compound.getDouble("z"));
	}

	/**
	 * Convert a vector pair to an AABB
	 *
	 * @param min
	 *            The min vector
	 * @param max
	 *            The max vector
	 * @return An AABB
	 */
	public static AxisAlignedBB makeAABB(Vector3 min, Vector3 max) {
		return AxisAlignedBB.getBoundingBox(Math.min(min.x, max.x), Math.min(min.y, max.y), Math.min(min.z, max.z),
				Math.max(max.x, min.x), Math.max(max.y, min.y), Math.max(max.z, min.z));
	}

	/**
	 * Converts a Vector3 to a Minecraft Vec3 object.
	 *
	 * @return A Minecraft Vec3 object.
	 */
	public Vec3 toVec3() {
		return Vec3.createVectorHelper(x, y, z);
	}

	/**
	 * Returns this Vector3 as an NBT Compound.
	 *
	 * @return This Vector3 as an NBT Compound.
	 */
	public NBTTagCompound toNBT() {
		NBTTagCompound comp = new NBTTagCompound();
		comp.setDouble("x", x);
		comp.setDouble("y", y);
		comp.setDouble("z", z);
		return comp;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Vector3{(");
		sb.append(x).append(",");
		sb.append(y).append(",");
		sb.append(z).append(")}");
		return sb.toString();
	}

	/**
	 * Copy the Vector3.
	 *
	 * @return A copy of this Vector3.
	 */
	public Vector3 copy() {
		return new Vector3(x, y, z);
	}

	/**
	 * Adds the specified components to this Vector3, returns a new Vector3
	 * product.
	 *
	 * @param x
	 *            The x-component.
	 * @param y
	 *            The y-component.
	 * @param z
	 *            The z-component.
	 * @return The Vector3 product.
	 */
	public Vector3 add(double x, double y, double z) {
		return new Vector3(this.x + x, this.y + y, this.z + z);
	}

	/**
	 * Adds the specified Vector3 to this Vector3, returns a new Vector3
	 * product.
	 *
	 * @param v
	 *            The foreign Vector3.
	 * @return The Vector3 product.
	 */
	public Vector3 add(Vector3 v) {
		return add(v.x, v.y, v.z);
	}

	/**
	 * Add a specified offset from a ForgeDirection.
	 *
	 * @param direction
	 *            The ForgeDirection to translate.
	 * @return The Vector3 product.
	 */
	public Vector3 add(ForgeDirection direction) {
		return new Vector3(x + direction.offsetX, y + direction.offsetY, z + direction.offsetZ);
	}

	/**
	 * Subtracts the specified components to this Vector3, returns a new Vector3
	 * product.
	 *
	 * @param x
	 *            The x-component.
	 * @param y
	 *            The y-component.
	 * @param z
	 *            The z-component.
	 * @return The Vector3 product.
	 */
	public Vector3 sub(double x, double y, double z) {
		return new Vector3(this.x - x, this.y - y, this.z - z);
	}

	/**
	 * Subtracts the specified Vector3 to this Vector3, returns a new Vector3
	 * product.
	 *
	 * @param v
	 *            The foreign Vector3.
	 * @return The Vector3 product.
	 */
	public Vector3 sub(Vector3 v) {
		return sub(v.x, v.y, v.z);
	}

	/**
	 * Multiplies the components of this Vector3 by the constant c, returns a
	 * new Vector3 multiplication product.
	 *
	 * @param c
	 *            The constant.
	 * @return The Vector3 multiplication product.
	 */
	public Vector3 mul(double c) {
		return new Vector3(c * x, c * y, c * z);
	}

	/**
	 * Divides the components of this Vector3 by the constant c, returns a new
	 * Vector3 division product.
	 *
	 * @param c
	 *            The constant.
	 * @return The Vector3 division product.
	 */
	public Vector3 div(double c) {
		return new Vector3(x / c, y / c, z / c);
	}

	/**
	 * Determine the dot-product of this Vector3 with respect to another
	 * Vector3.
	 *
	 * @param v
	 *            The foreign Vector3 object.
	 * @return The dot product (this.x * that.x + y * that.y + this.z * that.z).
	 */
	public double dot(Vector3 v) {
		return x * v.x + y * v.y + z * v.z;
	}

	/**
	 * Determine the minimum vector components of this Vector3 and another
	 * Vector3, returns a new Vector3 minimum.
	 *
	 * @param v
	 *            The foreign Vector3.
	 * @return The smallest Vector3 object.
	 */
	public Vector3 min(Vector3 v) {
		return new Vector3(Math.min(x, v.x), Math.min(y, v.y), Math.min(z, v.z));
	}

	/**
	 * Determine the maximum vector components of this Vector3 and another
	 * Vector3, returns a new Vector3 maximum.
	 *
	 * @param v
	 *            The foreign Vector3.
	 * @return The largest Vector3 object.
	 */
	public Vector3 max(Vector3 v) {
		return new Vector3(Math.max(x, v.x), Math.max(y, v.y), Math.max(z, v.z));
	}

	/**
	 * Determine the distance between this Vector3 and another Vector3.
	 *
	 * @param v
	 *            The foreign Vector3.
	 * @return The distance between this Vector3 and the foreign Vector3.
	 */
	public double distanceTo(Vector3 v) {
		double dx = x - v.x, dy = y - v.y, dz = z - v.z;
		return Math.sqrt(dx * dx + dy * dy + dz * dz);
	}

	/**
	 * Determines the length of this Vector3.
	 *
	 * @return The length of this Vector3.
	 */
	public double mag() {
		return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));
	}

	/**
	 * Returns the unit vector for this vector.
	 *
	 * @return The unit vector for this vector.
	 */
	public Vector3 unitV() {
		return new Vector3(x / mag(), y / mag(), z / mag());
	}

	/**
	 * Calculates the floored x-component of this Vector3.
	 *
	 * @return The floored x-component of this Vector3.
	 */
	public int fx() {
		return (int) Math.floor(x);
	}

	/**
	 * Calculates the floored y-component of this Vector3.
	 *
	 * @return The floored y-component of this Vector3.
	 */
	public int fy() {
		return (int) Math.floor(y);
	}

	/**
	 * Calculates the floored z-component of this Vector3.
	 *
	 * @return The floored z-component of this Vector3.
	 */
	public int fz() {
		return (int) Math.floor(z);
	}

	/**
	 * Calculates the rounded x-component of this Vector3.
	 *
	 * @return The floored x-component of this Vector3.
	 */
	public int rx() {
		return (int) Math.round(x);
	}

	/**
	 * Calculates the rounded y-component of this Vector3.
	 *
	 * @return The floored y-component of this Vector3.
	 */
	public int ry() {
		return (int) Math.round(y);
	}

	/**
	 * Calculates the rounded z-component of this Vector3.
	 *
	 * @return The floored z-component of this Vector3.
	 */
	public int rz() {
		return (int) Math.round(z);
	}

	/**
	 * Computes the angle pre normal.
	 *
	 * @param mul
	 *            The other Vector3.
	 * @return The angle pre normal.
	 */
	public float anglePNorm(Vector3 mul) {
		return (float) Math.acos(dot(mul));
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Vector3))
			return false;
		Vector3 that = (Vector3) o;
		return x == that.x && y == that.y && z == that.z;
	}
}
