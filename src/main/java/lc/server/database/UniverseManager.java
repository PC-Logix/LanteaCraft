package lc.server.database;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;

import lc.common.util.math.DimensionPos;
import net.minecraftforge.event.world.WorldEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;

public class UniverseManager {

	private final RecordIO jsonAgent;

	/** Heap for all registered addresses */
	private final ArrayList<StargateRecord> recordHeap = new ArrayList<StargateRecord>();

	/** Dictionary of all records to integer dimensions */
	private final HashMap<Integer, ArrayList<WeakReference<StargateRecord>>> dimensionMap = new HashMap<Integer, ArrayList<WeakReference<StargateRecord>>>();

	/** Default constructor */
	public UniverseManager() {
		jsonAgent = new RecordIO();
	}

	/**
	 * Called by the system when a server is being started.
	 * 
	 * @param event
	 *            The server event.
	 */
	public void loadUniverse(FMLServerStartingEvent event) {
	}

	/**
	 * Called by the system when a server is being stopped.
	 * 
	 * @param event
	 *            The server event.
	 */
	public void unloadUniverse(FMLServerStoppingEvent event) {
	}

	/**
	 * Called by the system when a dimension is being loaded.
	 * 
	 * @param load
	 *            The load event.
	 */
	public void loadGalaxy(WorldEvent.Load load) {
	}

	/**
	 * Called by the system when a dimension is being unloaded.
	 * 
	 * @param unload
	 *            The unload event.
	 */
	public void unloadGalaxy(WorldEvent.Unload unload) {
	}

	/**
	 * Called by the system when the dimension is being auto-saved.
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
