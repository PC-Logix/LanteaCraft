package lc.client.openal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.WeakHashMap;

import cpw.mods.fml.relauncher.Side;
import net.minecraftforge.common.MinecraftForge;
import lc.LCRuntime;
import lc.api.audio.ISoundController;
import lc.api.audio.channel.IMixer;
import lc.api.audio.streaming.ISoundServer;
import lc.api.event.ITickEventHandler;
import lc.common.LCLog;
import lc.common.util.java.DestructableReference;

public class ClientSoundController implements ISoundController, ITickEventHandler {

	private final ISoundServer server;

	private final HashMap<DestructableReference<Object>, IMixer> liveMixers;

	public ClientSoundController() {
		LCRuntime.runtime.ticks().register(this);
		this.server = new StreamingSoundServer();
		this.liveMixers = new HashMap<DestructableReference<Object>, IMixer>();
		this.server.initialize();
	}

	@Override
	public boolean ready() {
		return server != null && server.ready();
	}

	@Override
	public ISoundServer getSoundService() {
		return server;
	}

	@Override
	public IMixer findMixer(Object key) {
		Iterator<Entry<DestructableReference<Object>, IMixer>> itr = liveMixers.entrySet().iterator();
		while (itr.hasNext()) {
			Entry<DestructableReference<Object>, IMixer> mz = itr.next();
			if (mz.getKey().get().equals(key))
				return mz.getValue();
		}
		StreamingSoundMixer mixer = new StreamingSoundMixer();
		liveMixers.put(new DestructableReference<Object>(key), mixer);
		return mixer;
	}

	@Override
	public void think(Side what) {
		if (what == Side.CLIENT) {
			server.think();
			Iterator<Entry<DestructableReference<Object>, IMixer>> iter = liveMixers.entrySet().iterator();
			while (iter.hasNext()) {
				Entry<DestructableReference<Object>, IMixer> mixer = iter.next();
				DestructableReference<Object> ref = mixer.getKey();
				if (ref.get() != null) {
					mixer.getValue().think();
				} else {
					LCLog.debug("Cleaning up orphaned sound mixer %s", mixer);
					mixer.getValue().shutdown(true);
					iter.remove();
				}
			}
		}
	}

}
