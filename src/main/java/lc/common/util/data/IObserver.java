package lc.common.util.data;

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
	 * @param next
	 *            The new (value = next) value.
	 */
	public void modified(T next);

}
