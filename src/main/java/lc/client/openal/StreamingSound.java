package lc.client.openal;

import paulscode.sound.SoundSystem;
import lc.api.audio.ISound;
import lc.api.audio.ISoundPosition;
import lc.api.audio.ISoundProperties;
import lc.api.audio.ISoundServer;

public class StreamingSound implements ISound {

	public StreamingSound(SoundSystem system, ISoundPosition pos, String f, ISoundProperties props, String tag) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void play() {
		// TODO Auto-generated method stub

	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub

	}

	@Override
	public void remove() {
		// TODO Auto-generated method stub

	}

	@Override
	public void cull() {
		// TODO Auto-generated method stub

	}

	@Override
	public void activate() {
		// TODO Auto-generated method stub

	}

	@Override
	public void think(ISoundServer server, Object player) {
		// TODO Auto-generated method stub

	}

	@Override
	public float realvol() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean playing() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean paused() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ISoundProperties properties() {
		// TODO Auto-generated method stub
		return null;
	}

}
