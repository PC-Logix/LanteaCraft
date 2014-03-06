package pcl.common.audio;

import net.minecraft.entity.player.EntityPlayer;

public class AudioSource {

	/**
	 * Play the source.
	 */
	public void play() {
	}

	/**
	 * Pause the source.
	 */
	public void pause() {
	}

	/**
	 * Stop the source.
	 */
	public void stop() {
	}

	/**
	 * Remove the source.
	 */
	public void remove() {
	}

	/**
	 * Flush the source.
	 */
	public void flush() {
	}

	/**
	 * Gets the volume of the source.
	 * 
	 * @return The volume of the source.
	 */
	public float getVolume() {
		return 0.0f;
	}

	/**
	 * Sets the volume of the source.
	 * 
	 * @param f
	 *            The volume of the source.
	 */
	public void setVolume(float f) {
	}

	/**
	 * Gets the pitch of the source.
	 * 
	 * @return The pitch of the source.
	 */
	public float getPitch() {
		return 0.0f;
	}

	/**
	 * Sets the pitch of the source.
	 * 
	 * @param f
	 *            The pitch of the source.
	 */
	public void setPitch(float f) {
	}

	/**
	 * Updates the source.
	 * 
	 * @param clientPlayer
	 *            The player.
	 */
	public void advance(EntityPlayer clientPlayer) {
	}

	/**
	 * Activates the source.
	 */
	public void activate() {
	}

	/**
	 * Culls the source.
	 */
	public void cull() {
	}

	/**
	 * Gets the effective playback volume of this source.
	 * 
	 * @return The effective playback volume of this source.
	 */
	public float getRealVolume() {
		return 0.0f;
	}

}
