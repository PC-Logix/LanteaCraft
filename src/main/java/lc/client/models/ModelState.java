package lc.client.models;

import java.util.HashMap;

public class ModelState {
	
	private final HashMap<String, Object> state = new HashMap<String, Object>();

	public <T> T get(String index) {
		return (T) state.get(index);
	}

	public void set(String index, Object value) {
		state.put(index, value);
	}
}
