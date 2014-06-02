package net.afterlifelochie.sandbox;

import java.util.HashMap;

public class ObserverContext {

	private static int OBSERVER_COUNTER = 0;

	private static int getUID() {
		if (ObserverContext.OBSERVER_COUNTER - 1 > Integer.MAX_VALUE)
			ObserverContext.OBSERVER_COUNTER = 0;
		return ObserverContext.OBSERVER_COUNTER++;
	}

	private final int id;
	public HashMap<Integer, Integer> states = new HashMap<Integer, Integer>();

	public ObserverContext() {
		id = ObserverContext.getUID();
	}

	@Override
	public int hashCode() {
		return id;
	}

}
