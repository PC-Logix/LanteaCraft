package pcl.lc.api;

/**
 * Enum containing all iris types, their ordinal and the name assigned to them.
 * 
 * @author AfterLifeLochie
 * 
 */
public enum EnumIrisType {

	MECHANICAL("iris.mechanical"), ENERGY("iris.energy");

	private final String name;

	EnumIrisType(String name) {
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

	public static EnumIrisType fromOrdinal(int ordinal) {
		return EnumIrisType.values()[ordinal];
	}

}
