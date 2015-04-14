package lc.common.util.java;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import lc.common.LCLog;

/**
 * <p>
 * DestructableReferenceQueue represents a container for procedurally
 * dereferencing objects on demand. It is primarily used by
 * DestructableReference to assert if the object underlying the reference should
 * be dereferenced by calls.
 * </p>
 * <p>
 * DestructableReferenceQueue keeps a collection of all objects in the queue in
 * WeakReference instances. Because DestructableReference keeps the underlying
 * object in a WeakReference as well, the queue only needs to be purged when the
 * real garbage collector collects the references.
 * </p>
 * 
 * @author AfterLifeLochie
 *
 */
public class DestructableReferenceQueue {

	private static final ArrayList<WeakReference<Object>> map = new ArrayList<WeakReference<Object>>();

	private Runnable gc = new Runnable() {
		@Override
		public void run() {
			LCLog.debug("Performing reference garbage collection...");
			DestructableReferenceQueue.dereference();
			LCLog.debug("Reference garbage collection completed.");
		}
	};

	private DestructableReferenceQueue() {
		/* !!private */
		DeferredTaskExecutor.scheduleWithFixedDelay(gc, 240, 300, TimeUnit.SECONDS);
	}

	/**
	 * Ask the reference queue if it has seen the instance of the Object
	 * specified. If the queue does have an instance of the Object, true is
	 * returned; else false is returned.
	 * 
	 * @param o
	 *            The object
	 * @return If the reference queue has enqueued this object virtually
	 */
	public static boolean queued(Object o) {
		synchronized (map) {
			for (WeakReference<Object> obj : map) {
				if (obj.get() == null)
					continue;
				if (obj.get() == o)
					return true;
			}
			return false;
		}
	}

	/**
	 * Queue an object with the reference queue so that it is flagged as
	 * virtually disposed. The object is placed immutably into the reference
	 * queue so that subsequent calls to check the queue indicate this object
	 * has been marked as enqueued.
	 * 
	 * @param o
	 *            The object to enqueue
	 */
	public static void queue(Object o) {
		synchronized (map) {
			map.add(new WeakReference<Object>(o));
		}
	}

	private static void dereference() {
		synchronized (map) {
			Iterator<WeakReference<Object>> refs = map.iterator();
			while (refs.hasNext()) {
				WeakReference<Object> obj = refs.next();
				if (obj.get() == null)
					refs.remove();
			}
		}
	}

}
