package pcl.common.audio;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Vector;

import org.apache.logging.log4j.Level;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundCategory;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.audio.SoundManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.sound.SoundLoadEvent;
import net.minecraftforge.common.MinecraftForge;
import paulscode.sound.SoundSystem;
import paulscode.sound.SoundSystemConfig;
import pcl.lc.LanteaCraft;
import pcl.lc.api.internal.ITickAgent;

public class ClientAudioEngine extends AudioEngine implements ITickAgent {

	private static class SoundHostObject extends WeakReference<Object> {
		public SoundHostObject(Object host) {
			super(host);
		}

		@Override
		public int hashCode() {
			if (get() != null)
				return get().hashCode();
			return 0;
		}

		@Override
		public boolean equals(Object o) {
			if (!(o instanceof SoundHostObject))
				return get().equals(o);
			return get().equals(((SoundHostObject) o).get());
		}
	}

	private static String label() {
		StringBuilder label = new StringBuilder();
		label.append("lc_directsound_").append(sourceCounter);
		sourceCounter++;
		if (sourceCounter > 512)
			sourceCounter = sourceCounter % 512;
		return label.toString();
	}

	private static int sourceCounter = 0;

	public final float falloffDistance = 22.0F;
	private final int maxStreamingSources = 8;

	public boolean enabled = true;
	public int maxSources = 32;
	public float masterVolume = 0.5F;

	private SoundManager manager = null;
	private SoundSystem system = null;
	private volatile Thread initThread;
	private Field soundman_state;

	private HashMap<SoundHostObject, ArrayList<AudioSource>> hostSourceList = new HashMap<SoundHostObject, ArrayList<AudioSource>>();

	public ClientAudioEngine() {
		super();
	}

	@Override
	public void initialize() {
		int k = 0;
		for (Field field : SoundManager.class.getDeclaredFields()) {
			if (field.getType().isAssignableFrom(Boolean.TYPE)) {
				soundman_state = field;
				k++;
			}
		}
		if (k != 1) {
			enabled = false;
			return;
		}
		SoundSystemConfig.setNumberStreamingChannels(maxStreamingSources);
		SoundSystemConfig.setNumberNormalChannels(maxSources - maxStreamingSources);
		soundman_state.setAccessible(true);
		MinecraftForge.EVENT_BUS.register(this);
	}

	private static SoundSystem getSoundSystem(SoundManager soundManager) {
		for (Field field : SoundManager.class.getDeclaredFields()) {
			if (SoundSystem.class.isAssignableFrom(field.getType())) {
				field.setAccessible(true);
				try {
					return (SoundSystem) field.get(soundManager);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}
		return null;
	}

	private static SoundManager getSoundManager() {
		SoundHandler handler = Minecraft.getMinecraft().getSoundHandler();
		for (Field field : SoundHandler.class.getDeclaredFields()) {
			if (SoundManager.class.isAssignableFrom(field.getType())) {
				field.setAccessible(true);
				try {
					return (SoundManager) field.get(handler);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}
		return null;
	}

	@SubscribeEvent
	public void onSoundSetup(SoundLoadEvent event) {
		if (!this.enabled)
			return;
		hostSourceList.clear();
		system = null;

		if (initThread != null) {
			initThread.interrupt();
			try {
				initThread.join();
			} catch (InterruptedException e) {
			}
		}

		LanteaCraft.getLogger().log(Level.INFO, "LanteaCraft audio task starting.");
		manager = getSoundManager();

		this.initThread = new Thread(new Runnable() {
			public void run() {
				try {
					while (!Thread.currentThread().isInterrupted()) {
						boolean loaded;
						try {
							loaded = soundman_state.getBoolean(manager);
						} catch (Exception e) {
							throw new RuntimeException(e);
						}

						if (loaded) {
							system = ClientAudioEngine.getSoundSystem(manager);
							if (system == null) {
								LanteaCraft.getLogger().log(Level.WARN,
										"Can't get audio system. LanteaCraft sound offline.");
								enabled = false;
								break;
							}
							LanteaCraft.getLogger().log(Level.INFO, "LanteaCraft sound online.");
							break;
						}

						Thread.sleep(100L);
					}
				} catch (InterruptedException e) {
				}
				initThread = null;
			}
		}, "LanteaCraft audio hook task");

		this.initThread.start();
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
			return;
		float vol = Minecraft.getMinecraft().gameSettings.getSoundLevel(SoundCategory.MASTER);
		if (masterVolume != vol)
			masterVolume = vol;

		Vector<SoundHostObject> stopSounds = new Vector<SoundHostObject>();
		EntityPlayer client = Minecraft.getMinecraft().thePlayer;
		if (client == null) {
			LanteaCraft.getLogger().log(Level.INFO, "Removing all sounds...");
			stopSounds.addAll(hostSourceList.keySet());
		} else {
			PriorityQueue<AudioSource> soundQueue = new PriorityQueue<AudioSource>();

			for (Entry<SoundHostObject, ArrayList<AudioSource>> entry : hostSourceList.entrySet())
				if (entry.getKey().isEnqueued())
					stopSounds.add(entry.getKey());
				else
					for (AudioSource audioSource : entry.getValue()) {
						audioSource.advance(client);
						if (audioSource.getRealVolume() > 0.0F)
							soundQueue.add(audioSource);
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
