package lc.common.util.math;

/**
 * Represents a facing value in three-dimensions.
 *
 * @author AfterLifeLochie
 */
public class Facing3 {

	/** A yaw angle */
	public double yaw;
	/** A pitch angle */
	public double pitch;

	/**
	 * Create a new Facing value
	 *
	 * @param yaw
	 *            The yaw angle
	 * @param pitch
	 *            The pitch angle
	 */
	public Facing3(double yaw, double pitch) {
		this.yaw = yaw;
		this.pitch = pitch;
	}

}
