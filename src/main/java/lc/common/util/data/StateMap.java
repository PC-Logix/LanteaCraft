package lc.common.util.data;

import java.util.HashMap;

public class StateMap {

	private final HashMap<String, Object> state = new HashMap<String, Object>();

	public <T> T get(String index, T def) {
		Object zz = state.get(index);
		if (def.getClass().isAssignableFrom(zz.getClass()) || zz.getClass().isAssignableFrom(def.getClass()))
			return (T) zz;
		return def;
	}

	public <T> T get(String index) {
		return (T) state.get(index);
	}

	public void set(String index, Object value) {
		state.put(index, value);
	}
}
