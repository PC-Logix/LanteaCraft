package pcl.lc.client.audio;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.apache.logging.log4j.Level;

import pcl.lc.BuildInfo;
import pcl.lc.LanteaCraft;

public class SoundHost {

	protected final Object owner;
	protected final AudioPosition ownerPosition;
	private final HashMap<String, AudioSourceDef> defs;
	private final HashMap<String, AudioSourceWrapper> sources;
	private final ArrayList<String> dead_sources;

	private class AudioSourceDef {
		protected final String filename;
		protected final float volume;
		protected final int maxAge;

		public AudioSourceDef(String filename, float volume, int maxAge) {
			this.filename = filename;
			this.volume = volume;
			this.maxAge = maxAge;
		}
	}

	private class AudioSourceWrapper {
		private final SoundHost host;
		private final AudioSourceDef def;
		private AudioSource vsource;
		private int age;
		private boolean stopped, gcollected;

		public AudioSourceWrapper(SoundHost host, AudioSourceDef def) {
			this.host = host;
			this.def = def;
		}

		public void tick() {
			if (!playing() || stopped)
				return;
			if (def.maxAge > 0) {
				age++;
				if (age > def.maxAge)
					stop();
			}
		}

		public boolean alive() {
			return ((0 > def.maxAge) || (def.maxAge > age)) && playing();
		}

		public boolean dead() {
			if (gcollected)
				return true;
			if ((def.maxAge > 0 && age > def.maxAge) || stopped)
				return true;
			if (vsource != null && !vsource.isPlaying() && !vsource.isPaused())
				return true;
			return false;
		}

		public void setupSource() {
			AudioEngine engine = LanteaCraft.getProxy().getAudioEngine();
			vsource = engine.create(owner, host.ownerPosition, def.filename, 0 > age, false, def.volume);
		}

		public void play() {
			if (gcollected)
				return;
			if (vsource == null)
				setupSource();
			if (vsource.isPlaying())
				vsource.stop();
			stopped = false;
			vsource.play();
		}

		public boolean playing() {
			return vsource != null && vsource.isPlaying();
		}

		public void pause() {
			if (gcollected)
				return;
			if (vsource != null)
				vsource.pause();
		}

		public void stop() {
			if (gcollected)
				return;
			if (vsource != null)
				vsource.stop();
			vsource = null;
			stopped = true;
		}

		public void shutdown() {
			if (vsource != null) {
				vsource.stop();
				vsource.remove();
				gcollected = true;
			}
		}
	}

	public SoundHost(Object owner, AudioPosition ownerPosition) {
		this.owner = owner;
		this.ownerPosition = ownerPosition;
		this.defs = new HashMap<String, AudioSourceDef>();
		this.sources = new HashMap<String, AudioSourceWrapper>();
		this.dead_sources = new ArrayList<String>();
	}

	public void tick() {
		for (Entry<String, AudioSourceWrapper> wrapper : sources.entrySet()) {
			wrapper.getValue().tick();
			if (wrapper.getValue().dead())
				dead_sources.add(wrapper.getKey());
		}

		if (dead_sources.size() > 0) {
			for (String dead : dead_sources) {
				AudioSourceWrapper wrapper = sources.get(dead);
				wrapper.shutdown();
				sources.remove(dead);
			}
			dead_sources.clear();
		}
	}

	public void registerChannel(String name, String filename, float volume, int age) {
		if (!defs.containsKey(name))
			defs.put(name, new AudioSourceDef(filename, volume, age));
	}

	public AudioSourceWrapper findOrCreateWrapper(String key) {
		for (Entry<String, AudioSourceWrapper> wrapper : sources.entrySet())
			if (wrapper.getKey().equalsIgnoreCase(key))
				return wrapper.getValue();
		AudioSourceDef def = defs.get(key.toLowerCase());
		if (def == null)
			return null;
		AudioSourceWrapper wrapper = new AudioSourceWrapper(this, def);
		sources.put(key.toLowerCase(), wrapper);
		return wrapper;
	}

	public void playChannel(String channel) {
		AudioSourceWrapper wrapper = findOrCreateWrapper(channel);
		if (wrapper != null) {
			if (wrapper.playing())
				wrapper.stop();
			wrapper.play();
		} else
			LanteaCraft.getLogger().log(Level.WARN, String.format("No such channel %s.", channel));
	}

	public void pauseChannel(String channel) {
		AudioSourceWrapper wrapper = findOrCreateWrapper(channel);
		if (wrapper != null) {
			wrapper.pause();
		} else
			LanteaCraft.getLogger().log(Level.WARN, String.format("No such channel %s.", channel));
	}

	public void stopChannel(String channel) {
		AudioSourceWrapper wrapper = findOrCreateWrapper(channel);
		if (wrapper != null) {
			wrapper.stop();
		} else
			LanteaCraft.getLogger().log(Level.WARN, String.format("No such channel %s.", channel));
	}

	public void shutdown(boolean force) {
		if (!force)
			for (AudioSourceWrapper wrapper : sources.values())
				if (wrapper.playing())
					return;
		for (AudioSourceWrapper wrapper : sources.values()) {
			wrapper.stop();
			wrapper.shutdown();
		}
	}

}
