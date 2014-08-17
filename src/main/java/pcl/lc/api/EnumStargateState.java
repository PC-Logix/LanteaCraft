package pcl.lc.api;

/**
 * Represents the states which the Stargate may be in.
 * 
 * @author AfterLifeLochie
 * 
 */
public enum EnumStargateState {
	/** No owned state */
	Idle,
	/** Stargate is waiting for the connection to establish (visual only). */
	PendingTransient,
	/** Stargate has formed unstable wormhole (transient effect). */
	Transient,
	/** Stargate can not connect. */
	Abort,
	/** Stargate is connected and stable. */
	Connected,
	/** Stargate is closing wormhole connection. */
	Disconnecting;

	public static EnumStargateState fromOrdinal(int ordinal) {
		return EnumStargateState.values()[ordinal];
	}

}
