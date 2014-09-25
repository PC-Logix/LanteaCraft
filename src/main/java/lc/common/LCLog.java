package lc.common;

import lc.core.BuildInfo;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

/**
 * Logger implementation
 * 
 * @author AfterLifeLochie
 * 
 */
public class LCLog {

	private static Logger log;
	private static Logger cmLog;

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
		for (int i = 0; i < trackback.length; i++)
			if (trackback[i].getClassName().startsWith("lc.coremod")) {
				log = LCLog.cmLog;
				break;
			}
		if (args.length == 1) {
			if (args[0] instanceof Throwable)
				log.log(level, (Throwable) args[0]);
			else
				log.log(level, args[0]);
		} else if (args.length == 2 && args[0] instanceof Throwable)
			log.log(level, (String) args[1], (Throwable) args[0]);
		else if (args.length == 2 && args[1] instanceof Throwable)
			log.log(level, (String) args[0], (Throwable) args[1]);
		else {
			boolean flag = false;
			for (int i = 0; i < args.length; i++)
				if (args[i] instanceof Throwable)
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
			push((BuildInfo.DEBUG_MASQ) ? Level.INFO : Level.DEBUG, args);
	}

	/**
	 * Push a trace log entry to the log
	 * 
	 * @param args
	 *            The log entry arguments
	 */
	public static void trace(Object... args) {
		if (BuildInfo.DEBUG)
			push((BuildInfo.DEBUG_MASQ) ? Level.INFO : Level.TRACE, args);
	}

	/**
	 * Display the run-time information about the mod.
	 */
	public static void showRuntimeInfo() {
		info("Loading LanteaCraft build %s (debugging: %s, masq: %s).", BuildInfo.getBuildNumber(), BuildInfo.DEBUG,
				BuildInfo.DEBUG_MASQ);
	}

}
