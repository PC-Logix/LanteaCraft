package pcl.lc.api;

/**
 * Represents the states which the Stargate may be in.
 * 
 * @author AfterLifeLochie
 * 
 */
public enum EnumStargateState {
	Idle, Dialling, Transient, Connected, Disconnecting, InterDialling;

	public static EnumStargateState fromOrdinal(int ordinal) {
		return EnumStargateState.values()[ordinal];
	}

}
