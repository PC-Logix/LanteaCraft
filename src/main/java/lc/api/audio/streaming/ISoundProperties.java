/**
 * This file is part of the official LanteaCraft API. Please see the usage guide and
 * restrictions on use in the package-info file.
 */
package lc.api.audio.streaming;

import lc.api.audio.SoundPlaybackChannel;

/**
 * Interface for sound property containers at runtime.
 * 
 * @author AfterLifeLochie
 *
 */
public interface ISoundProperties {
	/**
	 * @return If the sound is to loop
	 */
	boolean loop();

	/**
	 * @return If the sound is to override other sounds (that is, if this sound
	 *         has high priority over others)
	 */
	boolean override();

	/**
	 * @return The sound's volume
	 */
	float volume();

	/**
	 * @return The sound's pitch spin value
	 */
	float pitch();

	/**
	 * @return The sound's playback category
	 */
	SoundPlaybackChannel category();

	/**
	 * @param b
	 *            If the sound is to loop
	 */
	void loop(boolean b);

	/**
	 * @param b
	 *            If the sound is to override other sounds (that is, if this
	 *            sound has high priority over others)
	 */
	void override(boolean b);

	/**
	 * @param vol
	 *            The sound's volume
	 */
	void volume(float vol);

	/**
	 * @param pit
	 *            The sound's pitch spin value
	 */
	void pitch(float pit);

	/**
	 * @param cat
	 *            The sound's playback category
	 */
	void category(SoundPlaybackChannel cat);
}
