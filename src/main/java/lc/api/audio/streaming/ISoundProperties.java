package lc.api.audio.streaming;

import net.minecraft.client.audio.SoundCategory;

public interface ISoundProperties {
	boolean loop();

	boolean override();

	float volume();

	float pitch();

	SoundCategory category();

	void loop(boolean b);

	void override(boolean b);

	void volume(float vol);

	void pitch(float pit);

	void category(SoundCategory cat);
}
