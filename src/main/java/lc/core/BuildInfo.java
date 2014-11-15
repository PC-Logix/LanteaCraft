package lc.core;

/**
 * This file is automatically updated by Jenkins as part of the CI build script
 * in Gradle. Don't put any pre-set values here.
 *
 * @author AfterLifeLochie
 */
public class BuildInfo {
	/** Mod name */
	public static final String modName = "LanteaCraft";
	/** Mod ID */
	public static final String modID = "LanteaCraft";

	/** Mod version number */
	public static final String versionNumber = "@VERSION@";
	/** Build number (auto-gen) */
	public static final String buildNumber = "@BUILD@";

	/** The base path for all API requests */
	public static final String webAPI = "http://lanteacraft.com/api/";

	/**
	 * Enable or disable general debugging mode.
	 */
	public static boolean DEBUG;

	/**
	 * Allow DEBUG and TRACE level messages to masquerade as higher priority
	 * ones.
	 */
	public static boolean DEBUG_MASQ;

	private static final boolean IS_DEV_ENV;

	static {
		IS_DEV_ENV = getBuildNumber() == 0;
		DEBUG = true && IS_DEV_ENV;
		DEBUG_MASQ = true && DEBUG;
	}

	/**
	 * Get the build number of the mod
	 *
	 * @return The build number
	 */
	public static int getBuildNumber() {
		try {
			return Integer.parseInt(buildNumber);
		} catch (Throwable t) {
			return 0;
		}
	}

	/**
	 * Get if this build is a development version
	 *
	 * @return If this build is a development version
	 */
	public static boolean isDevelopmentEnvironment() {
		return IS_DEV_ENV;
	}
}