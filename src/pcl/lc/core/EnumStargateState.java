// ------------------------------------------------------------------------------------------------
//
// SG Craft - Stargate state
//
// ------------------------------------------------------------------------------------------------

package pcl.lc.core;

public enum EnumStargateState {
	Idle, Dialling, Transient, Connected, Disconnecting, InterDialling;

	static EnumStargateState[] VALUES = values();

	public static EnumStargateState getStateFromOrdinal(int id) {
		try {
			return VALUES[id];
		} catch (IndexOutOfBoundsException e) {
			return Idle;
		}
	}

}
