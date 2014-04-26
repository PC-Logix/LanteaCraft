package pcl.common.ai;

import pcl.common.util.Vector3;

/**
 * Any entity which remembers it's home and thus returns there periodically, or
 * likes to wander around it.
 * 
 * @author AfterLifeLochie
 */
public interface IHomingPigeon {
	/**
	 * The dimension of the home.
	 * 
	 * @return The dimension of the home.
	 */
	public int getHomeDimension();

	/**
	 * The location of the home.
	 * 
	 * @return The location of the home.
	 */
	public Vector3 getHomeLocation();
}
