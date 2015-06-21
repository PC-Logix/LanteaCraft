package lc.api.stargate;

/**
 * Iris type list.
 * 
 * @author AfterLifeLochie
 *
 */
public enum IrisType {
	/** Mechanical */
	MECHANICAL("mechanical", false),
	/** Energy */
	ENERGY("energy", true);

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

	/**
	 * Gets the Iris type from an ordinal
	 * 
	 * @param ordinal
	 *            The ordinal
	 * @return The type
	 */
	public static IrisType fromOrdinal(int ordinal) {
		return IrisType.values()[ordinal];
	}
}
