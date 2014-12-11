package lc.server;

import java.io.File;
import java.util.ArrayList;

import lc.common.LCLog;
import net.minecraftforge.event.world.WorldEvent;

import com.lanteacraft.astrodat.GalaxyFile;
import com.lanteacraft.astrodat.UniverseFile;

import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;

public class UniverseManager {

	private File cwd;
	private UniverseFile universe;
	private ArrayList<GalaxyFile> galaxies;

	/** Default constructor */
	public UniverseManager() {
		this.galaxies = new ArrayList<GalaxyFile>();
	}

	/**
	 * Called by the system when a server is being started and a Universe should
	 * be loaded or created.
	 * 
	 * @param event
	 *            The server event.
	 */
	public void loadUniverse(FMLServerStartingEvent event) {
		File tld = event.getServer().worldServerForDimension(0).getSaveHandler().getWorldDirectory();
		LCLog.info("Top world directory: %s", tld);
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

}
