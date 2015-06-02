package lc.api.stargate;

public enum IrisType {
	MECHANICAL("mechanical", false), ENERGY("energy", true);

	private final String name;
	private final boolean invulnerable;

	IrisType(String name, boolean invulnerable) {
		this.name = name;
		this.invulnerable = invulnerable;
	}

	/**
	 * Gets the unique name for this type.
	 * 
	 * @return The name of this type.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Determines if this iris type is invulnerable.
	 * 
	 * @return If this iris type is invulnerable.
	 */
	public boolean isInvulnerable() {
		return invulnerable;
	}

	public static IrisType fromOrdinal(int ordinal) {
		return IrisType.values()[ordinal];
	}
}
