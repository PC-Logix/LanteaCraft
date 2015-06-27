package lc.common;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;

import lc.BuildInfo;
import lc.api.jit.AnyPredicate;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

/**
 * Logger implementation
 *
 * @author AfterLifeLochie
 *
 */
public class LCLog {

	private static volatile Logger log;
	private static volatile PrintStream printLog;

	private static volatile Logger cmLog;
	private static volatile PrintStream cmPrintLog;

	public static void initPrintLoggers() throws FileNotFoundException {
		File logDir = new File("./logs/lanteacraft/");
		if (!logDir.exists())
			logDir.mkdir();
		printLog = new PrintStream(new File("./logs/lanteacraft/game.log"));
		cmPrintLog = new PrintStream(new File("./logs/lanteacraft/core.log"));
	}

	/**
	 * Set the global logger
	 *
	 * @param log
	 *            The new global logger
	 */
	public static void setLogger(Logger log) {
		LCLog.log = log;
	}

	/**
	 * Set the coremod logger
	 *
	 * @param log
	 *            The new coremod logger
	 */
	public static void setCoremodLogger(Logger log) {
		LCLog.cmLog = log;
	}

	private static boolean isCore() {
		StackTraceElement[] trackback = Thread.currentThread().getStackTrace();
		for (StackTraceElement element : trackback)
			if (element.getClassName().startsWith("lc.coremod"))
				return true;
		return false;
	}

	private static void push(Level level, Object[] args) {
		Logger log = LCLog.log;
		PrintStream stream = LCLog.printLog;
		if (isCore() || LCLog.log == null) {
			log = LCLog.cmLog;
			stream = LCLog.cmPrintLog;
		}

		if (args.length == 1) {
			if (args[0] instanceof Throwable)
				writeAll(log, stream, level, "Uncaught exception, no reason given.", (Throwable) args[0]);
			else
				writeAll(log, stream, level, (args[0] instanceof String) ? (String) args[0] : args[0].toString(), null);
		} else if (args.length == 2 && args[0] instanceof Throwable) {
			writeAll(log, stream, level, (String) args[1], (Throwable) args[0]);
		} else if (args.length == 2 && args[1] instanceof Throwable) {
			writeAll(log, stream, level, (String) args[0], (Throwable) args[1]);
		} else {
			boolean flag = false;
			for (Object arg : args)
				if (arg instanceof Throwable)
					flag = true;
			Object[] format;
			if (!flag) {
				format = new Object[args.length - 1];
				System.arraycopy(args, 1, format, 0, format.length);
				writeAll(log, stream, level, String.format((String) args[0], format), null);
			} else {
				format = new Object[args.length - 2];
				Throwable t = null;
				for (int i = 1, q = 0; i < args.length; i++)
					if (!(args[i] instanceof Throwable))
						format[q++] = args[i];
					else
						t = (Throwable) args[i];
				if (t != null)
					writeAll(log, stream, level, String.format((String) args[0], format), t);
				else
					writeAll(log, stream, level, String.format((String) args[0], format), null);
			}
		}
	}

	private static void writeAll(Logger l, PrintStream s, Level l0, String s0, Throwable z) {
		if (l0 == Level.DEBUG || l0 == Level.TRACE) {
			if (BuildInfo.DEBUG)
				writeLog(l, l0, s0, z);
		} else
			writeLog(l, l0, s0, z);
		writeStream(s, l0, s0, z);
	}

	private static void writeLog(Logger l, Level l0, String s0, Throwable z) {
		if (z != null)
			l.log(l0, s0, z);
		else
			l.log(l0, s0);
	}

	private static void writeStream(PrintStream s, Level l0, String s0, Throwable z) {
		StringBuilder blob = new StringBuilder();
		blob.append("[").append(l0).append("] ");
		blob.append(s0);
		if (z != null) {
			blob.append(": ").append(z.getClass()).append(": ").append(z.getMessage());
			StringWriter writer = new StringWriter();
			z.printStackTrace(new PrintWriter(writer));
			s.println(blob.toString());
			s.println(writer.toString());
		} else {
			s.println(blob.toString());
		}
	}

	/**
	 * Push a fatal log entry to the log
	 *
	 * @param args
	 *            The log entry arguments
	 */
	public static void fatal(Object... args) {
		push(Level.FATAL, args);
	}

	/**
	 * Push a warn log entry to the log
	 *
	 * @param args
	 *            The log entry arguments
	 */
	public static void warn(Object... args) {
		push(Level.WARN, args);
	}

	/**
	 * Push an info log entry to the log
	 *
	 * @param args
	 *            The log entry arguments
	 */
	public static void info(Object... args) {
		push(Level.INFO, args);
	}

	/**
	 * Push a debug log entry to the log
	 *
	 * @param args
	 *            The log entry arguments
	 */
	public static void debug(Object... args) {
		push(BuildInfo.DEBUG_MASQ ? Level.INFO : Level.DEBUG, args);
	}

	/**
	 * Push a trace log entry to the log
	 *
	 * @param args
	 *            The log entry arguments
	 */
	public static void trace(Object... args) {
		push(BuildInfo.DEBUG_MASQ ? Level.INFO : Level.TRACE, args);
	}

	/**
	 * Display the run-time information about the mod.
	 */
	public static void showRuntimeInfo() {
		info("Hello, I'm LanteaCraft build %s version %s (debug: %s).", BuildInfo.$.build(), BuildInfo.versionNumber,
				BuildInfo.DEBUG);
		if (BuildInfo.DEBUG)
			info("Debugging is ON (log masquerading: %s).", BuildInfo.DEBUG_MASQ);
	}

	/**
	 * Perform an assertion. If the condition is not {@code true} the assertion
	 * fails and the application will halt.
	 * 
	 * @param condition
	 *            The assertion condition.
	 * @param params
	 *            The text parts to display on assertion failure.
	 */
	public static void doAssert(boolean condition, Object... params) {
		if (!condition) {
			fatal(params);
			throw new Error("A proxied assertion error in LanteaCraft occurred.");
		}
	}

	/**
	 * Perform a predicate-based assertion. If the predicate does not produce
	 * {@code true} the assertion fails and the application will halt.
	 * 
	 * @param dictate
	 *            The predicate.
	 * @param conditions
	 *            The predicate's functional arguments, if any.
	 * @param params
	 *            The text parts to display on assertion failure.
	 */
	public static void doPredicateAssert(AnyPredicate dictate, Object[] conditions, Object... params) {
		try {
			if (!dictate.test(conditions)) {
				fatal(params);
				throw new Error("A proxied assertion error in LanteaCraft occurred.");
			}
		} catch (Exception e) {
			fatal(params);
			throw new Error("A proxied assertion error in LanteaCraft occurred.");
		}
	}

	/**
	 * Perform a soft assertion. If the condition is not {@code true} the
	 * assertion fails and the application will display a warning message.
	 * 
	 * @param condition
	 *            The assertion condition.
	 * @param params
	 *            The text parts to display on assertion failure.
	 */
	public static void doSoftAssert(boolean condition, Object... params) {
		if (!condition)
			warn(params);
	}

	/**
	 * Perform a soft predicate-based assertion. If the predicate does not
	 * produce {@code true} the assertion fails and the application will display
	 * a warning message.
	 * 
	 * @param dictate
	 *            The predicate.
	 * @param conditions
	 *            The predicate's functional arguments, if any.
	 * @param params
	 *            The text parts to display on assertion failure.
	 */
	public static void doSoftPredicateAssert(AnyPredicate dictate, Object[] conditions, Object... params) {
		try {
			if (!dictate.test(conditions))
				warn(params);
		} catch (Exception e) {
			warn(params);
		}
	}

}
