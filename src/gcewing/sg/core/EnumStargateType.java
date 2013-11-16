package gcewing.sg.core;

/**
 * Enum containing all gate types, their ordinal and the name assigned to them.
 * This file includes unannounced gates, called 'RESERVED'. You'll have to wait
 * and see what they are. ;)
 * 
 * @author AfterLifeLochie
 * 
 */
public enum EnumStargateType {

	STANDARD(1, "stargate.standard"), ATLANTIS(2, "stargate.atl"), RESERVED_A(3, "stargate.resv_a"), RESERVED_B(4,
			"stargate.resv_b");

	private final int ordinal;
	private final String name;

	EnumStargateType(int ord, String nm) {
		this.ordinal = ord;
		this.name = nm;
	}

	/**
	 * Gets the unique ordinal for this type.
	 * 
	 * @return The ordinal of this type.
	 */
	public int getOrdinal() {
		return ordinal;
	}

	/**
	 * Gets the unique name for this type.
	 * 
	 * @return The name of this type.
	 */
	public String getName() {
		return name;
	}

}
