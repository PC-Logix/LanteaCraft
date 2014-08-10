package pcl.lc.api;

public enum EnumUnits {

	/** Reference unit. */
	NaquadahUnit(new String[] { "LanteaCraft" }, 1),
	/** Minecraft Joules (BuildCraft/Railcraft/Forestry/etc). */
	MinecraftJoules(new String[] { "BuildCraft", "Railcraft", "Forestry" }, 4375.0),
	/** Energy Unit (IndustrialCraft2 and derivatives). */
	EnergyUnit(new String[] { "IndustrialCraft2" }, 1800.0),
	/** Restone Flux (Thermal Expansion). */
	RedstoneFlux(new String[] { "Thermal Expansion" }, 8192.0),
	/** Univeral Amperes (Universal Electricity). */
	UniversalAmperes(new String[] { "Universal Electricity" }, 100.0);

	private String[] modNames;
	private double ratio;

	EnumUnits(String[] names, double rate) {
		modNames = names;
		ratio = rate;
	}

	public double getRate() {
		return ratio;
	}

	public String[] getUsingMods() {
		return modNames;
	}

	/**
	 * Converts a foreign unit of energy to a NaquadahUnit of energy, lossless.
	 * 
	 * @param unitFrom
	 *            The unit to convert from.
	 * @param quantityFrom
	 *            The quantity of the foreign unit.
	 * @return The resulting quantity in terms of NaquadahUnit.
	 * @deprecated To be replaced by a MathHelper in order to make balancing
	 *             actually not be shit.
	 */

	@Deprecated
	public static double convertToNaquadahUnit(EnumUnits unitFrom, double quantityFrom) {
		return (1 / unitFrom.getRate()) * quantityFrom;
	}

	/**
	 * Converts a NaquadahUnit to a foreign unit of energy, lossless.
	 * 
	 * @param unitTo
	 *            The unit to convert to.
	 * @param quantityOf
	 *            The quantity NaquadahUnit.
	 * @return The resulting quantity in terms of the foreign unit.
	 * @deprecated To be replaced by a MathHelper in order to make balancing
	 *             actually not be shit.
	 */
	@Deprecated
	public static double convertFromNaquadahUnit(EnumUnits unitTo, double quantityOf) {
		return unitTo.getRate() * quantityOf;
	}

}
