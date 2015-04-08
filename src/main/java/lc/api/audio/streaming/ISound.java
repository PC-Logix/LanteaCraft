/**
 * This file is part of the official LanteaCraft API. Please see the usage guide and
 * restrictions on use in the package-info file.
 */
package lc.api.audio.streaming;

/**
 * Contract interface for objects which are to act as sound-streams.
 * 
 * @author AfterLifeLochie
 *
 */
public interface ISound {

	/**
	 * Play the sound. If the sound is already playing, do nothing.
	 */
	void play();

	/**
	 * Pause the sound. If the sound is not playing, do nothing. If the sound is
	 * already paused, do nothing.
	 */
	void pause();

	/**
	 * Stop the sound. If the sound is not playing, do nothing.
	 */
	void stop();

	/**
	 * Remove the sound from the system. If the sound is already removed, do
	 * nothing.
	 */
	void remove();

	/**
	 * Cull the sound. If the sound is already culled, do nothing.
	 */
	void cull();

	/**
	 * Activate the sound. If the sound is already activated, do nothing.
	 */
	void activate();

	/**
	 * Called by the sound server to update the sound.
	 * 
	 * @param server
	 *            The owning sound server
	 * @param player
	 *            The game's player
	 */
	void think(ISoundServer server, Object player);

	/**
	 * Get the real effective volume of the sound, after distance or other
	 * falloff computation
	 * 
	 * @return The real effective volume of the sound
	 */
	float realvol();

	/**
	 * @return If the sound is currently playing
	 */
	boolean playing();

	/**
	 * @return If the sound is currently paused
	 */
	boolean paused();

	/**
	 * @return The sound properties container for this sound
	 */
	ISoundProperties properties();

}
