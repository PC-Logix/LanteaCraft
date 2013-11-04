package gcewing.sg;

import java.util.HashMap;

/**
 * GCESGCompatHelper is the registry driving interop between older versions of
 * the mod (such as Greg's names). This also provides formaizlied names,
 * particularly considering the coming of 1.7.
 * 
 * DO NOT ALTER UNLESS YOU KNOW EXACTLY WHAT YOU ARE DOING.
 * 
 * @author AfterLifeLochie
 * 
 */
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
		registerTileEntityMapping("tileEntityRing", "gcewing.sg.SGRingTE");
		registerTileEntityMapping("tileEntityBase", "gcewing.sg.SGBaseTE");
		registerTileEntityMapping("tileEntityController", "gcewing.sg.SGControllerTE");

		registerBlockMapping("blockRing", "gcewing.sg.SGRingBlock");
		registerBlockMapping("blockBase", "gcewing.sg.SGBaseBlock");
		registerBlockMapping("blockController", "gcewing.sg.SGControllerBlock");
		registerBlockMapping("blockNaquadah", "gcewing.sg.NaquadahBlock");
		registerBlockMapping("oreNaquadah", "gcewing.sg.NaquadahOreBlock");

		registerItemMapping("itemNaquadah", "gcewing.sg.ItemNaquadah");
		registerItemMapping("itemNaquadahIngot", "gcewing.sg.ItemNaquadahIngot");
		registerItemMapping("itemCoreCrystal", "gcewing.sg.ItemCoreCrystal");
		registerItemMapping("itemControllerCrystal", "gcewing.sg.ItemControllerCrystal");
	}

}
