package lc.common.util.java;

public class MethodInvocationResolver extends SecurityManager {

	private static final MethodInvocationResolver RR = new MethodInvocationResolver();
	private static final int RR_OFFSET = 3;

	protected Class[] getClassContext() {
		return super.getClassContext();
	}

	public static Class getCallerClass(int callerOffset) {
		return RR.getClassContext()[RR_OFFSET + callerOffset];
	}

	public static int getContextSize() {
		return RR.getClassContext().length - RR_OFFSET;
	}

}
