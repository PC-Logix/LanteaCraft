//------------------------------------------------------------------------------------------------
//
//   SG Craft - Stargate state
//
//------------------------------------------------------------------------------------------------

package gcewing.sg.core;

public enum EnumStargateState {
	Idle, Dialling, Transient, Connected, Disconnecting, InterDialling;

	static EnumStargateState[] VALUES = values();

	public static EnumStargateState valueOf(int i) {
		try {
			return VALUES[i];
		} catch (IndexOutOfBoundsException e) {
			return Idle;
		}
	}

}
