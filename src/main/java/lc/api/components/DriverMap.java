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
	/** LC internal driver */
	LANTEACRAFT("LanteaCraft", "LanteaCraft", "lc.common.impl.drivers.LanteaCraftPeripheralDriver",
			"lc.common.impl.drivers.LanteaCraftDriverManager", IntegrationType.COMPUTERS),

	/** CC computer driver */
	COMPUTERCRAFT("ComputerCraft", "ComputerCraft", "lc.common.impl.drivers.ComputerCraftPeripheralDriver",
			"lc.common.impl.drivers.ComputerCraftDriverManager", IntegrationType.COMPUTERS),

	/** OC computer driver */
	OPENCOMPUTERS("OpenComputers", "OpenComputers", "lc.common.impl.drivers.OpenComputersEnvironmentDriver",
			"lc.common.impl.drivers.OpenComputersDriverManager", IntegrationType.COMPUTERS)

	/** Waila support driver */
	/*
	 * WAILA("Waila", "Waila", null,
	 * "lc.common.impl.drivers.WailaDriverManager", IntegrationType.UTILITY)
	 */;

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
	 * Try and spin up all required drivers in the system. If a Driver has not
	 * been imported, a Driver manager may not be loaded; this ensures that all
	 * managers which are applicable are booted for usage later on.
	 */
	public static void trySpinUpAll() {
		for (DriverMap mapping : values())
			if (Loader.isModLoaded(mapping.modId))
				try {
					mapping.trySpinUpDriver();
				} catch (Exception e) {
					LCLog.warn("Failed to fallback spin-up needed driver %s (class %s).", mapping.modName,
							mapping.managerClassName);
				}
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
