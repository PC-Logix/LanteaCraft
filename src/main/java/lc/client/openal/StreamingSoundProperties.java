package lc.client.openal;

import net.minecraft.client.audio.SoundCategory;
import lc.api.audio.ISoundProperties;

public class StreamingSoundProperties implements ISoundProperties {

	private boolean loop, override;
	private float volume, pitch;
	private SoundCategory category;

	public StreamingSoundProperties() {
		this(SoundCategory.MASTER);
	}

	public StreamingSoundProperties(SoundCategory category) {
		this(1.0f, category);
	}

	public StreamingSoundProperties(float volume, SoundCategory category) {
		this(false, volume, category);
	}

	public StreamingSoundProperties(boolean loop, float volume, SoundCategory category) {
		this(loop, false, volume, category);
	}

	public StreamingSoundProperties(boolean loop, boolean override, float volume, SoundCategory category) {
		this(loop, override, volume, 1.0f, category);
	}

	public StreamingSoundProperties(boolean loop, boolean override, float volume, float pitch, SoundCategory category) {
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
	public SoundCategory category() {
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
	public void category(SoundCategory cat) {
		category = cat;
	}

}
