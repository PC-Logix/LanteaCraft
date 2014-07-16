package pcl.lc.client.audio;

import java.net.MalformedURLException;
import java.net.URL;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

import org.apache.logging.log4j.Level;

import paulscode.sound.SoundSystem;
import pcl.lc.BuildInfo;
import pcl.lc.LanteaCraft;
import pcl.lc.core.ResourceAccess;
import pcl.lc.util.Vector3;

public class ClientAudioSource extends AudioSource implements Comparable<ClientAudioSource> {

	private SoundSystem system;
	private AudioPosition position;
	private String name;
	private boolean valid, culled;
	private float configuredVolume, realVolume;

	public ClientAudioSource(SoundSystem system, AudioPosition position, String file, boolean looping,
			boolean override, float volume, String tag) {
		try {
			this.system = system;
			this.position = position;
			name = tag;
			configuredVolume = volume;
			ClientAudioEngine engine = (ClientAudioEngine) LanteaCraft.getProxy().getAudioEngine();

			String filename = ResourceAccess.formatResourceName("${ASSET_KEY}:sound/%s", file);

			ResourceLocation resourcelocation = new ResourceLocation(filename);
			String s1 = String.format("%s:%s:%s", "soundconnectionhax", resourcelocation.getResourceDomain(),
					resourcelocation.getResourcePath());
			if (BuildInfo.SS_DEBUGGING)
				LanteaCraft.getLogger().log(Level.INFO, "AudioSource mounting file: " + s1);
			URL path = new URL(null, s1, new ClientSoundProtocolHandler());

			system.newSource(override, name, path, file, looping, (float) position.position.x,
					(float) position.position.y, (float) position.position.z, 0,
					engine.falloffDistance * Math.max(volume, 1.0F));
			valid = true;
			setVolume(volume);
		} catch (MalformedURLException malurl) {
			LanteaCraft.getLogger().log(Level.WARN, "Could not initialize AudioSource.", malurl);
		}
	}

	@Override
	public void play() {
		if (!valid)
			return;
		if (isPlaying()) {
			if (BuildInfo.SS_DEBUGGING)
				LanteaCraft.getLogger().log(Level.INFO,
						String.format("Can't play sound %s because it's already playing.", name));
			return;
		}
		if (culled) {
			if (BuildInfo.SS_DEBUGGING)
				LanteaCraft.getLogger()
						.log(Level.INFO, String.format("Can't play sound %s because it's culled.", name));
			return;
		}
		if (name == null)
			LanteaCraft.getLogger().log(Level.WARN, "Attempt to perform audio operation on illegal label.");
		system.play(name);
	}

	@Override
	public void pause() {
		if (!valid || !isPlaying() || culled)
			return;
		system.pause(name);
	}

	@Override
	public void stop() {
		if (!valid || !isPlaying())
			return;
		if (culled)
			return;
		system.stop(name);
		system.rewind(name);
	}

	@Override
	public void remove() {
		if (!valid)
			return;
		if (name == null)
			return;
		stop();
		system.removeSource(name);
		name = null;
		valid = false;
	}

	@Override
	public void flush() {
		if (!valid || !isPlaying() || culled)
			return;
		system.flush(name);
	}

	@Override
	public float getVolume() {
		if (!valid)
			return 0.0F;
		return system.getVolume(name);
	}

	@Override
	public void setVolume(float f) {
		configuredVolume = f;
		system.setVolume(name, 0.001F);
	}

	@Override
	public float getPitch() {
		if (!valid)
			return 0.0F;
		return system.getPitch(name);
	}

	@Override
	public void setPitch(float f) {
		if (!valid)
			return;
		system.setPitch(name, f);
	}

	@Override
	public void advance(EntityPlayer clientPlayer) {
		if (!valid || !isPlaying()) {
			realVolume = 0;
			return;
		}

		float md = ((ClientAudioEngine) LanteaCraft.getProxy().getAudioEngine()).falloffDistance;
		md *= Math.max(configuredVolume, 1.0F);
		float rolloff = 1.0F, rd = 1.0F;

		float d = 0.0F;

		if (position.world.equals(clientPlayer.worldObj))
			d = (float) position.position.sub(new Vector3(clientPlayer)).mag();

		if (d > md) {
			realVolume = 0.0F;
			cull();
			return;
		}

		if (rd > d)
			d = rd;
		float gain = 1.0F - rolloff * (d - rd) / (md - rd);
		float nrv = gain * configuredVolume
				* ((ClientAudioEngine) LanteaCraft.getProxy().getAudioEngine()).masterVolume;

		Vector3 i = new Vector3(clientPlayer);
		Vector3 j = position.position.sub(i).div(d);
		if (nrv > 0.1f)
			for (int k = 0; k < d; k++) {
				Block b = clientPlayer.worldObj.getBlock((int) i.x, (int) i.y, (int) i.z);
				if (b.isOpaqueCube())
					nrv *= 0.5F;
				else
					nrv *= 0.85F;
				i.add(j);
			}

		if (Math.abs(realVolume / nrv - 1.0F) > 0.06D)
			system.setVolume(name, Math.min(nrv, 1.0F)
					* ((ClientAudioEngine) LanteaCraft.getProxy().getAudioEngine()).masterVolume);
		realVolume = nrv;
	}

	@Override
	public void activate() {
		if (!valid || !culled)
			return;
		system.activate(name);
		culled = false;
		if (isPlaying()) {
			stop();
			play();
		}
	}

	@Override
	public void cull() {
		if (!valid || culled)
			return;
		system.cull(name);
		culled = true;
	}

	@Override
	public float getRealVolume() {
		return realVolume;
	}

	@Override
	public int compareTo(ClientAudioSource x) {
		if (culled)
			return (int) ((realVolume * 0.9F - x.realVolume) * 128.0F);
		return (int) ((realVolume - x.realVolume) * 128.0F);
	}

	@Override
	public boolean isPlaying() {
		if (!valid || name == null)
			return false;
		return system.playing(name);
	}
}
