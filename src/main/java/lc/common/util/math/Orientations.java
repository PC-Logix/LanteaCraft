package lc.common.util.math;

import java.util.EnumSet;

import net.minecraftforge.common.util.ForgeDirection;

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
	EAST(Matrix3.turnRotations[1], 90.0f),
	/** Cardinal west */
	WEST(Matrix3.turnRotations[3], 270.0f),
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

	public Matrix3 rotation() {
		return rotation;
	}

	public static Orientations from(ForgeDirection dir) {
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

	public static EnumSet<Orientations> getCardinals() {
		return EnumSet.of(NORTH, EAST, SOUTH, WEST);
	}

	public static EnumSet<Orientations> getFacings() {
		return EnumSet.of(NORTH, EAST);
	}

	public float angle() {
		return angle;
	}

}
