package lc.api.stargate;

public enum StargateType {

	STANDARD("stargate.standard", ""), ATLANTIS("stargate.pegasus", "pegasus");

	private final String name;
	private final String suffix;

	StargateType(String name, String suffix) {
		this.name = name;
		this.suffix = suffix;
	}

	public String getName() {
		return name;
	}

	public String getSuffix() {
		return suffix;
	}

	public static StargateType fromOrdinal(int ordinal) {
		return StargateType.values()[ordinal];
	}

	public static int count() {
		return values().length;
	}

}
