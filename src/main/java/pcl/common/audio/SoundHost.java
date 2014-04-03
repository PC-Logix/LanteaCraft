package pcl.common.audio;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import pcl.lc.LanteaCraft;

public class SoundHost {

	protected final Object owner;
	private HashMap<String, AudioSourceWrapper> sources;
	private ArrayList<String> dead_sources;

	private class AudioSourceWrapper {
		private String filename;
		private AudioPosition position;
		private float volume;

		private AudioSource vsource;
		private int maxAge;
		private int age;
		private boolean running;
		private boolean stopped;

		public AudioSourceWrapper(String filename, AudioPosition position, float volume, int maxAge) {
			this.filename = filename;
			this.position = position;
			this.volume = volume;
			this.maxAge = maxAge;
		}

		public void tick() {
			if (!running || stopped)
				return;
			if (maxAge > 0)
				age++;
			if (age > maxAge)
				stop();
		}

		public boolean alive() {
			return ((0 > maxAge) || (maxAge > age)) && running;
		}

		public boolean canPlay() {
			return !stopped;
		}

		public void setupSource() {
			AudioEngine engine = LanteaCraft.getProxy().getAudioEngine();
			vsource = engine.create(owner, position, filename, false, false, volume);
		}

		public void play() {
			if (vsource == null)
				setupSource();
			vsource.play();
			running = true;
		}

		public void pause() {
			if (vsource != null)
				vsource.pause();
			running = false;
		}

		public void stop() {
			if (vsource != null)
				vsource.stop();
			vsource = null;
			running = false;
			stopped = true;
		}
	}

	public SoundHost(Object owner) {
		this.owner = owner;
		sources = new HashMap<String, AudioSourceWrapper>();
		dead_sources = new ArrayList<String>();
	}

	public void tick() {
		for (Entry<String, AudioSourceWrapper> wrapper : sources.entrySet()) {
			wrapper.getValue().tick();
			if (!wrapper.getValue().canPlay())
				dead_sources.add(wrapper.getKey());
		}
		for (String dead : dead_sources)
			sources.remove(dead);
		dead_sources.clear();
	}

	public void addChannel(String name, String filename, AudioPosition position, float volume, int age) {
		synchronized (sources) {
			sources.put(name, new AudioSourceWrapper(filename, position, volume, age));
		}
	}

	public boolean channelExists(String channel) {
		return sources.containsKey(channel) && sources.get(channel) != null;
	}

	public void playChannel(String channel) {
		AudioSourceWrapper wrapper = sources.get(channel);
		if (wrapper != null)
			wrapper.play();
	}

	public void pauseChannel(String channel) {
		AudioSourceWrapper wrapper = sources.get(channel);
		if (wrapper != null)
			wrapper.pause();
	}

	public void stopChannel(String channel) {
		AudioSourceWrapper wrapper = sources.get(channel);
		if (wrapper != null)
			wrapper.stop();
		sources.remove(channel);
	}

	public void shutdown() {
		for (AudioSourceWrapper wrapper : sources.values())
			wrapper.stop();
		sources.clear();
	}

}
