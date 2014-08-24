package lc.common;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

public class LCLog {

	private static Logger log;

	public static void setLogger(Logger log) {
		LCLog.log = log;
	}

	private static void push(Level level, Object[] args) {
		if (args.length == 1) {
			if (args[0] instanceof Throwable)
				log.log(level, (Throwable) args[0]);
			else
				log.log(level, args[0]);
		} else if (args.length == 2) {
			if (args[0] instanceof Throwable)
				log.log(level, (String) args[1], (Throwable) args[0]);
			else
				log.log(level, (String) args[0], (Throwable) args[1]);
		} else {
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

	public static void fatal(Object... args) {
		push(Level.FATAL, args);
	}

	public static void warn(Object... args) {
		push(Level.WARN, args);
	}

	public static void info(Object... args) {
		push(Level.INFO, args);
	}

	public static void debug(Object... args) {
		push(Level.DEBUG, args);
	}

	public static void trace(Object... args) {
		push(Level.TRACE, args);
	}

}
