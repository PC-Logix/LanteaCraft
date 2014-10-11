package lc.common.base.multiblock;

public enum MultiblockState {
	NONE, FORMED;

	public static MultiblockState fromOrdinal(int ord) {
		MultiblockState[] states = values();
		if (ord >= states.length || 0 > ord)
			return NONE;
		return states[ord];
	}
}
