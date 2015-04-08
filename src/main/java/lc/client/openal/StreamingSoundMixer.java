package lc.client.openal;

import java.util.HashMap;

import lc.api.audio.channel.IMixer;
import lc.api.audio.streaming.ISound;

public class StreamingSoundMixer implements IMixer {

	private final HashMap<String, ISound> channels;
	private boolean dead;

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
	public IMixer playChannel(String name) {
		ISound sound = channels.get(name);
		if (sound != null)
			sound.play();
		return this;
	}

	@Override
	public IMixer replayChannel(String name) {
		ISound sound = channels.get(name);
		if (sound != null) {
			sound.stop();
			sound.play();
		}
		return this;
	}

	@Override
	public IMixer pauseChannel(String name) {
		ISound sound = channels.get(name);
		if (sound != null)
			sound.pause();
		return this;
	}

	@Override
	public IMixer stopChannel(String name) {
		ISound sound = channels.get(name);
		if (sound != null)
			sound.stop();
		return this;
	}

	@Override
	public boolean shutdown(boolean now) {
		dead = true;
		if (!now) {
			for (ISound sound : channels.values()) {
				if (sound.properties().loop())
					continue;
				if (sound.playing() && sound.realvol() > 0)
					return false;
			}
		}
		for (ISound sound : channels.values())
			sound.remove();
		return true;
	}

	@Override
	public void think() {
		if (dead)
			shutdown(false);
	}

}
