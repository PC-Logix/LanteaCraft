package lc.api.components;

import java.util.EnumSet;

import cpw.mods.fml.common.Loader;

public enum DriverMap {
	IC2("IndustrialCraft 2", "IC2", "lc.common.impl.drivers.DriverIC2Power", IntegrationType.POWER), BUILDCRAFT(
			"BuildCraft", "BuildCraft|Core", "lc.common.impl.drivers.DriverBCPower", IntegrationType.POWER);

	public final String modName;
	public final String modId;
	public final String className;
	public final IntegrationType type;

	DriverMap(String modName, String modId, String className, IntegrationType type) {
		this.modName = modName;
		this.modId = modId;
		this.className = className;
		this.type = type;
	}

	public static EnumSet<DriverMap> mapOf(IntegrationType typeof) {
		EnumSet<DriverMap> map = EnumSet.noneOf(DriverMap.class);
		for (DriverMap mapping : values())
			if (mapping.type.equals(typeof) && Loader.isModLoaded(mapping.modId))
				map.add(mapping);
		return map;
	}

}
