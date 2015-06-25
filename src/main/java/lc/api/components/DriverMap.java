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
			"lc.common.impl.drivers.ComputerCraftDriverManager", IntegrationType.COMPUTERS);

	/** The target mod name */
	public final String modName;
	/** The target mod ID */
	public final String modId;
	/** The target class */
	public final String className;
	/** The driver manager class */
	public final String managerClassName;
	/** The driver manager object, if any */
	public Object managerObject;
	/** The target type */
	public final IntegrationType type;

	DriverMap(String modName, String modId, String className, IntegrationType type) {
		this.modName = modName;
		this.modId = modId;
		this.className = className;
		this.managerClassName = null;
		this.type = type;
	}

	DriverMap(String modName, String modId, String className, String managerClassName, IntegrationType type) {
		this.modName = modName;
		this.modId = modId;
		this.className = className;
		this.managerClassName = managerClassName;
		this.type = type;
	}

	@Override
	public String toString() {
		return "DriverMapping{ mod: " + modName + " (" + modId + "): " + className + ", type " + type + " }";
	}

	/**
	 * Called by the system to spin up the driver. If the driver has already
	 * been spun up, no effect is performed.
	 * 
	 * @throws Exception
	 *             Any exception which occurs when initializing the driver.
	 */
	public void trySpinUpDriver() throws Exception {
		if (managerClassName == null || managerObject != null)
			return;
		Class<?> clazz = Class.forName(managerClassName);
		managerObject = clazz.newInstance();
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
