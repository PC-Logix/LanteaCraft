package lc.common.util.java;

import java.lang.ref.WeakReference;

/**
 * DestructableReference is a container for a managed WeakReference type. The
 * DestructableReference uses the DestructableReferenceQueue to determine if an
 * object has been queued and is therefore considered enqueued for the garbage
 * collector at some future stage. This allows objects to elect to have
 * themselves dereferenced procedurally, rather than by the garbage collector
 * arbitarily.
 * 
 * @param <T>
 *            The type of the reference.
 * 
 * @author AfterLifeLochie.
 * 
 */
public class DestructableReference<T> {

	private WeakReference<T> ref;

	/**
	 * Create a new DestructableReference to an object.
	 * 
	 * @param t
	 *            The object to reference.
	 */
	public DestructableReference(T t) {
		ref = new WeakReference<T>(t);
	}

	/**
	 * Get the value of the reference. If the object has been garbage collected,
	 * enqueued by the garbage collector or has been virtually disposed, this
	 * method will return null.
	 * 
	 * @return The object reference, or null if the reference has expired.
	 */
	public T get() {
		T t = ref.get();
		if (t == null || DestructableReferenceQueue.queued(t))
			return null;
		return t;
	}

	@Override
	public int hashCode() {
		return ref.hashCode();
	}

}
