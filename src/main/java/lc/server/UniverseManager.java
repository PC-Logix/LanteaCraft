package lc.server;

import java.io.File;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Random;

import lc.common.LCLog;
import lc.common.stargate.StargateCharsetHelper;
import lc.common.util.data.ImmutablePair;
import lc.core.BuildInfo;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.world.WorldEvent;

import com.lanteacraft.astrodat.GalaxyFile;
import com.lanteacraft.astrodat.GalaxyFileException;
import com.lanteacraft.astrodat.UniverseFile;
import com.lanteacraft.astrodat.UniverseFileException;
import com.lanteacraft.astrodat.io.GalaxyFileReader;
import com.lanteacraft.astrodat.io.GalaxyFileWriter;
import com.lanteacraft.astrodat.io.UniverseFileReader;
import com.lanteacraft.astrodat.io.UniverseFileWriter;

import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;

public class UniverseManager {

	private final UniverseFileReader universeReader = new UniverseFileReader();
	private final UniverseFileWriter universeWriter = new UniverseFileWriter();
	private final GalaxyFileReader galaxyReader = new GalaxyFileReader();
	private final GalaxyFileWriter galaxyWriter = new GalaxyFileWriter();

	private File registry;
	private UniverseFile universe;
	private HashMap<Integer, ImmutablePair<File, GalaxyFile>> galaxies = new HashMap<Integer, ImmutablePair<File, GalaxyFile>>();
	private HashMap<Integer, GalaxyWrapper> wrappers = new HashMap<Integer, GalaxyWrapper>();

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
		WorldServer overworld = event.getServer().worldServerForDimension(0);
		File cwd = overworld.getSaveHandler().getWorldDirectory();
		registry = new File(cwd, "lanteacraft.uni");
		if (!registry.exists()) {
			universe = new UniverseFile();
			universe.comment = String.format("Created with LanteaCraft build %s.", BuildInfo.versionNumber);
		} else {
			try {
				universe = universeReader.read(registry);
			} catch (UniverseFileException exception) {
				LCLog.fatal("Problem reading universe file.", exception);
				universe = new UniverseFile();
			}
		}
		universe.name = overworld.getWorldInfo().getWorldName();
	}

	/**
	 * Called by the system when a server is being stopped and any open Universe
	 * should be saved to disk.
	 * 
	 * @param event
	 *            The server event.
	 */
	public void unloadUniverse(FMLServerStoppingEvent event) {
		LCLog.debug("Shutting down universe...");
		for (Entry<Integer, ImmutablePair<File, GalaxyFile>> entry : galaxies.entrySet()) {
			try {
				galaxyWriter.write(entry.getValue().getB(), entry.getValue().getA());
			} catch (GalaxyFileException exception) {
				LCLog.fatal("Problem saving galaxy file %s (%s) for dimension %s.", entry.getValue().getA(), entry
						.getValue().getB(), entry.getKey(), exception);
			}
		}
		try {
			universeWriter.write(universe, registry);
		} catch (UniverseFileException exception) {
			LCLog.fatal("Problem saving universe file.", exception);
		}
		galaxies.clear();
		wrappers.clear();
		universe = null;
	}

	/**
	 * Called by the system when a dimension is being loaded and a Galaxy should
	 * be loaded or created.
	 * 
	 * @param load
	 *            The load event.
	 */
	public void loadGalaxy(WorldEvent.Load load) {
		File worldDir = load.world.getSaveHandler().getWorldDirectory();
		if (load.world.provider.getSaveFolder() != null)
			LCLog.info("World save directory: %s", worldDir.getAbsolutePath());
		File galFile = new File(worldDir, "world.gal");
		LCLog.debug("Dimension %s loaded (file: %s)", load.world.provider.dimensionId, galFile);
		GalaxyFile galaxy = null;
		if (!galFile.exists()) {
			LCLog.debug("Creating new descriptor for galaxy file %s.", galFile);
			galaxy = new GalaxyFile();
			galaxy.comment = String.format("Created with LanteaCraft build %s.", BuildInfo.versionNumber);
		} else {
			try {
				galaxy = galaxyReader.read(galFile);
				LCLog.debug("Read descriptor %s from file %s.", galaxy, galFile);
			} catch (GalaxyFileException exception) {
				LCLog.fatal("Problem reading galaxy file %s.", galFile, exception);
				galaxy = new GalaxyFile();
			}
		}
		galaxy.name = load.world.getWorldInfo().getWorldName();
		galaxies.put(load.world.provider.dimensionId, new ImmutablePair<File, GalaxyFile>(galFile, galaxy));
		wrappers.put(load.world.provider.dimensionId, new GalaxyWrapper(this, galaxy));
	}

	/**
	 * Called by the system when a dimension is being unloaded and a Galaxy
	 * should be saved to disk.
	 * 
	 * @param unload
	 *            The unload event.
	 */
	public void unloadGalaxy(WorldEvent.Unload unload) {
		int what = unload.world.provider.dimensionId;
		for (Entry<Integer, ImmutablePair<File, GalaxyFile>> entry : galaxies.entrySet()) {
			if (entry.getKey() == what) {
				try {
					LCLog.debug("Dimension %s unloaded (file: %s)", what, entry.getValue().getA());
					galaxyWriter.write(entry.getValue().getB(), entry.getValue().getA());
				} catch (GalaxyFileException exception) {
					LCLog.fatal("Problem saving galaxy file %s (%s) for dimension %s.", entry.getValue().getA(), entry
							.getValue().getB(), entry.getKey(), exception);
				}
			}
		}
		galaxies.remove(what);
	}

	/**
	 * Called by the system when the dimension is being auto-saved and a Galaxy
	 * should also be auto-saved.
	 * 
	 * @param save
	 *            The save event.
	 */
	public void autosaveGalaxy(WorldEvent.Save save) {
		int what = save.world.provider.dimensionId;
		for (Entry<Integer, ImmutablePair<File, GalaxyFile>> entry : galaxies.entrySet()) {
			if (entry.getKey() == what) {
				try {
					galaxyWriter.write(entry.getValue().getB(), entry.getValue().getA());
				} catch (GalaxyFileException exception) {
					LCLog.fatal("Problem auto-saving galaxy file %s (%s) for dimension %s.", entry.getValue().getA(),
							entry.getValue().getB(), entry.getKey(), exception);
				}
			}
		}
	}

	public char[] getFreeAddress() {
		Random rng = new Random();
		StargateCharsetHelper helper = StargateCharsetHelper.singleton();
		while (true) {
			boolean flag = true;
			char[] next = new char[9];
			for (int i = 0; i < 9; i++)
				next[i] = helper.index(rng.nextInt(helper.radixSize));
			for (GalaxyWrapper wrapper : wrappers.values()) {
				if (wrapper.hasAddress(next)) {
					flag = false;
					break;
				}
			}
			if (flag)
				return next;
		}
	}

}
