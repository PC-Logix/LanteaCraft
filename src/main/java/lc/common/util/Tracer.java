package lc.common.util;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Stack;
import java.util.concurrent.TimeUnit;

import lc.common.LCLog;
import lc.common.util.data.WindowedArrayList;
import lc.common.util.java.DeferredTaskExecutor;

public class Tracer {

	private static final Tracer tracer = new Tracer();

	public static void begin(Object z) {
		Tracer.begin(z, null);
	}

	public static void begin(Object z, String what) {
		StackTraceElement src = trace();
		if (what != null)
			Tracer.tracer.traceEnter(((z instanceof Class) ? ((Class<?>) z).getName() : z.getClass().getName()) + "#"
					+ src.getMethodName() + ": " + what, src);
		else
			Tracer.tracer.traceEnter(((z instanceof Class) ? ((Class<?>) z).getName() : z.getClass().getName()) + "#"
					+ src.getMethodName(), src);
	}

	public static void end() {
		Tracer.tracer.traceExit();
	}

	public static HashMap<String, ProfileHistory> history() {
		return Tracer.tracer.history;
	}

	private static StackTraceElement trace() {
		StackTraceElement[] trace = Thread.currentThread().getStackTrace();
		int pz = 1;
		StackTraceElement src = trace[pz];
		while (src.getClassName().equals("lc.common.util.Tracer"))
			src = trace[pz++];
		return src;
	}

	public static class ProfilePerformanceWriter implements Runnable {
		private File cwd = new File("./logs/lanteacraft/performance.log");

		@Override
		public void run() {
			try {
				LCLog.debug("Saving profiling data...");
				PrintStream stream = new PrintStream(cwd);
				DecimalFormat df = new DecimalFormat("0", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
				df.setMaximumFractionDigits(340);

				HashMap<String, ProfileHistory> history = (HashMap<String, ProfileHistory>) Tracer.history().clone();
				for (Entry<String, ProfileHistory> record : history.entrySet()) {
					stream.print(record.getKey());
					stream.print(",");
					ProfileHistory histogram = record.getValue();
					stream.print(df.format(histogram.best));
					stream.print(",");
					stream.print(df.format(histogram.avg()));
					stream.print(",");
					stream.print(df.format(histogram.worst));
					stream.println();
				}
				stream.close();
				LCLog.debug("Saved profiling data to disk.");
			} catch (IOException ioex) {
				LCLog.warn("Failed to save profiling data.", ioex);
			}
		}
	}

	public static class ProfileHistory {
		public volatile long stamp;
		public final String sig;
		public long best = Long.MAX_VALUE;
		public long worst = Long.MIN_VALUE;
		private WindowedArrayList<Long> history;

		public ProfileHistory(int heap, String sig) {
			this.sig = sig;
			history = new WindowedArrayList<Long>(heap);
		}

		public void push(long value) {
			if (value > worst)
				worst = value;
			if (best > value)
				best = value;
			synchronized (history) {
				history.add(value);
			}
		}

		public float avg() {
			long sum = 0;
			Long[] iheap;
			synchronized (history) {
				iheap = history.toArray(new Long[0]);
			}
			for (Long lz : iheap)
				sum += lz;
			return (float) sum / ((float) iheap.length);
		}
	}

	private HashMap<String, ProfileHistory> history = new HashMap<String, ProfileHistory>();
	private HashMap<Long, Stack<String>> labels = new HashMap<Long, Stack<String>>();
	private ProfilePerformanceWriter writer = new ProfilePerformanceWriter();

	static {
		DeferredTaskExecutor.scheduleWithFixedDelay(Tracer.tracer.writer, 90, 60, TimeUnit.SECONDS);
	}

	private String makeSignature(StackTraceElement tracer) {
		StringBuilder sig = new StringBuilder();
		sig.append(tracer.getClassName()).append("#").append(tracer.getMethodName());
		return sig.toString();
	}

	private void traceEnter(String blob, StackTraceElement tracer) {
		long tz = Thread.currentThread().getId();
		if (!labels.containsKey(tz))
			labels.put(tz, new Stack<String>());
		labels.get(tz).push(blob);
		if (!history.containsKey(blob))
			history.put(blob, new ProfileHistory(10, makeSignature(tracer)));
		history.get(blob).stamp = System.nanoTime();
	}

	private void traceExit() {
		long m0 = System.nanoTime();
		String key = makeSignature(Tracer.trace());
		long threadId = Thread.currentThread().getId();
		if (labels.get(threadId).size() == 0) {
			LCLog.warn("Tracer: requested trace exit but the trace stack for thread %s is empty.", threadId);
			return;
		}
		String tracer = labels.get(threadId).pop();
		ProfileHistory hist = history.get(tracer);
		if (hist.sig.equals(key)) {
			hist.push(m0 - hist.stamp);
			return;
		} else {
			LCLog.warn("Tracer: detected unclosed trace %s on thread %s from call %s, unwinding stack.", threadId,
					tracer, hist.sig);
			Stack<String> tstack = labels.get(threadId);
			while (tstack.size() > 0) {
				tracer = tstack.peek();
				hist = history.get(tracer);
				if (hist.sig.equals(key)) {
					hist.push(m0 - hist.stamp);
					LCLog.warn("Tracer: unwound the trace stack on thread %s to trace %s from call %s...", threadId,
							tracer, hist.sig);
					return;
				} else {
					LCLog.warn("Tracer: detected nested unclosed trace on thread %s with trace %s from call %s...",
							tracer, hist.sig);
				}
			}
			LCLog.warn("Tracer: fully unwound the trace stack.");
		}
	}

}
