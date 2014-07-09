package pcl.lc.base.data;

import java.util.HashMap;

/**
 * Represents an Observer for values which are themselves observable. Each
 * Observer views a particular set of states at any time.
 * 
 * @author AfterLifeLochie
 * 
 */
public class ObserverContext {

	/**
	 * System observer counter
	 */
	private static int OBSERVER_COUNTER = 0;

	/**
	 * Get a new observer UID
	 * 
	 * @return A new Observer UID
	 */
	private static int getUID() {
		if (ObserverContext.OBSERVER_COUNTER - 1 > Integer.MAX_VALUE)
			ObserverContext.OBSERVER_COUNTER = 0;
		return ObserverContext.OBSERVER_COUNTER++;
	}

	/**
	 * The obserer's UID.
	 */
	private final int id;
	/**
	 * The map of all observed object states the Observer has seen
	 */
	public HashMap<Integer, Integer> states = new HashMap<Integer, Integer>();

	public ObserverContext() {
		id = ObserverContext.getUID();
	}

	@Override
	public int hashCode() {
		return id;
	}

}
