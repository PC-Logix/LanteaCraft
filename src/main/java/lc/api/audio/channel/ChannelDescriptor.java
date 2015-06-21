package lc.api.audio.channel;

import lc.api.audio.streaming.ISoundProperties;

/**
 * Represents an OpenAL channel descriptor for use in a Mixer; the descriptor
 * described the file, the name of the channel and the properties of the channel
 * for faster channel generation at runtime.
 * 
 * @author AfterLifeLochie
 *
 */
public class ChannelDescriptor {
	/** The channel name */
	public final String name;
	/** The file name */
	public final String file;
	/** The properties of the sound */
	public final ISoundProperties properties;

	/**
	 * Create a new channel descriptor
	 * 
	 * @param name
	 *            The name of the channel
	 * @param file
	 *            The name of the file on disk
	 * @param properties
	 *            The properties of the channel
	 */
	public ChannelDescriptor(String name, String file, ISoundProperties properties) {
		this.name = name;
		this.file = file;
		this.properties = properties;
	}
}
