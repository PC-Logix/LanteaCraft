package pcl.lc;

import java.util.HashMap;

public class LanteaNameRegistry {

	private static HashMap<String, String> tileEntityRegistry = new HashMap<String, String>();
	private static HashMap<String, String> blockRegistry = new HashMap<String, String>();
	private static HashMap<String, String> itemRegistry = new HashMap<String, String>();

	private static void registerTileEntityMapping(String refName, String master) {
		if (!tileEntityRegistry.containsKey(refName) && !tileEntityRegistry.containsValue(master)) {
			tileEntityRegistry.put(refName, master);
			return;
		}
		throw new IllegalStateException("Cannot register that name; name or value already exists.");
	}

	private static void registerBlockMapping(String refName, String master) {
		if (!blockRegistry.containsKey(refName) && !blockRegistry.containsValue(master)) {
			blockRegistry.put(refName, master);
			return;
		}
		throw new IllegalStateException("Cannot register that name; name or value already exists.");
	}

	private static void registerItemMapping(String refName, String master) {
		if (!itemRegistry.containsKey(refName) && !itemRegistry.containsValue(master)) {
			itemRegistry.put(refName, master);
			return;
		}
		throw new IllegalStateException("Cannot register that name; name or value already exists.");
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
		registerTileEntityMapping("tileEntityRing", "pcl.lc.tileentity.TileEntityStargateRing");
		registerTileEntityMapping("tileEntityBase", "pcl.lc.tileentity.TileEntityStargateBase");
		registerTileEntityMapping("tileEntityController", "pcl.lc.tileentity.TileEntityStargateController");

		registerBlockMapping("blockRing", "pcl.lc.blocks.BlockStargateRing");
		registerBlockMapping("blockBase", "pcl.lc.blocks.BlockStargateBase");
		registerBlockMapping("blockController", "pcl.lc.blocks.BlockStargateController");

		registerBlockMapping("blockNaquadah", "pcl.lc.blocks.BlockNaquadah");
		registerBlockMapping("oreNaquadah", "pcl.lc.blocks.BlockNaquadahOre");

		registerItemMapping("itemNaquadah", "pcl.lc.items.ItemNaquadah");
		registerItemMapping("itemNaquadahIngot", "pcl.lc.items.ItemNaquadahIngot");
		registerItemMapping("itemCoreCrystal", "pcl.lc.items.ItemCoreCrystal");
		registerItemMapping("itemControllerCrystal", "pcl.lc.items.ItemControllerCrystal");
	}

}
