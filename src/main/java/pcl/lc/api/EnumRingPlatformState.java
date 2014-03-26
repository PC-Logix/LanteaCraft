package pcl.lc.api;

public enum EnumRingPlatformState {

	/** Platform is idle. */
	Idle,
	/** Platform is connecting to other platform. */
	Connecting,
	/** Platform has connected to other platform. */
	Connected,
	/** Platform is transmitting entities or data to other platform. */
	Transmitting,
	/** Platform is disconnecting from other platform. */
	Disconnecting;

	public static EnumRingPlatformState fromOrdinal(int ordinal) {
		return EnumRingPlatformState.values()[ordinal];
	}

}
