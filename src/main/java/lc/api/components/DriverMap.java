/**
 * This file is part of the official LanteaCraft API. Please see the usage guide and
 * restrictions on use in the package-info file.
 */
package lc.api.components;

import java.util.EnumSet;

import lc.common.LCLog;
import cpw.mods.fml.common.Loader;

/**
 * Driver list and wrapper class
 *
 * @author AfterLifeLochie
 *
 */
public enum DriverMap {
	/** CC computer driver */
	COMPUTERCRAFT("ComputerCraft", "ComputerCraft", "lc.common.impl.drivers.ComputerCraftPeripheralDriver",
			IntegrationType.COMPUTERS);

	/** The target mod name */
	public final String modName;
	/** The target mod ID */
	public final String modId;
	/** The target class */
	public final String className;
	/** The target type */
	public final IntegrationType type;

	DriverMap(String modName, String modId, String className, IntegrationType type) {
		this.modName = modName;
		this.modId = modId;
		this.className = className;
		this.type = type;
	}

	@Override
	public String toString() {
		return "DriverMapping{ mod: " + modName + " (" + modId + "): " + className + ", type " + type + " }";
	}

	/**
	 * Get a map of all drivers for a type of integration
	 *
	 * @param typeof
	 *            The type of drivers
	 * @return A list of drivers which should be loaded
	 */
	public static EnumSet<DriverMap> mapOf(IntegrationType typeof) {
		EnumSet<DriverMap> map = EnumSet.noneOf(DriverMap.class);
		for (DriverMap mapping : values())
			if (mapping.type.equals(typeof))
				if (Loader.isModLoaded(mapping.modId))
					map.add(mapping);
				else
					LCLog.debug("Not loading driver %s", mapping);
		return map;
	}

}
