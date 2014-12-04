/**
 * This file is part of the official LanteaCraft API. Please see the usage guide and
 * restrictions on use in the package-info file.
 */
package lc.api.drivers;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import lc.api.components.IntegrationType;

/**
 * Device driver annotations and markers for at-runtime use.
 *
 * @author AfterLifeLochie
 *
 */
public class DeviceDrivers {

	/**
	 * Annotation for classes which act as drivers at runtime.
	 *
	 * @author AfterLifeLochie
	 *
	 */
	@Retention(RetentionPolicy.RUNTIME)
	public static @interface DriverProvider {
		/**
		 * The type of driver this class provides.
		 *
		 * @return The type of driver.
		 */
		IntegrationType type();
	}

	/**
	 * Annotation for classes which require drivers at runtime.
	 *
	 * @author AfterLifeLochie
	 *
	 */
	@Retention(RetentionPolicy.RUNTIME)
	public static @interface DriverCandidate {
		/**
		 * The requested driver types.
		 *
		 * @return A list of requested driver types.
		 */
		IntegrationType[] types();
	}

	/**
	 * Annotation for methods which should be called on internal event firing.
	 *
	 * @author AfterLifeLochie
	 *
	 */
	@Retention(RetentionPolicy.RUNTIME)
	public static @interface DriverRTCallback {
		/**
		 * The event name
		 *
		 * @return The event name
		 */
		String event();
	}

}
