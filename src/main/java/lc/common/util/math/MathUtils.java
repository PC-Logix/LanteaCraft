package lc.common.util.math;

/**
 * Math utilities
 *
 * @author AfterLifeLochie
 *
 */
public class MathUtils {

	/**
	 * Normalize an angle between 0.0 and 360.0 degrees
	 *
	 * @param a
	 *            The angle
	 * @return A normalized angle between 0.0 (0PIr) and 360.0 (2PIr) degrees
	 */
	public static double normaliseAngle(double a) {
		a %= 360.0;
		if (a < 0.0)
			a += 360.0;
		return a;
	}

	/**
	 * Add and normalize two angles
	 *
	 * @param a
	 *            An angle
	 * @param b
	 *            An angle
	 * @return The sum of the two angles between 0.0 (0PIr) and 360.0 (2PIr)
	 *         degrees
	 */
	public static double addAngle(double a, double b) {
		return normaliseAngle(a + b);
	}

	/**
	 * Calculate the difference between two angles
	 *
	 * @param a
	 *            An angle
	 * @param b
	 *            An angle
	 * @return The difference in degrees between the two angles.
	 */
	public static double diffAngle(double a, double b) {
		double d = a > b ? a - b : b - a;
		if (d > 180.0)
			d -= 360.0;
		if (a > b)
			d = -d;
		return d;
	}

	/**
	 * Relax an angle towards a target angle at a given rate
	 *
	 * @param a
	 *            The source angle
	 * @param target
	 *            The target angle
	 * @param rate
	 *            The rate of relaxation
	 * @return The new source angle
	 */
	public static double relaxAngle(double a, double target, double rate) {
		return addAngle(a, rate * diffAngle(a, target));
	}

	/**
	 * Interpolate two angles
	 *
	 * @param a
	 *            The source angle
	 * @param b
	 *            The target angle
	 * @param t
	 *            The time in ticks, t
	 * @return The new source angle
	 */
	public static double interpolateAngle(double a, double b, double t) {
		return addAngle(a, t * diffAngle(a, b));
	}

}
