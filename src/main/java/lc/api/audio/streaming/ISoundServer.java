/**
 * This file is part of the official LanteaCraft API. Please see the usage guide and
 * restrictions on use in the package-info file.
 */
package lc.api.audio.streaming;

import net.minecraft.client.audio.SoundCategory;

/**
 * Interface for sound servers at runtime.
 * 
 * @author AfterLifeLochie
 *
 */
public interface ISoundServer {

	/**
	 * Initializes the sound service.
	 */
	void initialize();

	/**
	 * @return If the sound server is ready, initialized and can support audio
	 *         operations.
	 */
	boolean ready();

	/**
	 * Called by the system to cause the sound service to update periodically.
	 * You should not invoke this yourself; the system will manage the updates
	 * automatically.
	 */
	void think();

	/**
	 * Get the master volume of the sound system. All sounds in playback will be
	 * volume-adjusted linearly against the master volume.
	 * 
	 * @return The master volume of the sound system.
	 */
	float master();

	/**
	 * Get the master fall-off distance. Depending on the listner's position,
	 * the fall-off affects the volume of individual sounds in a linear way.
	 * 
	 * @return The fall-off rate of the sound system.
	 */
	float falloff();

	/**
	 * Get the volume of a sound category in the system.
	 * 
	 * @param cat
	 *            The category to query.
	 * @return The volume of the category (normalized 0.0f - 1.0f values).
	 */
	float volume(SoundCategory cat);

	/**
	 * Assign a sound to this sound server. The sound parameters specify an
	 * abstract sound and the server thus returns a formal {@link ISound}
	 * container which is linked to the underlying sound engine of the machine.
	 * 
	 * @param owner
	 *            The owner object of the sound
	 * @param f
	 *            The file name of the sound
	 * @param pos
	 *            The sound's position descriptor
	 * @param props
	 *            The sound's properties
	 * @return An {@link ISound} connector to the underlying sound engine which
	 *         represents the link as a sound stream.
	 */
	ISound assign(Object owner, String f, ISoundPosition pos, ISoundProperties props);

}
