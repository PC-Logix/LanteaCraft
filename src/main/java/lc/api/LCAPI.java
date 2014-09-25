package lc.api;

/**
 * LanteaCraft API access class
 * 
 * @author AfterLifeLochie
 * 
 */
public class LCAPI {

	/** Runtime container */
	private static ILCAPIProxy runtime = null;

	/**
	 * Fetch the current LanteaCraft API runtime element.
	 * 
	 * @return The current LanteaCraft API runtime.
	 */
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
