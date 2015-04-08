package lc.server.openal;

import lc.api.audio.ISoundController;
import lc.api.audio.channel.IMixer;
import lc.api.audio.streaming.ISoundServer;

public class VoidSoundController implements ISoundController {

	public static final VoidSoundController controller = new VoidSoundController();
	public static final VoidSoundServer server = new VoidSoundServer();

	private VoidSoundController() {
		/* !!private */
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
