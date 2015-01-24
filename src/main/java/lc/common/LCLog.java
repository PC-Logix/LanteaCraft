package lc.common;

import lc.BuildInfo;

import org.apache.commons.lang3.StringUtils;
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
		Throwable t = null;
		for (Object arg : args)
			if (arg instanceof Throwable)
				t = (Throwable) arg;
		
		Object[] format;
		if (t == null) {
			log.log(level, StringUtils.join(args, " "));
		}
		else {
			format = new Object[args.length - 1];
			for (int i = 0, q = 0; i < args.length; i++)
				if (!(args[i] instanceof Throwable))
					format[q++] = args[i];
			log.log(level, StringUtils.join(format, " "), t);
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

}
