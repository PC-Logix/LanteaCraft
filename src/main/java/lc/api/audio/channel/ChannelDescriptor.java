package lc.api.audio.channel;

import lc.api.audio.streaming.ISoundPosition;
import lc.api.audio.streaming.ISoundProperties;

public class ChannelDescriptor {
	public final String name;
	public final String file;
	public final ISoundPosition position;
	public final ISoundProperties properties;

	public ChannelDescriptor(String name, String file, ISoundPosition position, ISoundProperties properties) {
		this.name = name;
		this.file = file;
		this.position = position;
		this.properties = properties;
	}
}
