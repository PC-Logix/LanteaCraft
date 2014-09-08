package lc.api;

public class LCAPI {

	private static ILCAPIProxy runtime = null;

	public static ILCAPIProxy runtime() {
		if (runtime == null)
			try {
				Class<?> rtc = Class.forName("lc.core.LCRuntime");
				runtime = (ILCAPIProxy) rtc.getField("runtime").get(rtc);
			} catch (Throwable e) {
				return null;
			}
		return runtime;
	}

}
