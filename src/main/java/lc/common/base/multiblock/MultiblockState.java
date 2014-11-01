package lc.common.base.multiblock;

/**
 * Multi-block state list
 * 
 * @author AfterLifeLochie
 * 
 */
public enum MultiblockState {
	/** No valid state */
	NONE,
	/** Valid formed state */
	FORMED;

	/**
	 * Get a state from an ordinal
	 * 
	 * @param ord
	 *            The ordinal
	 * @return A state, or none if not valid.
	 */
	public static MultiblockState fromOrdinal(int ord) {
		MultiblockState[] states = values();
		if (ord >= states.length || 0 > ord)
			return NONE;
		return states[ord];
	}
}
