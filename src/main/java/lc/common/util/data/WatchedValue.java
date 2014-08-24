package lc.common.util.data;

/**
 * Represents a Value which is Observable.
 * 
 * @author AfterLifeLochie
 * 
 * @param <T>
 *            The type of the Value object to contain.
 */
public class WatchedValue<T extends Object> extends Observable {

	/**
	 * The T value reference.
	 */
	private volatile T value;

	/**
	 * The hash-code of the last value.
	 */
	private volatile int last;

	/**
	 * Creates a new WatchedValue container with an initial value of type T.
	 * 
	 * @param initial
	 *            The initial state value.
	 */
	public WatchedValue(T initial) {
		super(null);
		this.value = initial;
	}

	/**
	 * Creates a new WatchedValue container with a parent Observable and an
	 * initial value of type T.
	 * 
	 * @param parent
	 *            The parent Observable object.
	 * @param initial
	 *            The initial state value.
	 */
	public WatchedValue(Observable parent, T initial) {
		super(parent);
		this.value = initial;
	}

	@Override
	public boolean modified(ObserverContext context) {
		if (!context.states.containsKey(hashCode()))
			return true;
		if (context.states.get(hashCode()) != value.hashCode())
			return true;
		return false;
	}

	@Override
	public void clearModified(ObserverContext context) {
		context.states.put(hashCode(), value.hashCode());
	}

	/**
	 * Gets the value of this WatchedValue at the moment of invocation.
	 */
	public T get() {
		return this.value;
	}

	/**
	 * Sets the value of this WatchedValue immediately. This notifies the
	 * Observable that a state change has occurred.
	 */
	public void set(T value) {
		this.last = this.value.hashCode();
		this.value = value;
		modify();
	}

	/**
	 * Clears the value of this WatchedValue immediately. This notifies the
	 * Observable that a state change has occurred.
	 */
	public void reset() {
		this.last = this.value.hashCode();
		this.value = null;
		modify();
	}

	/**
	 * Returns the hash-code of the last value in this WatchedValue. This value
	 * may be {@link null} if the last value was a {@link null} value.
	 */
	public int last() {
		return this.last;
	}

	/**
	 * Determines if the value of type T in this container is equal to that of
	 * the passed Object. If the passed Object is also an instance of
	 * WatchedValue with an unknown type (? extends T), the value of the passed
	 * WatchedValue will be compared.
	 */
	@Override
	public boolean equals(Object o) {
		if (o instanceof WatchedValue<?>)
			return this.get().equals(((WatchedValue<?>) o).get());
		return this.get().equals(o);
	}

	@Override
	public String toString() {
		return new StringBuilder().append("WatchedValue: { ")
				.append((this.get() != null) ? this.get().toString() : "null").append(" }").toString();
	}

}
