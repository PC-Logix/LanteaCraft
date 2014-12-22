package lc.server.database;

import lc.common.util.math.DimensionPos;
import net.minecraftforge.event.world.WorldEvent;

import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;

public class UniverseManager {

	/** Default constructor */
	public UniverseManager() {
	}

	/**
	 * Called by the system when a server is being started and a Universe should
	 * be loaded or created.
	 * 
	 * @param event
	 *            The server event.
	 */
	public void loadUniverse(FMLServerStartingEvent event) {
	}

	/**
	 * Called by the system when a server is being stopped and any open Universe
	 * should be saved to disk.
	 * 
	 * @param event
	 *            The server event.
	 */
	public void unloadUniverse(FMLServerStoppingEvent event) {
	}

	/**
	 * Called by the system when a dimension is being loaded and a Galaxy should
	 * be loaded or created.
	 * 
	 * @param load
	 *            The load event.
	 */
	public void loadGalaxy(WorldEvent.Load load) {
	}

	/**
	 * Called by the system when a dimension is being unloaded and a Galaxy
	 * should be saved to disk.
	 * 
	 * @param unload
	 *            The unload event.
	 */
	public void unloadGalaxy(WorldEvent.Unload unload) {

	}

	/**
	 * Called by the system when the dimension is being auto-saved and a Galaxy
	 * should also be auto-saved.
	 * 
	 * @param save
	 *            The save event.
	 */
	public void autosaveGalaxy(WorldEvent.Save save) {

	}

	public char[] getFreeAddress() {
		return null;
	}

	public char[] findAddress(DimensionPos dimensionPos) {
		return null;
	}

}
