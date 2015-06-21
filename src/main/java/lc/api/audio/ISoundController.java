/**
 * This file is part of the official LanteaCraft API. Please see the usage guide and
 * restrictions on use in the package-info file.
 */
package lc.api.audio;

import lc.api.audio.channel.IMixer;
import lc.api.audio.streaming.ISoundPosition;
import lc.api.audio.streaming.ISoundServer;

/**
 * Interface for sound controller systems at runtime.
 * 
 * @author AfterLifeLochie
 *
 */
public interface ISoundController {

	/**
	 * @return If the sound controller is ready, initialized and can support
	 *         audio operations.
	 */
	boolean ready();

	/**
	 * Derive a sound position from an object
	 * 
	 * @param object
	 *            The object to derive from
	 * @return The sound position, or null if no position can be derived
	 */
	ISoundPosition getPosition(Object object);

	/**
	 * Get the current sound server on the system.
	 * 
	 * @return The current sound server which is running and providing audio
	 *         streams.
	 */
	ISoundServer getSoundService();

	/**
	 * Find a mixer for an object.
	 * 
	 * @param key
	 *            The object key.
	 * @return The mixer associated with an object, or a new mixer if no mixer
	 *         exists for the object specified.
	 */
	IMixer findMixer(Object key);
}
