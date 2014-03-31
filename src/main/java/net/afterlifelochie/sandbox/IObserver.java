package net.afterlifelochie.sandbox;

/**
 * Observer prototype.
 * 
 * @author AfterLifeLochie
 * 
 * @param <T>
 *            The type to observe.
 */
public interface IObserver<T> {

	/**
	 * Called when the Observable changes.
	 * 
	 * @param last
	 *            The current (value = now) value.
	 * @param next
	 *            The new (value = next) value.
	 */
	public void modified(T last, T next);

}
