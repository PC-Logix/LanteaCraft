package pcl.common.audio;

/**
 * Core AudioEngine hook; can be called on either side of the game. The instance
 * {@link ClientAudioEngine} has fillers for these methods which actually call
 * real SoundSystem methods.
 * 
 * @author AfterLifeLochie
 */
public class AudioEngine {

	/**
	 * Explicit constructor
	 */
	public AudioEngine() {

	}

	/**
	 * Get the default master volume of the sound-hook.
	 */
	public float getDefaultMasterVolume() {
		return 1.0f;
	}

	/**
	 * Initialize the sound engine.
	 */
	/*
	 * This is a shadow method implementation.
	 */
	public void initialize() {
		// Do nothing.
	}

	/**
	 * Play one sound object with parameters once.
	 * 
	 * @param soundObject
	 *            The sound object.
	 * @param file
	 *            The file.
	 * @param override
	 *            The priority setting.
	 * @param volume
	 *            The volume.
	 */
	/*
	 * This is a shadow method implementation.
	 */
	public void playOnce(AudioSource soundObject, boolean override, float volume) {
		// Do nothing.
	}

	/**
	 * Remove a sound object.
	 * 
	 * @param soundObject
	 *            The object to remove.
	 */
	/*
	 * This is a shadow method implementation.
	 */
	public void remove(Object soundObject) {
		// Do nothing.
	}

	/**
	 * Create a sound object with parameters.
	 * 
	 * @param aref
	 *            The aref.
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
	/*
	 * This is a shadow method implementation.
	 */
	public Object create(Object owner, AudioPosition position, String file, boolean looping, boolean override, float volume) {
		// Do nothing.
		return null;
	}

	/**
	 * Tick the sound engine.
	 */
	/*
	 * This is a shadow method implementation.
	 */
	public void advance() {
		// Do nothing.
	}

}
