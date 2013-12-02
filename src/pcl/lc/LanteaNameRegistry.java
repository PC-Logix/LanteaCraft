package pcl.lc;

import java.util.HashMap;
import java.util.logging.Level;

public class LanteaNameRegistry {

	private static HashMap<String, String> tileEntityRegistry = new HashMap<String, String>();
	private static HashMap<String, String> blockRegistry = new HashMap<String, String>();
	private static HashMap<String, String> itemRegistry = new HashMap<String, String>();

	private static void registerTileEntityMapping(String refName, String master) {
		if (!tileEntityRegistry.containsKey(refName) && !tileEntityRegistry.containsValue(master)) {
			tileEntityRegistry.put(refName, master);
			return;
		}
		throw new RuntimeException("Cannot register that name; name or value already exists.");
	}

	private static void registerBlockMapping(String refName, String master) {
		if (!blockRegistry.containsKey(refName) && !blockRegistry.containsValue(master)) {
			blockRegistry.put(refName, master);
			return;
		}
		throw new RuntimeException("Cannot register that name; name or value already exists.");
	}

	private static void registerItemMapping(String refName, String master) {
		if (!itemRegistry.containsKey(refName) && !itemRegistry.containsValue(master)) {
			itemRegistry.put(refName, master);
			return;
		}
		throw new RuntimeException("Cannot register that name; name or value already exists.");
	}

	public static String getTileEntityMapping(String refName) {
		LanteaCraft.getLogger().log(Level.INFO, "Request for getTileEntityMapping " + refName);
		String name = tileEntityRegistry.get(refName);
		if (name == null)
			throw new RuntimeException("Cannot find TileEntityMapping with name reference " + refName
					+ ", hard failure!");
		return name;
	}

	public static String getBlockMapping(String refName) {
		LanteaCraft.getLogger().log(Level.INFO, "Request for getBlockMapping " + refName);
		String name = blockRegistry.get(refName);
		if (name == null)
			throw new RuntimeException("Cannot find BlockMapping with name reference " + refName + ", hard failure!");
		return name;
	}

	public static String getItemMapping(String refName) {
		LanteaCraft.getLogger().log(Level.INFO, "Request for getItemMapping " + refName);
		String name = itemRegistry.get(refName);
		if (name == null)
			throw new RuntimeException("Cannot find ItemMapping with name reference " + refName + ", hard failure!");
		return name;
	}

	static {
		registerTileEntityMapping("tileEntityRing", "tileentity.StargateRing");
		registerTileEntityMapping("tileEntityBase", "tileentity.StargateBase");
		registerTileEntityMapping("tileEntityController", "tileentity.StargateController");
		registerTileEntityMapping("tileEntityNaquadahGenerator", "tileentity.NaquadahGenerator");

		registerBlockMapping("blockRing", "block.StargateRing");
		registerBlockMapping("blockBase", "block.StargateBase");
		registerBlockMapping("blockController", "block.StargateController");

		registerBlockMapping("blockNaquadah", "block.Naquadah");
		registerBlockMapping("oreNaquadah", "block.NaquadahOre");
		registerBlockMapping("blockNaquadahGenerator", "block.NaquadahGenerator");

		registerItemMapping("itemNaquadah", "item.Naquadah");
		registerItemMapping("itemNaquadahIngot", "item.NaquadahIngot");
		registerItemMapping("itemCoreCrystal", "item.CoreCrystal");
		registerItemMapping("itemControllerCrystal", "item.ControllerCrystal");
		registerItemMapping("itemTokraSpawnEgg", "item.TokraSpawnEgg");
	}

}
