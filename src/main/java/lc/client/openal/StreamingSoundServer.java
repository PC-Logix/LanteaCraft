package lc.client.openal;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Vector;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import paulscode.sound.SoundSystem;
import paulscode.sound.SoundSystemConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundCategory;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.audio.SoundManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.sound.SoundLoadEvent;
import net.minecraftforge.common.MinecraftForge;
import lc.api.audio.streaming.ISound;
import lc.api.audio.streaming.ISoundPosition;
import lc.api.audio.streaming.ISoundProperties;
import lc.api.audio.streaming.ISoundServer;
import lc.common.LCLog;

public class StreamingSoundServer implements ISoundServer {

	private static class SoundOwner extends WeakReference<Object> {
		public SoundOwner(Object host) {
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
			if (!(o instanceof SoundOwner))
				return get().equals(o);
			return get().equals(((SoundOwner) o).get());
		}
	}

	private static String nextSoundTag(String clazz) {
		sourceCounter++;
		if (sourceCounter > 2048)
			sourceCounter = sourceCounter % 2048;
		return new StringBuilder().append("lcds_").append(clazz).append("_").append(sourceCounter).toString();
	}

	private static int sourceCounter = 0;

	private final float falloffDistance = 22.0F;
	private final int maxStreamingSources = 12;

	private boolean enabled = true;
	private int maxSources = 32;
	private float masterVolume = 0.5F;

	private SoundManager manager = null;
	private SoundSystem system = null;
	private volatile Thread initThread;
	private Field soundman_state;

	private HashMap<SoundOwner, ArrayList<ISound>> sounds = new HashMap<SoundOwner, ArrayList<ISound>>();

	public StreamingSoundServer() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	public void initialize() {
		int k = 0;
		for (Field field : SoundManager.class.getDeclaredFields())
			if (field.getType().isAssignableFrom(Boolean.TYPE)) {
				soundman_state = field;
				k++;
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

	@Override
	public boolean ready() {
		return enabled && system != null;
	}

	private static SoundSystem getSoundSystem(SoundManager soundManager) {
		for (Field field : SoundManager.class.getDeclaredFields())
			if (SoundSystem.class.isAssignableFrom(field.getType())) {
				field.setAccessible(true);
				try {
					return (SoundSystem) field.get(soundManager);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		return null;
	}

	private static SoundManager getSoundManager() {
		SoundHandler handler = Minecraft.getMinecraft().getSoundHandler();
		for (Field field : SoundHandler.class.getDeclaredFields())
			if (SoundManager.class.isAssignableFrom(field.getType())) {
				field.setAccessible(true);
				try {
					return (SoundManager) field.get(handler);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		return null;
	}

	@SubscribeEvent
	public void onSoundSetup(SoundLoadEvent event) {
		if (!enabled)
			return;
		sounds.clear();
		system = null;

		if (initThread != null) {
			initThread.interrupt();
			try {
				initThread.join();
			} catch (InterruptedException e) {
			}
		}

		LCLog.debug("StreamingSoundServer starting...");
		manager = getSoundManager();

		initThread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					while (!Thread.currentThread().isInterrupted()) {
						boolean loaded = false;
						try {
							loaded = soundman_state.getBoolean(manager);
						} catch (Exception e) {
							LCLog.fatal("StreamingSoundServer startup error: can't read sound management state.", e);
							throw new RuntimeException(e);
						}

						if (loaded) {
							system = StreamingSoundServer.getSoundSystem(manager);
							if (system == null) {
								LCLog.warn("StreamingSoundServer sound offline: system was marked as ready but can't find it.");
								enabled = false;
							} else
								LCLog.debug("StreamingSoundServer online.");
							break;
						}

						Thread.sleep(100L);
					}
				} catch (InterruptedException e) {
				}
				initThread = null;
			}
		}, "LanteaCraft-StreamingSoundServer OpenAL fetch task");

		initThread.start();
	}

	@Override
	public ISound assign(Object owner, String f, ISoundPosition pos, ISoundProperties props) {
		LCLog.doAssert(owner != null, "No sound owner");
		LCLog.doAssert(f != null, "No sound file specified");
		LCLog.doAssert(pos != null, "No sound position specified");
		LCLog.doAssert(props != null, "No sound properties specified");
		String tag = StreamingSoundServer.nextSoundTag("StreamingSound");
		StreamingSound sound = new StreamingSound(this, system, pos, f, props, tag);
		SoundOwner host = new SoundOwner(owner);
		if (!sounds.containsKey(host))
			sounds.put(host, new ArrayList<ISound>());
		sounds.get(host).add(sound);
		return sound;
	}

	@Override
	public float master() {
		return masterVolume;
	}

	@Override
	public float falloff() {
		return falloffDistance;
	}

	@Override
	public float volume(SoundCategory cat) {
		return Minecraft.getMinecraft().gameSettings.getSoundLevel(cat);
	}

	@Override
	public void think() {
		if (!enabled)
			return;
		if (system == null)
			return;
		masterVolume = Minecraft.getMinecraft().gameSettings.getSoundLevel(SoundCategory.MASTER);
		Vector<SoundOwner> purge = new Vector<SoundOwner>();
		EntityPlayer client = Minecraft.getMinecraft().thePlayer;
		if (client == null)
			purge.addAll(sounds.keySet());
		else {
			PriorityQueue<ISound> playing = new PriorityQueue<ISound>();
			for (Entry<SoundOwner, ArrayList<ISound>> entry : sounds.entrySet())
				if (entry.getKey().isEnqueued())
					purge.add(entry.getKey());
				else {
					for (ISound sound : entry.getValue()) {
						sound.think(this, client);
						if (sound.realvol() > 0.0F)
							playing.add(sound);
						if (sound instanceof StreamingSound) {
							StreamingSound base = (StreamingSound) sound;
							if (base.errored())
								base.setup(nextSoundTag("StreamingSound"));
						}
					}
				}
			for (int k = 0; !playing.isEmpty(); k++) {
				ISound source = playing.poll();
				if (maxSources > k)
					source.activate();
				else
					source.cull();
			}
		}

		for (SoundOwner host : purge)
			removeSources(host);
	}

	private void removeSources(Object o) {
		if (system == null)
			return;
		SoundOwner host;
		if (!(o instanceof SoundOwner))
			host = new SoundOwner(o);
		else
			host = (SoundOwner) o;
		if (!sounds.containsKey(host))
			return;
		ArrayList<ISound> sources = sounds.get(host);
		for (ISound source : sources)
			source.remove();
		sounds.remove(host);
	}
}
