package pcl.lc.api;

/**
 * Enum containing all iris types, their ordinal and the name assigned to them.
 * 
 * @author AfterLifeLochie
 * 
 */
public enum EnumIrisType {

	MECHANICAL("iris.mechanical", false), ENERGY("iris.energy", true);

	private final String name;
	private final boolean invulnerable;

	EnumIrisType(String name, boolean invulnerable) {
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
	public boolean getInvulnerability() {
		return invulnerable;
	}

	public static EnumIrisType fromOrdinal(int ordinal) {
		return EnumIrisType.values()[ordinal];
	}

}
