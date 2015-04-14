package lc.common.util.java;

import java.util.concurrent.Callable;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import lc.common.LCLog;

/**
 * Deferred server task management system.
 * 
 * @author AfterLifeLochie
 *
 */
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

	/**
	 * Schedule a Callable task to be performed at a Future. The Callable task
	 * will be invoked by the executor after the time period has elapsed. The
	 * execution of the task is not guaranteed at the specified time if the
	 * executor has no available processing threads with which to execute on.
	 * 
	 * @param callable
	 *            The callable object
	 * @param delay
	 *            The delay value
	 * @param unit
	 *            The unit of the delay
	 * @return A ScheduledFuture for the future invocation of the Callable
	 */
	public static <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
		LCLog.debug("Scheduling deferred: %s, %s %s", callable, delay, unit);
		return (ScheduledFuture<V>) executor.schedule(callable, delay, unit);
	}

	/**
	 * Schedule a Runnable task to be performed at a Future. The Runnable task
	 * will be invoked by the executor after the time period has elapsed. The
	 * execution of the task is not guaranteed at the specified time if the
	 * executor has no available processing threads with which to execute on.
	 * 
	 * @param command
	 *            The runnable object
	 * @param delay
	 *            The delay value
	 * @param unit
	 *            The unit of the delay
	 * @return A ScheduledFuture for the future invocation of the Runnable
	 */
	public static ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
		LCLog.debug("Scheduling deferred: %s, %s %s", command, delay, unit);
		return (ScheduledFuture<?>) executor.schedule(command, delay, unit);
	}

	/**
	 * Schedule a Runnable task to be performed at a Future with interval. The
	 * Runnable task will be invoked by the executor once after the initial
	 * delay, then recurring every delay frequency afterward. The execution of
	 * the task is not guaranteed at a specified time if the executor has no
	 * available processing threads with which to execute on.
	 * 
	 * @param command
	 *            The runnable object
	 * @param initialDelay
	 *            The initial wait-to-run delay value
	 * @param delay
	 *            The recurring-delay value
	 * @param unit
	 *            The unit of the delays
	 * @return A ScheduledFuture for the future invocations of the Runnable
	 */
	public static ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay,
			TimeUnit unit) {
		LCLog.debug("Scheduling repeated deferred: %s, %s %s => %s %s", command, initialDelay, unit, delay, unit);
		return (ScheduledFuture<?>) executor.scheduleWithFixedDelay(command, initialDelay, delay, unit);
	}

}
