package lc.api.drivers;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import lc.api.components.IntegrationType;

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

	@Retention(RetentionPolicy.RUNTIME)
	public static @interface DriverRTCallback {
		String event();
	}

}
