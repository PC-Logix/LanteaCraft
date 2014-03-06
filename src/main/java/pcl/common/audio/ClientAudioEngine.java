package pcl.common.audio;

import net.minecraft.client.Minecraft;
import paulscode.sound.SoundSystem;
import paulscode.sound.SoundSystemConfig;

public class ClientAudioEngine extends AudioEngine {

	public final float falloffDistance = 22.0F;
	private final int maxStreamingSources = 4;

	public boolean enabled = true;
	public int maxSources = 32;
	private float masterVolume = 0.5F;
	private SoundSystem system = null;

	public ClientAudioEngine() {
		super();
	}

	/**
	 * Initialize the sound engine.
	 */
	@Override
	public void initialize() {
		SoundSystemConfig.setNumberStreamingChannels(maxStreamingSources);
		SoundSystemConfig.setNumberNormalChannels(maxSources - maxStreamingSources);
	}

	/**
	 * Play one sound object once.
	 * 
	 * @param soundObject
	 *            The sound object.
	 * @param file
	 *            The file.
	 */
	@Override
	public void playOnce(Object soundObject, String file) {
		// Do nothing.
	}

	/**
	 * Play one sound object with parameters once.
	 * 
	 * @param soundObject
	 *            The sound object.
	 * @param positionObject
	 *            The position object.
	 * @param file
	 *            The file.
	 * @param override
	 *            The priority setting.
	 * @param volume
	 *            The volume.
	 */
	@Override
	public void playOnce(Object soundObject, Object positionObject, String file, boolean override, float volume) {
		// Do nothing.
	}

	/**
	 * Remove a sound object.
	 * 
	 * @param soundObject
	 *            The object to remove.
	 */
	@Override
	public void remove(Object soundObject) {
		// Do nothing.
	}

	/**
	 * Create a sound object.
	 * 
	 * @param aref
	 *            The aref.
	 * @param file
	 *            The file.
	 * @return A sound object.
	 */
	@Override
	public Object create(Object aref, String file) {
		// Do nothing.
		return null;
	}

	/**
	 * Create a sound object with parameters.
	 * 
	 * @param aref
	 *            The aref.
	 * @param positionObject
	 *            The position object.
	 * @param file
	 *            The file.
	 * @param looping
	 *            The looping setting.
	 * @param override
	 *            The priority setting.
	 * @param volume
	 *            The volume.
	 * @return A sound object.
	 */
	@Override
	public Object create(Object aref, Object positionObject, String file, boolean looping, boolean override,
			float volume) {
		// Do nothing.
		return null;
	}

	/**
	 * Tick the sound engine.
	 */
	@Override
	public void advance() {
		if (!enabled || system == null)
			return;
		
	}
}
