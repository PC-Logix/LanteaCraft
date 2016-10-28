package lc.api.stargate;

/**
 * Iris state enumeration
 * 
 * @author AfterLifeLochie
 *
 */
public enum IrisState {
	/** No iris */
	NONE,
	/** Iris is open */
	OPEN,
	/** Iris is closed */
	CLOSED,
	/** Iris is opening */
	OPENING,
	/** Iris is closing */
	CLOSING;

	/**
	 * Find an iris state from an ordinal
	 * 
	 * @param ordinal
	 *            The ordinal value
	 * @return The iris state
	 */
	public static IrisState fromOrdinal(int ordinal) {
		return IrisState.values()[ordinal];
	}
}
