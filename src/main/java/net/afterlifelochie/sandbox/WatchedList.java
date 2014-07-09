package net.afterlifelochie.sandbox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Represents a list with assigned key-value pairs which is Observable. Adding
 * and removing of key-pair values does not guarantee garbage-collection until
 * the key additions and removals have been acknowledged by any Observable
 * observer.
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
	 * The map of all keys added this Observable session. As B values may not be
	 * Observable themselves, we must keep a record of changes from the outside.
	 */
	private ArrayList<A> key_add;
	/**
	 * The map of all keys removed this Observable session. As B values may not
	 * be Observable themselves, we must keep a record of changes from the
	 * outside.
	 */
	private ArrayList<A> key_remove;
	/**
	 * The map of all keys updated this Observable session. As B values may not
	 * be Observable themselves, we must keep a record of changes from the
	 * outside.
	 */
	private ArrayList<A> key_modified;

	/**
	 * Creates a new WatchedList.
	 */
	public WatchedList() {
		super(null);
		this.values = new HashMap<A, B>();
		this.key_add = new ArrayList<A>();
		this.key_remove = new ArrayList<A>();
		this.key_modified = new ArrayList<A>();
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
		this.key_add = new ArrayList<A>();
		this.key_remove = new ArrayList<A>();
		this.key_modified = new ArrayList<A>();
	}

	@Override
	public void clearModified(ObserverContext context) {
		super.clearModified(context);
		this.key_add.clear();
		this.key_remove.clear();
		this.key_modified.clear();
	}

	/**
	 * @see {@link HashMap#get(Object)}
	 */
	public B get(A key) {
		return values.get(key);
	}

	/**
	 * @see {@link HashMap#entrySet()}
	 */
	public Set<Entry<A, B>> entrySet() {
		return values.entrySet();
	}

	/**
	 * @return The iterator of all values.
	 */
	public Iterator<B> values() {
		return values.values().iterator();
	}

	/**
	 * @return The iterator of all keys.
	 */
	public Iterator<A> keys() {
		return values.keySet().iterator();
	}

	/**
	 * @see {@link HashMap#put(Object, Object)}
	 */
	public B set(A key, B value) {
		modify();
		// Operation SET(A, B) will create key A if it does not exist already;
		// reflect this in key_add list if key_add has no such key A.
		if (key_remove.contains(key))
			key_remove.remove(key);
		if (!values.containsKey(key) && !key_add.contains(key))
			key_add.add(key);
		if (values.containsKey(key) && !key_modified.contains(key) && !values.get(key).equals(value))
			key_modified.add(key);
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
		modify();
		// Operation REMOVE(A) will remove key A with value B if key A exists
		// already; reflect this in key_remove list if key_remove has no such
		// key A.
		if (key_add.contains(key))
			key_add.remove(key);
		if (values.containsKey(key) && !key_remove.contains(key))
			key_remove.add(key);
		return values.remove(key);
	}

	/**
	 * @see {@link HashMap#clear()}
	 */
	public void clear() {
		modify();
		// Operation CLEAR() will remove all key A with value B; reflect this in
		// key_remove list if key_remove has no duplicates of each(key A).
		Iterator<A> of = values.keySet().iterator();
		while (of.hasNext()) {
			A next = of.next();
			if (key_add.contains(next))
				key_add.remove(next);
			if (!key_remove.contains(next))
				key_remove.add(next);
		}
		values.clear();
	}

	/**
	 * Gets the history of all key values which have been added this Observable
	 * session.
	 */
	public ArrayList<A> added() {
		return key_add;
	}

	/**
	 * Gets the history of all key values which have been removed this
	 * Observable session.
	 */
	public ArrayList<A> removed() {
		return key_remove;
	}

	/**
	 * Gets the history of all key values which have had their associated value
	 * modified this Observable session.
	 */
	public ArrayList<A> modified() {
		return key_modified;
	}
	
	@Override
	public boolean equals(Object o) {
		return values.equals(o);
	}

}
