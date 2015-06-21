package lc.server.openal;

import lc.api.audio.ISoundController;
import lc.api.audio.channel.IMixer;
import lc.api.audio.streaming.ISoundPosition;
import lc.api.audio.streaming.ISoundServer;

public class VoidSoundController implements ISoundController {

	public static final VoidSoundController controller = new VoidSoundController();
	public static final VoidSoundServer server = new VoidSoundServer();

	private VoidSoundController() {
		/* !!private */
		server.initialize();
	}

	@Override
	public boolean ready() {
		return false;
	}

	@Override
	public ISoundPosition getPosition(Object object) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ISoundServer getSoundService() {
		return VoidSoundController.server;
	}

	@Override
	public IMixer findMixer(Object key) {
		return null;
	}

}
