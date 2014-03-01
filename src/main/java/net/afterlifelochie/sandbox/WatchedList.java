package net.afterlifelochie.sandbox;

import java.util.HashMap;

public class WatchedList<A, B> extends Observable {

	private HashMap<A, B> values;

	public WatchedList() {
		super(null);
		this.values = new HashMap<A, B>();
	}

	public WatchedList(Observable parent) {
		super(parent);
		this.values = new HashMap<A, B>();
	}
	
	public B get(A key) {
		return values.get(key);
	}
	
	public B set(A key, B value) {
		this.modify();
		return values.put(key, value);
	}
	
	public int size() {
		return values.size();
	}
	
	public B remove(A key) {
		this.modify();
		return values.remove(key);
	}
	
	public void clear() {
		this.modify();
		values.clear();
	}
	

}
