/**
 * This file is part of the official LanteaCraft API. Please see the usage guide and
 * restrictions on use in the package-info file.
 */
package lc.api.audio.channel;

import lc.api.audio.streaming.ISound;

public interface IMixer {

	/**
	 * Create a channel on the sound mixer. The channel consists of a name and a
	 * sound stream. The sound stream should not be reassigned once a channel is
	 * assigned a sound.
	 * 
	 * @param name
	 *            The name of the channel
	 * @param sound
	 *            The sound stream
	 */
	void createChannel(String name, ISound sound);

	/**
	 * Delete a channel on the sound mixer. If the channel is currently active,
	 * the channel is stopped. The underlying sound stream is garbage collected
	 * and removed from the sound server which allocated it.
	 * 
	 * @param name
	 *            The name of the channel
	 */
	void deleteChannel(String name);

	/**
	 * Plays the specified channel. Has no effect if the channel is already
	 * playing, is paused or is killed.
	 * 
	 * @param name
	 *            The sound channel
	 * @return This mixer
	 */
	IMixer playChannel(String name);

	/**
	 * Replays the specified channel. If the channel is currently playing, the
	 * channel is re-wound to the start and played again; else the sound is
	 * played again.
	 * 
	 * @param name
	 *            The sound channel
	 * @return This mixer
	 */
	IMixer replayChannel(String name);

	/**
	 * Pauses the specified channel. Has no effect if the channel is already
	 * paused, stopped or is killed.
	 * 
	 * @param name
	 *            The sound channel
	 * @return This mixer
	 */
	IMixer pauseChannel(String name);

	/**
	 * Stops the specified channel. Has no effect if the channel is already
	 * stopped or is killed.
	 * 
	 * @param name
	 *            The sound channel
	 * @return This mixer
	 */
	IMixer stopChannel(String name);

	/**
	 * Shuts down the mixer with an optional force flag.
	 * 
	 * If the force flag is not set and there are channels playing, the mixer is
	 * not shut down until all the sounds have finished playing. If more sounds
	 * are added to the mixer or sounds are replayed, the mixer will not halt.
	 * The mixer will not wait for looping sounds to finish.
	 * 
	 * If the force flag is set and any channels are active, those channels are
	 * stopped regardless of the state of the sound. The mixer is immediately
	 * shut down.
	 * 
	 * All channels on the mixer are disposed as per
	 * {@link IMixer#deleteChannel(String)}.
	 * 
	 * @param now
	 *            The force-shutdown flag
	 * @return If the mixer was shut down.
	 */
	boolean shutdown(boolean now);

	/**
	 * Called by the system to perform update logic on the mixer. You should not
	 * call this yourself; the mixer manager will do this for you.
	 */
	void think();

}
