package lc.server.database;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.Semaphore;

import lc.common.stargate.StargateCharsetHelper;
import lc.common.util.data.PrimitiveCompare;
import lc.common.util.math.ChunkPos;
import lc.common.util.math.DimensionPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.world.WorldEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;

public class UniverseManager {

	/** Default Stargate address size */
	private final static int ADDRESS_WIDTH = 9;

	/** Disk IO provider */
	private final RecordIO jsonAgent;
	/** Disk IO mode */
	private final boolean useCompression;

	/** Current working file */
	private File workFile;
	/** The random number generator */
	private Random worldRandom;
	/** Heap for all registered addresses */
	private ArrayList<StargateRecord> recordHeap = new ArrayList<StargateRecord>();
	/** Dictionary of all allocated character maps */
	private ArrayList<Long> characterMap = new ArrayList<Long>();

	/** Default constructor */
	public UniverseManager(boolean useCompression) {
		this.useCompression = useCompression;
		jsonAgent = new RecordIO(this.useCompression);
	}

	/**
	 * Called by the system when a server is being started.
	 * 
	 * @param event
	 *            The server event.
	 */
	public void loadUniverse(FMLServerStartingEvent event) {
		WorldServer overworld = event.getServer().worldServerForDimension(0);
		File cwd = overworld.getSaveHandler().getWorldDirectory();
		File dataDir = new File(cwd, "lanteacraft");
		if (!dataDir.exists())
			dataDir.mkdir();
		
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

	/**
	 * Build the reference cache.
	 */
	public void buildIndex() {
		characterMap.clear();
		for (StargateRecord record : recordHeap)
			characterMap.add(StargateCharsetHelper.singleton().addressToLong(record.address));
	}

	public boolean isAddressAllocated(char[] address) {
		return characterMap.contains(StargateCharsetHelper.singleton().addressToLong(address));
	}

	public char[] putAddress(char[] address, int dimension, ChunkPos chunk) {
		StargateRecord record = new StargateRecord();
		record.address = address;
		record.dimension = dimension;
		record.chunk = chunk;
		recordHeap.add(record);
		characterMap.add(StargateCharsetHelper.singleton().addressToLong(address));
		return record.address;
	}

	public char[] findAddress(int dimension, ChunkPos chunk) {
		for (StargateRecord record : recordHeap) {
			if (record.chunk.equals(chunk))
				return record.address;
		}
		return putAddress(getFreeAddress(ADDRESS_WIDTH), dimension, chunk);
	}

	public char[] getFreeAddress(int width) {
		while (true) {
			char[] address = new char[width];
			for (int i = 0; i < width; i++)
				address[i] = StargateCharsetHelper.singleton().index(worldRandom.nextInt(36));
			if (!isAddressAllocated(address))
				return address;
		}
	}

	public StargateRecord findRecord(char[] address) {
		if (!isAddressAllocated(address))
			return null;
		for (StargateRecord record : recordHeap) {
			if (PrimitiveCompare.compareChar(record.address, address))
				return record;
		}
		return null;
	}

}
