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
		StackTraceElement[] trace = Thread.currentThread().getStackTrace();
		int pz = 1;
		StackTraceElement src = trace[pz];
		while (src.getClassName().equals("lc.common.util.Tracer"))
			src = trace[pz++];
		if (what != null)
			Tracer.tracer.traceEnter(z.getClass().getName() + "#" + src.getMethodName() + ": " + what);
		else
			Tracer.tracer.traceEnter(z.getClass().getName() + "#" + src.getMethodName());
	}

	public static void end() {
		Tracer.tracer.traceExit();
	}

	public static HashMap<String, ProfileHistory> history() {
		return Tracer.tracer.history;
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
		public long best = Long.MAX_VALUE;
		public long worst = Long.MIN_VALUE;
		private WindowedArrayList<Long> history;

		public ProfileHistory(int heap) {
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

	private void traceEnter(String blob) {
		long tz = Thread.currentThread().getId();
		if (!labels.containsKey(tz))
			labels.put(tz, new Stack<String>());
		labels.get(tz).push(blob);
		if (!history.containsKey(blob))
			history.put(blob, new ProfileHistory(10));
		history.get(blob).stamp = System.nanoTime();
	}

	private void traceExit() {
		long m0 = System.nanoTime();
		ProfileHistory hist = history.get(labels.get(Thread.currentThread().getId()).pop());
		hist.push(m0 - hist.stamp);
	}

}
