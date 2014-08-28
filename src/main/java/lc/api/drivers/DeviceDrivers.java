package lc.api.drivers;

import lc.api.components.IntegrationType;

public class DeviceDrivers {

	public static @interface DriverProvider {
		IntegrationType type();
	}

	public static @interface DriverCandidate {
		IntegrationType type();
	}

}
