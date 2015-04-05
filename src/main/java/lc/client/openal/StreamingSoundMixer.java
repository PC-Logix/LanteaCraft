package lc.client.openal;

import java.util.HashMap;

import lc.api.audio.channel.IMixer;
import lc.api.audio.streaming.ISound;

public class StreamingSoundMixer implements IMixer {

	private final HashMap<String, ISound> channels;

	public StreamingSoundMixer() {
		this.channels = new HashMap<String, ISound>();
	}

	@Override
	public void createChannel(String name, ISound sound) {
		channels.put(name, sound);
	}

	@Override
	public void deleteChannel(String name) {
		ISound sound = channels.remove(name);
		if (sound != null) {
			sound.stop();
			sound.remove();
		}
	}

	@Override
	public void playChannel(String name) {
		ISound sound = channels.get(name);
		if (sound != null)
			sound.play();
	}

	@Override
	public void pauseChannel(String name) {
		ISound sound = channels.get(name);
		if (sound != null)
			sound.pause();
	}

	@Override
	public void stopChannel(String name) {
		ISound sound = channels.get(name);
		if (sound != null)
			sound.stop();
	}

	@Override
	public boolean shutdown(boolean now) {
		if (!now) {
			for (ISound sound : channels.values())
				if (sound.playing() && sound.realvol() > 0)
					return false;
		}
		for (ISound sound : channels.values())
			sound.remove();
		return true;
	}

	@Override
	public void think() {
		// TODO Auto-generated method stub

	}

}
