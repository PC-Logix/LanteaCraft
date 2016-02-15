package lc.common.util.math;

import java.util.EnumSet;
import java.util.Random;

import net.minecraft.util.EnumFacing;

/**
 * Vector orientation map.
 *
 * @author AfterLifeLochie
 *
 */
public enum Orientations {
	/** Not an orientation */
	VOID(null, 0.0f),
	/** Cardinal north */
	NORTH(Matrix3.turnRotations[0], 0.0f),
	/** Cardinal south */
	SOUTH(Matrix3.turnRotations[2], 180.0f),
	/** Cardinal east */
	EAST(Matrix3.turnRotations[1], 270.0f),
	/** Cardinal west */
	WEST(Matrix3.turnRotations[3], 90.0f),
	/** Cardinal northeast */
	NORTHEAST(Matrix3.ident, 45.0f),
	/** Cardinal southeast */
	SOUTHEAST(Matrix3.ident, 135.0f),
	/** Cardinal southwest */
	SOUTHWEST(Matrix3.ident, 225.0f),
	/** Cardinal northwest */
	NORTHWEST(Matrix3.ident, 315.0f),
	/** Facing north-south */
	NORTHSOUTH(Matrix3.ident, 0.0f),
	/** Facing east-west */
	EASTWEST(Matrix3.ident, 90.0f);

	private final Matrix3 rotation;
	private final float angle;

	Orientations(Matrix3 rotation, float angle) {
		this.rotation = rotation;
		this.angle = angle;
	}

	/**
	 * Get the rotation matrix for this Orientation, based around the zero
	 * rotation
	 *
	 * @return The rotation matrix for this Orientation
	 */
	public Matrix3 rotation() {
		return rotation;
	}

	/**
	 * Convert a EnumFacing facing to an Orientation facing
	 *
	 * @param dir
	 *            The source direction
	 * @return The resultant Orientation
	 */
	public static Orientations from(EnumFacing dir) {
		switch (dir) {
		case NORTH:
			return Orientations.NORTH;
		case SOUTH:
			return Orientations.SOUTH;
		case EAST:
			return Orientations.EAST;
		case WEST:
			return Orientations.WEST;
		default:
			return Orientations.VOID;
		}
	}

	/**
	 * @return A list of all cardinal directions
	 */
	public static EnumSet<Orientations> getCardinals() {
		return EnumSet.of(NORTH, EAST, SOUTH, WEST);
	}

	/**
	 * @param r
	 *            The random number generator
	 * @return A random cardinal
	 */
	public static Orientations randomCardinal(Random r) {
		return Orientations.values()[1 + r.nextInt(4)];
	}

	/**
	 * @return A list of all unique rotation cardinals
	 */
	public static EnumSet<Orientations> getFacings() {
		return EnumSet.of(NORTH, EAST);
	}

	/**
	 * @return The angle of an Orientation's rotation
	 */
	public float angle() {
		return angle;
	}

	/**
	 * @return The EnumFacing of this Orientation
	 */
	public EnumFacing forge() {
		switch (this) {
		case NORTH:
			return EnumFacing.NORTH;
		case SOUTH:
			return EnumFacing.SOUTH;
		case EAST:
			return EnumFacing.EAST;
		case WEST:
			return EnumFacing.WEST;
		default:
			return null;
		}
	}

}
