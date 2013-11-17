package pcl.lc.config;

import java.util.HashMap;

import pcl.lc.core.EnumStargateType;

public class LanguageHelper {

	private static HashMap<String, String> unlocalizedNameMap = new HashMap<String, String>();
	private static HashMap<String, String> localizedNameMap = new HashMap<String, String>();

	private static void registerLocalizedName(String node, String val) {
		unlocalizedNameMap.put(node, val);
	}

	private static void registerUnlocalizedName(String node, String val) {
		localizedNameMap.put(node, val);
	}

	public static String getUnlocNameForGate(EnumStargateType typeof) {
		return typeof.getName();
	}

	public static String getUnlocNameForGatePart(EnumStargateType typeof, String subof) {
		return new StringBuilder().append(typeof.getName()).append(".").append(subof).toString();
	}

	public static String getLocalNameForGate(EnumStargateType typeof) {
		String unlocName = getUnlocNameForGate(typeof);
		String localName = localizedNameMap.get(unlocName);
		if (localName != null)
			return localName;
		return unlocName;
	}

	public static String getLocalNameForGatePart(EnumStargateType typeof, String subof) {
		String unlocName = getUnlocNameForGatePart(typeof, subof);
		String localName = localizedNameMap.get(unlocName);
		if (localName != null)
			return localName;
		return unlocName;
	}

}
