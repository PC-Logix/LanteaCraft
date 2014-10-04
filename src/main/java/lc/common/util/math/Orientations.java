package lc.common.util.math;

/**
 * Vector orientation map.
 * 
 * @author AfterLifeLochie
 * 
 */
public enum Orientations {
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

}
