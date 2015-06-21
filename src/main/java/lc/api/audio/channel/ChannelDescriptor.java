package lc.api.audio.channel;

import lc.api.audio.streaming.ISoundProperties;

public class ChannelDescriptor {
	public final String name;
	public final String file;
	public final ISoundProperties properties;

	public ChannelDescriptor(String name, String file, ISoundProperties properties) {
		this.name = name;
		this.file = file;
		this.properties = properties;
	}
}
