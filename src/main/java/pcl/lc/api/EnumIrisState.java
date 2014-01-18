package pcl.lc.api;

/**
 * Represents the states which the Stargate's Iris may be in.
 * 
 * @author AfterLifeLochie
 * 
 */
public enum EnumIrisState {
	Error, Open, Closed, Opening, Closing, Waiting;

	public static EnumIrisState fromOrdinal(int ordinal) {
		return EnumIrisState.values()[ordinal];
	}
}
