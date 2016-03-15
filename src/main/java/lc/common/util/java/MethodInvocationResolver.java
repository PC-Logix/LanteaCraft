package lc.common.util.java;

/**
 * Method resolving security manager. Used to obtain the caller class through
 * context resolution via the original security manager system. This assumes
 * that no security management is present which prevents the fetching of class
 * contexts.
 * 
 * @author AfterLifeLochie
 *
 */
public class MethodInvocationResolver extends SecurityManager {

	private static final MethodInvocationResolver RR = new MethodInvocationResolver();
	private static final int RR_OFFSET = 3;

	protected Class<?>[] getClassContext() {
		return super.getClassContext();
	}

	public static String[] getCallerClassNames(int callerOffset) {
		Class<?>[] klasses = RR.getClassContext();
		String[] classNames = new String[klasses.length];
		for (int i = 0; i < klasses.length; i++)
			classNames[i] = klasses[i].getName();
		return classNames;
	}

	/**
	 * Get the caller class context on the stack currently.
	 * 
	 * @param callerOffset
	 *            The offset up the stack to read; 0 is the caller class.
	 * @return The caller class
	 */
	public static Class<?> getCallerClass(int callerOffset) {
		return RR.getClassContext()[RR_OFFSET + callerOffset];
	}

	/**
	 * Get the size of the caller class context stack.
	 * 
	 * @return The size of the caller class context stack.
	 */
	public static int getContextSize() {
		return RR.getClassContext().length - RR_OFFSET;
	}

}
