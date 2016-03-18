package lc.common.util.data;

import java.util.HashMap;
import java.util.Map.Entry;

/**
 * An untyped map of states.
 * 
 * @author AfterLifeLochie
 *
 */
public class StateMap {

	private final HashMap<String, Object> state = new HashMap<String, Object>();

	/**
	 * Read the map for a particular value.
	 * 
	 * @param index
	 *            The index of the value
	 * @param def
	 *            The default value if no value exists in the map
	 * @return The value, or the default value
	 */
	@SuppressWarnings("unchecked")
	public <T> T get(String index, T def) {
		Object zz = state.get(index);
		if (zz == null)
			return def;
		if (def.getClass().isAssignableFrom(zz.getClass()) || zz.getClass().isAssignableFrom(def.getClass()))
			return (T) zz;
		return def;
	}

	/**
	 * Read the map for a particular value, unchecked to the receiver type.
	 * 
	 * @param index
	 *            The index to read
	 * @return The value, or an exception if the value cannot be cast to the
	 *         receiver type.
	 */
	@SuppressWarnings("unchecked")
	public <T> T get(String index) {
		return (T) state.get(index);
	}

	/**
	 * Set a value in the map.
	 * 
	 * @param index
	 *            The index to set
	 * @param value
	 *            The value to set
	 */
	public void set(String index, Object value) {
		state.put(index, value);
	}

	/**
	 * Set all values from a named map into this map.
	 * 
	 * @param values
	 *            The named HashMap
	 */
	public void setAllNamed(HashMap<String, Object> values) {
		for (Entry<String, Object> entry : values.entrySet())
			set(entry.getKey(), entry.getValue());
	}

	/**
	 * Get the raw underlying map in the state.
	 * 
	 * @return The raw map
	 */
	public HashMap<String, Object> raw() {
		return state;
	}
}
