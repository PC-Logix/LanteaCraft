package lc.client.openal;

import lc.api.audio.SoundPlaybackChannel;
import lc.api.audio.streaming.ISoundProperties;

public class StreamingSoundProperties implements ISoundProperties {

	private boolean loop, override;
	private float volume, pitch;
	private SoundPlaybackChannel category;

	public StreamingSoundProperties() {
		this(SoundPlaybackChannel.MASTER);
	}

	public StreamingSoundProperties(SoundPlaybackChannel category) {
		this(1.0f, category);
	}

	public StreamingSoundProperties(float volume, SoundPlaybackChannel category) {
		this(false, volume, category);
	}

	public StreamingSoundProperties(boolean loop, float volume, SoundPlaybackChannel category) {
		this(loop, false, volume, category);
	}

	public StreamingSoundProperties(boolean loop, boolean override, float volume, SoundPlaybackChannel category) {
		this(loop, override, volume, 1.0f, category);
	}

	public StreamingSoundProperties(boolean loop, boolean override, float volume, float pitch,
			SoundPlaybackChannel category) {
		this.loop = loop;
		this.override = override;
		this.volume = volume;
		this.pitch = pitch;
		this.category = category;
	}

	@Override
	public boolean loop() {
		return loop;
	}

	@Override
	public boolean override() {
		return override;
	}

	@Override
	public float volume() {
		return volume;
	}

	@Override
	public float pitch() {
		return pitch;
	}

	@Override
	public SoundPlaybackChannel category() {
		return category;
	}

	@Override
	public void loop(boolean b) {
		loop = b;
	}

	@Override
	public void override(boolean b) {
		override = b;
	}

	@Override
	public void volume(float vol) {
		volume = vol;
	}

	@Override
	public void pitch(float pit) {
		pitch = pit;
	}

	@Override
	public void category(SoundPlaybackChannel cat) {
		category = cat;
	}

}
