package gcewing.sg.core;

public enum EnumStargateType {

	STANDARD(1, "stargate.standard"), ATLANTIS(2, "stargate.atl"), RESERVED_A(3, "stargate.resv_a"), RESERVED_B(4,
			"stargate.resv_b");

	private final int ordinal;
	private final String name;

	EnumStargateType(int ord, String nm) {
		this.ordinal = ord;
		this.name = nm;
	}

	public int getOrdinal() {
		return ordinal;
	}

	public String getName() {
		return name;
	}

}
