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
	VOID(null),
	/** Cardinal north */
	NORTH(Matrix3.ident),
	/** Cardinal south */
	SOUTH(Matrix3.ident),
	/** Cardinal east */
	EAST(Matrix3.ident),
	/** Cardinal west */
	WEST(Matrix3.ident),
	/** Cardinal northeast */
	NORTHEAST(Matrix3.ident),
	/** Cardinal southeast */
	SOUTHEAST(Matrix3.ident),
	/** Cardinal southwest */
	SOUTHWEST(Matrix3.ident),
	/** Cardinal northwest */
	NORTHWEST(Matrix3.ident),
	/** Facing north-south */
	NORTHSOUTH(Matrix3.ident),
	/** Facing east-west */
	EASTWEST(Matrix3.ident);

	private final Matrix3 rotation;

	Orientations(Matrix3 rotation) {
		this.rotation = rotation;
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
			default: return Orientations.VOID;
		}
	}

	public static EnumSet<Orientations> getCardinals() {
		return EnumSet.of(NORTH, EAST, SOUTH, WEST);
	}

	public static EnumSet<Orientations> getFacings() {
		return EnumSet.of(NORTH, EAST);
	}

}
