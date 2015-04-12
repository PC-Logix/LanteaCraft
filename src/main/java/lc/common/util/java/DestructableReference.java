package lc.common.util.java;

import java.lang.ref.WeakReference;

public class DestructableReference<T> {

	private WeakReference<T> ref;

	public DestructableReference(T t) {
		ref = new WeakReference<T>(t);
	}

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
