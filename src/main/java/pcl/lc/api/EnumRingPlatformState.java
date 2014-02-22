package pcl.lc.api;

public enum EnumRingPlatformState {
	Idle, Connecting, Transmitting, Recieveing, Disconnecting;

	public static EnumRingPlatformState fromOrdinal(int ordinal) {
		return EnumRingPlatformState.values()[ordinal];
	}

}
