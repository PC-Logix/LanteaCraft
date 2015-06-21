package lc.client.openal;

import java.util.HashMap;

import lc.api.audio.ISoundController;
import lc.api.audio.channel.ChannelDescriptor;
import lc.api.audio.channel.IMixer;
import lc.api.audio.streaming.ISound;
import lc.common.util.java.DestructableReference;

public class StreamingSoundMixer implements IMixer {

	private final ISoundController controller;
	private final DestructableReference<Object> owner;
	private final HashMap<String, ISound> channels;
	private final HashMap<String, ChannelDescriptor> descriptors;
	private boolean dead;

	public StreamingSoundMixer(ISoundController controller, Object owner) {
		this.controller = controller;
		this.owner = new DestructableReference<Object>(owner);
		this.channels = new HashMap<String, ISound>();
		this.descriptors = new HashMap<String, ChannelDescriptor>();
	}

	@Override
	public void createChannelDescriptor(String name, ChannelDescriptor descriptor) {
		descriptors.put(name, descriptor);
	}

	@Override
	public void createChannel(String name, ISound sound) {
		channels.put(name, sound);
	}

	@Override
	public boolean hasChannel(String name) {
		return (channels.containsKey(name) && channels.get(name) != null);
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
		channels.clear();
		return true;
	}

	@Override
	public void think() {
		if (dead && shutdown(false))
			dead = false;
		Object oz = owner.get();
		if (oz != null) {
			for (ChannelDescriptor descriptor : descriptors.values())
				if (!hasChannel(descriptor.name)) {
					ISound what = controller.getSoundService().assign(oz, descriptor.file, controller.getPosition(oz),
							descriptor.properties);
					if (what != null)
						createChannel(descriptor.name, what);
				}
		}
	}
}
