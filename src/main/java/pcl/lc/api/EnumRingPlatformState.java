package pcl.lc.api;

public enum EnumRingPlatformState {
	Idle, Connecting, Connected, Transmitting, Disconnecting;

	public static EnumRingPlatformState fromOrdinal(int ordinal) {
		return EnumRingPlatformState.values()[ordinal];
	}

}
