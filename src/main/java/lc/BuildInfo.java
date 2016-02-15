package lc;

import java.security.CodeSource;
import java.security.cert.Certificate;

import net.minecraftforge.fml.common.CertificateHelper;
import lc.api.IModInfo;
import lc.common.resource.ResourceAccess;

/**
 * This file is automatically updated by Jenkins as part of the CI build script
 * in Gradle. Don't put any pre-set values here.
 *
 * @author AfterLifeLochie
 */
public class BuildInfo implements IModInfo {
	/** Mod name */
	public static final String modName = "LanteaCraft";
	/** Mod ID */
	public static final String modID = "LanteaCraft";

	/** Mod certificate value */
	public static final String CERT = "".replace(":", "");

	/** Mod version number */
	public static final String versionNumber = "@VERSION@";
	/** Build number (auto-gen) */
	public static final String buildNumber = "@BUILD@";

	/** The base path for all API requests */
	public static final String webAPI = "http://lanteacraft.com/api/";

	/** Info instance */
	public static final BuildInfo $;

	/**
	 * Enable or disable general debugging mode.
	 */
	public static boolean DEBUG;

	/**
	 * Allow DEBUG and TRACE level messages to masquerade as higher priority
	 * ones.
	 */
	public static boolean DEBUG_MASQ;

	/**
	 * If this is a development environment
	 */
	private static final boolean IS_DEV_ENV;

	/**
	 * If the code source has been signed.
	 */
	public static final boolean IS_SIGNED;

	static {
		$ = new BuildInfo();
		IS_DEV_ENV = $.build() == 0;
		IS_SIGNED = $.signed();
		$.init();
	}

	private BuildInfo init() {
		DEBUG = true && IS_DEV_ENV;
		DEBUG_MASQ = true && DEBUG;
		return this;
	}

	private boolean signed() {
		try {
			Class<? extends BuildInfo> me = this.getClass();
			CodeSource source = me.getProtectionDomain().getCodeSource();
			Certificate[] certs = source.getCertificates();
			if (certs == null || certs.length <= 0)
				return false;
			return CertificateHelper.getFingerprint(certs[0]).equalsIgnoreCase(CERT);
		} catch (Throwable t) {
			return false;
		}
	}

	@Override
	public int build() {
		try {
			return Integer.parseInt(buildNumber);
		} catch (Throwable t) {
			return 0;
		}
	}

	@Override
	public boolean development() {
		return IS_DEV_ENV;
	}

	@Override
	public String assets() {
		return ResourceAccess.getAssetKey();
	}
}