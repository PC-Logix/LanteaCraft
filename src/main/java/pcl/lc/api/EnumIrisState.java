package pcl.lc.api;

/**
 * Represents the states which the Stargate's Iris may be in.
 * 
 * @author AfterLifeLochie
 * 
 */
public enum EnumIrisState {
	/** No iris is present. */
	None,
	/** Iris is in error state. */
	Error,
	/** Iris is open */
	Open,
	/** Iris is closed */
	Closed, 
	/** Iris is opening */
	Opening, 
	/** Iris is closing */
	Closing, 
	/** Iris is procrastinating (TODO: what even is this?) */
	Waiting;

	public static EnumIrisState fromOrdinal(int ordinal) {
		return EnumIrisState.values()[ordinal];
	}
}
