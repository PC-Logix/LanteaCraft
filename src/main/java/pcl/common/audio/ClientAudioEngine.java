package pcl.common.audio;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Vector;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import paulscode.sound.SoundSystem;
import paulscode.sound.SoundSystemConfig;
import pcl.lc.api.internal.ITickAgent;

public class ClientAudioEngine extends AudioEngine implements ITickAgent {

	private static class SoundHostObject extends WeakReference<Object> {
		public SoundHostObject(Object host) {
			super(host);
		}

		public int hashCode() {
			if (get() != null)
				return get().hashCode();
			return 0;
		}

		public boolean equals(Object o) {
			if (!(o instanceof SoundHostObject))
				return get().equals(o);
			return get().equals(((SoundHostObject) o).get());
		}
	}

	private static String label() {
		StringBuilder label = new StringBuilder();
		label.append("asm_snd").append(sourceCounter);
		sourceCounter++;
		return label.toString();
	}

	private static int sourceCounter = 0;

	public final float falloffDistance = 22.0F;
	private final int maxStreamingSources = 4;

	public boolean enabled = true;
	public int maxSources = 32;
	public float masterVolume = 0.5F;
	private SoundSystem system = null;

	private HashMap<SoundHostObject, ArrayList<AudioSource>> hostSourceList = new HashMap<SoundHostObject, ArrayList<AudioSource>>();

	public ClientAudioEngine() {
		super();
	}

	@Override
	public void initialize() {
		SoundSystemConfig.setNumberStreamingChannels(maxStreamingSources);
		SoundSystemConfig.setNumberNormalChannels(maxSources - maxStreamingSources);
	}
	
	@Override
	public AudioSource create(Object owner, AudioPosition position, String file, boolean looping, boolean override,
			float volume) {
		String tag = ClientAudioEngine.label();
		AudioSource source = new ClientAudioSource(system, position, file, looping, override, volume, tag);
		SoundHostObject host = new SoundHostObject(owner);
		if (!hostSourceList.containsKey(host))
			hostSourceList.put(host, new ArrayList<AudioSource>());
		hostSourceList.get(host).add(source);
		return source;
	}

	@Override
	public void advance() {
		if (!enabled)
			return;
		if (system == null)
			system = Minecraft.getMinecraft().sndManager.sndSystem;
		if (system == null)
			return;
		float vol = Minecraft.getMinecraft().gameSettings.soundVolume;
		if (masterVolume != vol)
			masterVolume = vol;

		Vector<SoundHostObject> stopSounds = new Vector<SoundHostObject>();
		EntityPlayer client = Minecraft.getMinecraft().thePlayer;
		if (client == null) {
			stopSounds.addAll(hostSourceList.keySet());
		} else {
			PriorityQueue<AudioSource> soundQueue = new PriorityQueue<AudioSource>();

			for (Entry<SoundHostObject, ArrayList<AudioSource>> entry : hostSourceList.entrySet()) {
				if (entry.getKey().isEnqueued()) {
					stopSounds.add(entry.getKey());
				} else {
					for (AudioSource audioSource : entry.getValue()) {
						audioSource.advance(client);
						if (audioSource.getRealVolume() > 0.0F)
							soundQueue.add(audioSource);
					}
				}
			}

			for (int k = 0; !soundQueue.isEmpty(); k++) {
				AudioSource source = soundQueue.poll();
				if (maxSources > k)
					source.activate();
				else
					source.cull();
			}
		}

		for (SoundHostObject host : stopSounds)
			removeSources(host);
	}

	private void removeSources(Object o) {
		if (system == null)
			return;
		SoundHostObject host;
		if (!(o instanceof SoundHostObject))
			host = new SoundHostObject(o);
		else
			host = (SoundHostObject) o;
		if (!hostSourceList.containsKey(host))
			return;
		ArrayList<AudioSource> sources = hostSourceList.get(host);
		for (AudioSource source : sources)
			source.remove();
		hostSourceList.remove(host);
	}
}
