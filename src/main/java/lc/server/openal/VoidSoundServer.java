package lc.server.openal;

import net.minecraft.client.audio.SoundCategory;
import lc.api.audio.streaming.ISound;
import lc.api.audio.streaming.ISoundPosition;
import lc.api.audio.streaming.ISoundProperties;
import lc.api.audio.streaming.ISoundServer;

public class VoidSoundServer implements ISoundServer {

	@Override
	public void initialize() {
		/* Do nothing */
	}

	@Override
	public void think() {
		/* Do nothing */
	}

	@Override
	public float master() {
		throw new RuntimeException("Cannot query VoidSoundServer, operation not allowed.");
	}

	@Override
	public float falloff() {
		throw new RuntimeException("Cannot query VoidSoundServer, operation not allowed.");
	}

	@Override
	public float volume(SoundCategory cat) {
		throw new RuntimeException("Cannot query VoidSoundServer, operation not allowed.");
	}

	@Override
	public ISound assign(Object owner, String f, ISoundPosition pos, ISoundProperties props) {
		throw new RuntimeException("Cannot query VoidSoundServer, operation not allowed.");
	}

}
