package pcl.lc.api;

/**
 * Enum containing all gate types, their ordinal and the name assigned to them.
 * This file includes unannounced gates, called 'RESERVED'. You'll have to wait
 * and see what they are. ;)
 * 
 * @author AfterLifeLochie
 * 
 */
public enum EnumStargateType {

	/** Standard Stargate type. */
	STANDARD("stargate.standard"),
	/** Atlantis Stargate type. */
	ATLANTIS("stargate.atl"),
	/** Reserved for future expansion. */
	RESERVED_A("stargate.resv_a"),
	/** Reserved for future expansion. */
	RESERVED_B("stargate.resv_b");

	private final String name;

	EnumStargateType(String name) {
		this.name = name;
	}

	/**
	 * Gets the unique name for this type.
	 * 
	 * @return The name of this type.
	 */
	public String getName() {
		return name;
	}

	public static EnumStargateType fromOrdinal(int ordinal) {
		return EnumStargateType.values()[ordinal];
	}

}
