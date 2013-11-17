package pcl.lc;

import java.util.HashMap;

/**
 * GCESGCompatHelper is the registry driving interop between older versions of
 * the mod (such as Greg's names). This also provides formalized names,
 * particularly considering the coming of 1.7.
 * 
 * DO NOT ALTER UNLESS YOU KNOW EXACTLY WHAT YOU ARE DOING.
 * 
 * @author AfterLifeLochie
 * 
 * @deprecated This file is to be removed when compatibility is broken
 *             completely. It should be replaced with a fully qualified name
 *             host or generator.
 * 
 */
@Deprecated
public class GCESGCompatHelper {

	private static HashMap<String, String> tileEntityRegistry = new HashMap<String, String>();
	private static HashMap<String, String> blockRegistry = new HashMap<String, String>();
	private static HashMap<String, String> itemRegistry = new HashMap<String, String>();

	private static void registerTileEntityMapping(String refName, String master) {
		if (!tileEntityRegistry.containsKey(refName) && !tileEntityRegistry.containsValue(master)) {
			tileEntityRegistry.put(refName, master);
			return;
		}
		throw new IllegalStateException("Cannot register that old name; name or value already exists.");
	}

	private static void registerBlockMapping(String refName, String master) {
		if (!blockRegistry.containsKey(refName) && !blockRegistry.containsValue(master)) {
			blockRegistry.put(refName, master);
			return;
		}
		throw new IllegalStateException("Cannot register that old name; name or value already exists.");
	}

	private static void registerItemMapping(String refName, String master) {
		if (!itemRegistry.containsKey(refName) && !itemRegistry.containsValue(master)) {
			itemRegistry.put(refName, master);
			return;
		}
		throw new IllegalStateException("Cannot register that old name; name or value already exists.");
	}

	public static String getTileEntityMapping(String refName) {
		return tileEntityRegistry.get(refName);
	}

	public static String getBlockMapping(String refName) {
		return blockRegistry.get(refName);
	}

	public static String getItemMapping(String refName) {
		return itemRegistry.get(refName);
	}

	static {
		registerTileEntityMapping("tileEntityRing", "pcl.lc.SGRingTE");
		registerTileEntityMapping("tileEntityBase", "pcl.lc.SGBaseTE");
		registerTileEntityMapping("tileEntityController", "pcl.lc.SGControllerTE");

		registerBlockMapping("blockRing", "pcl.lc.SGRingBlock");
		registerBlockMapping("blockPegasusRing", "pcl.lc.SGPegasusRingBlock");

		registerBlockMapping("blockBase", "pcl.lc.SGBaseBlock");
		registerBlockMapping("blockPegasusBase", "pcl.lc.SGPegasusBaseBlock");

		registerBlockMapping("blockController", "pcl.lc.SGControllerBlock");
		registerBlockMapping("blockPegasusController", "pcl.lc.SGPegasusControllerBlock");

		registerBlockMapping("blockNaquadah", "pcl.lc.NaquadahBlock");
		registerBlockMapping("oreNaquadah", "pcl.lc.NaquadahOreBlock");

		registerItemMapping("itemNaquadah", "pcl.lc.ItemNaquadah");
		registerItemMapping("itemNaquadahIngot", "pcl.lc.ItemNaquadahIngot");
		registerItemMapping("itemCoreCrystal", "pcl.lc.ItemCoreCrystal");
		registerItemMapping("itemControllerCrystal", "pcl.lc.ItemControllerCrystal");
	}

}
