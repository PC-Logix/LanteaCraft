package lc.common.util.java;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import lc.common.LCLog;

public class DestructableReferenceQueue {

	private static final DestructableReferenceQueue queue = new DestructableReferenceQueue();
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

	public static void queue(Object o) {
		synchronized (map) {
			map.add(new WeakReference<Object>(o));
		}
	}

	public static void dereference() {
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
