//------------------------------------------------------------------------------------------------
//
//   SG Craft - Stargate base tile entity renderer
//
//------------------------------------------------------------------------------------------------

package gcewing.sg;

public class Utils {

	public static double normaliseAngle(double a) {
		a %= 360.0;
		if (a < 0.0)
			a += 360.0;
		return a;
	}

	public static double addAngle(double a, double b) {
		return normaliseAngle(a + b);
	}
	
	public static double diffAngle(double a, double b) {
		// Shortest angular distance from a to b, in range -180 to 180
		double d = (a > b) ? (a - b) : (b - a);
		if (d > 180.0)
			d -= 360.0;
		if (a > b)
			d = -d;
		return d;
	}
	
	public static double relaxAngle(double a, double target, double rate) {
		return addAngle(a, rate * diffAngle(a, target));
	}
	
	public static double interpolateAngle(double a, double b, double t) {
		return addAngle(a, t * diffAngle(a, b));
	}

}
