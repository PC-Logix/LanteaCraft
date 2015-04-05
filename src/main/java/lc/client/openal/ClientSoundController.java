package lc.client.openal;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.WeakHashMap;

import cpw.mods.fml.relauncher.Side;
import net.minecraftforge.common.MinecraftForge;
import lc.api.audio.ISoundController;
import lc.api.audio.channel.IMixer;
import lc.api.audio.streaming.ISoundServer;
import lc.api.event.ITickEventHandler;
import lc.common.LCLog;

public class ClientSoundController implements ISoundController, ITickEventHandler {

	private final ISoundServer server;

	private final WeakHashMap<Object, IMixer> liveMixers;
	private final ArrayList<IMixer> mixers;

	public ClientSoundController() {
		this.server = new StreamingSoundServer();
		this.liveMixers = new WeakHashMap<Object, IMixer>();
		this.mixers = new ArrayList<IMixer>();
	}

	@Override
	public ISoundServer getSoundService() {
		return server;
	}

	@Override
	public IMixer findMixer(Object key) {
		if (liveMixers.containsKey(key))
			return liveMixers.get(key);
		StreamingSoundMixer mixer = new StreamingSoundMixer();
		liveMixers.put(key, mixer);
		return mixer;
	}

	@Override
	public void think(Side what) {
		if (what == Side.CLIENT) {
			server.think();
			ArrayList<IMixer> live = new ArrayList<IMixer>();
			for (Entry<Object, IMixer> mixer : liveMixers.entrySet())
				live.add(mixer.getValue());
			Iterator<IMixer> seen = mixers.iterator();
			while (seen.hasNext()) {
				IMixer mixer = seen.next();
				if (!live.contains(seen)) {
					LCLog.debug("Cleaning up orphaned mixer %s", seen);
					mixer.shutdown(true);
					seen.remove();
				} else
					mixer.think();
			}
		}
	}

}
