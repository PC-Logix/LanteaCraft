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

	STANDARD("stargate.standard", ""), ATLANTIS("stargate.pegasus", "pegasus"), WRAITH("stargate.wraith", "wraith"), NOX(
			"stargate.nox", "nox");

	private final String name;
	private final String suffix;

	EnumStargateType(String name, String suffix) {
		this.name = name;
		this.suffix = suffix;
	}

	/**
	 * Gets the unique name for this type.
	 * 
	 * @return The name of this type.
	 */
	public String getName() {
		return name;
	}

	public String getSuffix() {
		return suffix;
	}

	public static EnumStargateType fromOrdinal(int ordinal) {
		return EnumStargateType.values()[ordinal];
	}

}
