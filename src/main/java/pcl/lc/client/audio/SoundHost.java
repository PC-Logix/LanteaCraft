package pcl.lc.client.audio;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.apache.logging.log4j.Level;

import pcl.lc.BuildInfo;
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
		private boolean stopped;

		public AudioSourceWrapper(String filename, AudioPosition position, float volume, int maxAge) {
			this.filename = filename;
			this.position = position;
			this.volume = volume;
			this.maxAge = maxAge;
		}

		public void tick() {
			if (!playing() || stopped)
				return;
			if (maxAge > 0)
				age++;
			if (age > maxAge)
				stop();
		}

		public boolean alive() {
			return ((0 > maxAge) || (maxAge > age)) && playing();
		}

		public boolean canPlay() {
			return !stopped;
		}

		public void setupSource() {
			AudioEngine engine = LanteaCraft.getProxy().getAudioEngine();
			vsource = engine.create(owner, position, filename, 0 > age, false, volume);
		}

		public void play() {
			if (vsource == null)
				setupSource();
			if (vsource.isPlaying())
				vsource.stop();
			vsource.play();
		}

		public boolean playing() {
			return vsource != null && vsource.isPlaying();
		}

		public void pause() {
			if (vsource != null)
				vsource.pause();
		}

		public void stop() {
			if (vsource != null)
				vsource.stop();
			vsource = null;
			stopped = true;
		}

		public void shutdown() {
			if (vsource != null) {
				vsource.stop();
				vsource.remove();
			}
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
		if (BuildInfo.SS_DEBUGGING)
			LanteaCraft.getLogger().log(Level.INFO,
					String.format("SoundHost operation: ADD %s %s %s", filename, volume, age));
		synchronized (sources) {
			sources.put(name, new AudioSourceWrapper(filename, position, volume, age));
		}
	}

	public boolean channelExists(String channel) {
		return sources.containsKey(channel) && sources.get(channel) != null;
	}

	public void playChannel(String channel) {
		if (BuildInfo.SS_DEBUGGING)
			LanteaCraft.getLogger().log(Level.INFO, String.format("SoundHost operation: PLAY %s", channel));
		AudioSourceWrapper wrapper = sources.get(channel);
		if (wrapper != null) {
			if (wrapper.playing())
				wrapper.stop();
			wrapper.play();
		}
	}

	public void pauseChannel(String channel) {
		if (BuildInfo.SS_DEBUGGING)
			LanteaCraft.getLogger().log(Level.INFO, String.format("SoundHost operation: PAUSE %s", channel));
		AudioSourceWrapper wrapper = sources.get(channel);
		if (wrapper != null)
			wrapper.pause();
	}

	public void stopChannel(String channel) {
		if (BuildInfo.SS_DEBUGGING)
			LanteaCraft.getLogger().log(Level.INFO, String.format("SoundHost operation: STOP %s", channel));
		AudioSourceWrapper wrapper = sources.get(channel);
		if (wrapper != null)
			wrapper.stop();
		sources.remove(channel);
	}

	public void shutdown(boolean force) {
		if (!force)
			for (AudioSourceWrapper wrapper : sources.values())
				if (wrapper.playing())
					return;
		if (BuildInfo.SS_DEBUGGING)
			LanteaCraft.getLogger().log(Level.INFO, String.format("SoundHost operation: SHUTDOWN"));
		for (AudioSourceWrapper wrapper : sources.values()) {
			wrapper.stop();
			wrapper.shutdown();
		}
		sources.clear();
	}

}
