package pcl.lc;

/**
 * This file is automatically updated by Jenkins as part of the CI build script
 * in Ant. Don't put any pre-set values here.
 * 
 * @author AfterLifeLochie
 */
public class BuildInfo {
	public static final String modName = "LanteaCraft";
	public static final String modID = "LanteaCraft";

	public static final String versionNumber = "@VERSION@";
	public static final String buildNumber = "@BUILD@";

	public static int getBuildNumber() {
		if (buildNumber.equals("@" + "BUILD" + "@"))
			return 0;
		return Integer.parseInt(buildNumber);
	}

	public static boolean isDevelopmentEnvironment() {
		return getBuildNumber() == 0;
	}

	public static final String webAPI = "http://lanteacraft.com/api/";

}
