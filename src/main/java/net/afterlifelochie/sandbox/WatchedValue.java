package net.afterlifelochie.sandbox;

public class WatchedValue<T extends Object> extends Observable {

	/**
	 * The T value reference.
	 */
	private volatile T value;

	/**
	 * The hash-code of the last value.
	 */
	private volatile int last;

	public WatchedValue(T initial) {
		super(null);
		this.value = initial;
	}

	public WatchedValue(Observable parent, T initial) {
		super(parent);
		this.value = initial;
	}

	public T get() {
		return this.value;
	}

	public void set(T value) {
		this.last = this.value.hashCode();
		this.value = value;
		this.modify();
	}

	public void reset() {
		this.last = this.value.hashCode();
		this.value = null;
		this.modify();
	}

	public int last() {
		return this.last;
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof WatchedValue)) 
			return false;
		WatchedValue<?> that = (WatchedValue<?>) o;
		return this.get().equals(that.get());
	}

}
