package lc.common.util;

import java.util.HashMap;

import lc.BuildInfo;
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.common.MinecraftForge;

public class StatsProvider {

	public static void generateStats(HashMap<String, String> s) {
		s.put("minecraft-version", MinecraftForge.MC_VERSION.replace(".", "_"));
		s.put("forge-version", ForgeVersion.getVersion());
		s.put("lanteacraft-version", BuildInfo.versionNumber);
		s.put("lanteacraft-build", Integer.toString(BuildInfo.$.build()));
		s.put("lanteacraft-dev-mode", BuildInfo.$.development() ? "true" : "false");
	}

}
