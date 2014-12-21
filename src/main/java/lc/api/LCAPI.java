/**
 * This file is part of the official LanteaCraft API. Please see the usage guide and
 * restrictions on use in the package-info file.
 */
package lc.api;

/**
 * <p>
 * LanteaCraft's main API access class. Use this class to get a reference to the
 * LanteaCraft mod at runtime.
 * </p>
 * <p>
 * <b>Do not use reflection or other methods to access the runtime instance of
 * the mod.</b>
 * </p>
 *
 * @author AfterLifeLochie
 *
 */
public class LCAPI {

	/** Runtime cache container */
	private static ILCAPIProxy runtime = null;

	/**
	 * <p>
	 * Fetch the current LanteaCraft API runtime element. If LanteaCraft has
	 * been loaded by FML, this will return the current API runtime access
	 * instance. If LanteaCraft has not been loaded, this will return null.
	 * </p>
	 *
	 * <p>
	 * <b>Do not use reflection or other methods to access the runtime instance
	 * of the mod.</b>
	 * </p>
	 *
	 * @return The current LanteaCraft API runtime.
	 */
	public static ILCAPIProxy runtime() {
		if (runtime == null)
			try {
				Class<?> rtc = Class.forName("lc.LCRuntime");
				runtime = (ILCAPIProxy) rtc.getField("runtime").get(rtc);
			} catch (Throwable e) {
				return null;
			}
		return runtime;
	}

}
