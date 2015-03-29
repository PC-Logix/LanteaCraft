package lc.common;

import lc.BuildInfo;
import lc.common.util.data.AnyPredicate;

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
	private static volatile Logger cmLog;

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

	private static void push(Level level, Object[] args) {
		StackTraceElement[] trackback = Thread.currentThread().getStackTrace();
		Logger log = LCLog.log;
		for (StackTraceElement element : trackback)
			if (element.getClassName().startsWith("lc.coremod")) {
				log = LCLog.cmLog;
				break;
			}
		if (args.length == 1) {
			if (args[0] instanceof Throwable)
				log.log(level, args[0]);
			else
				log.log(level, args[0]);
		} else if (args.length == 2 && args[0] instanceof Throwable)
			log.log(level, (String) args[1], (Throwable) args[0]);
		else if (args.length == 2 && args[1] instanceof Throwable)
			log.log(level, (String) args[0], (Throwable) args[1]);
		else {
			boolean flag = false;
			for (Object arg : args)
				if (arg instanceof Throwable)
					flag = true;
			Object[] format;
			if (!flag) {
				format = new Object[args.length - 1];
				System.arraycopy(args, 1, format, 0, format.length);
				log.log(level, String.format((String) args[0], format));
			} else {
				format = new Object[args.length - 2];
				Throwable t = null;
				for (int i = 1, q = 0; i < args.length; i++)
					if (!(args[i] instanceof Throwable))
						format[q++] = args[i];
					else
						t = (Throwable) args[i];
				if (t != null)
					log.log(level, String.format((String) args[0], format), t);
				else
					log.log(level, String.format((String) args[0], format));
			}
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
		if (BuildInfo.DEBUG)
			push(BuildInfo.DEBUG_MASQ ? Level.INFO : Level.DEBUG, args);
	}

	/**
	 * Push a trace log entry to the log
	 *
	 * @param args
	 *            The log entry arguments
	 */
	public static void trace(Object... args) {
		if (BuildInfo.DEBUG)
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
