package net.afterlifelochie.sandbox;

import java.util.HashMap;

/**
 * Represents a list with assigned key-value pairs which is Observable.
 * 
 * @author AfterLifeLochie
 * 
 * @param <A>
 *            The type A of keys.
 * @param <B>
 *            The type B of values.
 */
public class WatchedList<A, B> extends Observable {

	/**
	 * The map of type A keys with type B values.
	 */
	private HashMap<A, B> values;

	/**
	 * Creates a new WatchedList.
	 */
	public WatchedList() {
		super(null);
		this.values = new HashMap<A, B>();
	}

	/**
	 * Creates a new WatchedList with an Observable parent.
	 * 
	 * @param parent
	 *            The parent Observable object.
	 */
	public WatchedList(Observable parent) {
		super(parent);
		this.values = new HashMap<A, B>();
	}

	/**
	 * @see {@link HashMap#get(Object)}
	 */
	public B get(A key) {
		return values.get(key);
	}

	/**
	 * @see {@link HashMap#put(Object, Object)}
	 */
	public B set(A key, B value) {
		this.modify();
		return values.put(key, value);
	}

	/**
	 * @see {@link HashMap#size()}
	 */
	public int size() {
		return values.size();
	}

	/**
	 * @see {@link HashMap#remove(Object)}
	 */
	public B remove(A key) {
		this.modify();
		return values.remove(key);
	}

	/**
	 * @see {@link HashMap#clear()}
	 */
	public void clear() {
		this.modify();
		values.clear();
	}

}
