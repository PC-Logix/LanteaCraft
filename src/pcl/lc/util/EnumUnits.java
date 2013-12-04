package pcl.lc.util;

public enum EnumUnits {

	NaquadahUnit(new String[] { "LanteaCraft" }, 1), MinecraftJoules(new String[] { "BuildCraft", "Railcraft",
			"Forestry" }, 256.0), EnergyUnit(new String[] { "IndustrialCraft2" }, 2048.0), RedstoneFlux(
			new String[] { "Thermal Expansion" }, 128.0);

	private String[] modNames;
	private double ratio;

	EnumUnits(String[] names, double rate) {
		this.modNames = names;
		this.ratio = rate;
	}

	public double getRate() {
		return this.ratio;
	}

	public String[] getUsingMods() {
		return this.modNames;
	}

	/**
	 * Converts a foreign unit of energy to a NaquadahUnit of energy, lossless.
	 * 
	 * @param unitFrom
	 *            The unit to convert from.
	 * @param quantityFrom
	 *            The quantity of the foreign unit.
	 * @return The resulting quantity in terms of NaquadahUnit.
	 */
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
	 */
	public static double convertFromNaquadahUnit(EnumUnits unitTo, double quantityOf) {
		return unitTo.getRate() * quantityOf;
	}

}
