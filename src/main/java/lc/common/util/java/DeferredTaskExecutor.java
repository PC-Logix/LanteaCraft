package lc.common.util.java;

import java.util.concurrent.Callable;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import lc.common.LCLog;

public class DeferredTaskExecutor {

	private static final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(2, new ThreadFactory() {
		@Override
		public Thread newThread(Runnable r) {
			Thread t0 = new Thread(r, "LanteaCraft DTE thread");
			t0.setDaemon(true);
			t0.setPriority(Thread.MIN_PRIORITY);
			return t0;
		}
	}, new RejectedExecutionHandler() {
		@Override
		public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
			LCLog.warn("Failed to execute object in DeferredTaskExecutor: %s.", r);
		}
	});

	public static <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
		LCLog.debug("Scheduling deferred: %s, %s %s", callable, delay, unit);
		return (ScheduledFuture<V>) executor.schedule(callable, delay, unit);
	}

	public static ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
		LCLog.debug("Scheduling deferred: %s, %s %s", command, delay, unit);
		return (ScheduledFuture<?>) executor.schedule(command, delay, unit);
	}

	public static ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay,
			TimeUnit unit) {
		LCLog.debug("Scheduling repeated deferred: %s, %s %s => %s %s", command, initialDelay, unit, delay, unit);
		return (ScheduledFuture<?>) executor.scheduleWithFixedDelay(command, initialDelay, delay, unit);
	}

}
