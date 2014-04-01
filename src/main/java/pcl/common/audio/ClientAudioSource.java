package pcl.common.audio;

import java.net.URL;
import java.util.logging.Level;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import paulscode.sound.SoundSystem;
import pcl.common.util.Vector3;
import pcl.lc.LanteaCraft;

public class ClientAudioSource extends AudioSource {

	private SoundSystem system;
	private AudioPosition position;
	private String name;
	private boolean valid, culled, isPlaying;
	private float configuredVolume, realVolume;

	public ClientAudioSource(SoundSystem system, AudioPosition position, String file, boolean looping,
			boolean override, float volume, String tag) {
		this.system = system;
		this.position = position;
		name = tag;
		configuredVolume = volume;
		ClientAudioEngine engine = (ClientAudioEngine) LanteaCraft.getProxy().getAudioEngine();

		URL path = ClientAudioSource.class.getClassLoader().getResource("assets/pcl_pc/sound/" + file);
		if (path == null)
			LanteaCraft.getLogger().log(Level.WARNING,
					String.format("Sound `%s` requested, but file doesn't exist!", file));

		system.newSource(override, name, path, file, looping, (float) position.position.x, (float) position.position.y,
				(float) position.position.z, 0, engine.falloffDistance * Math.max(volume, 1.0F));
		valid = true;
		setVolume(volume);
	}

	@Override
	public void play() {
		if (!valid)
			return;
		if (isPlaying)
			return;
		isPlaying = true;
		if (culled)
			return;
		system.play(name);
	}

	@Override
	public void pause() {
		if (!valid || !isPlaying || culled)
			return;
		isPlaying = false;
		system.pause(name);
	}

	@Override
	public void stop() {
		if (!valid || !isPlaying)
			return;
		isPlaying = false;
		if (culled)
			return;
		system.stop(name);
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
		if (!valid || !isPlaying || culled)
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
		if (!valid || !isPlaying) {
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
				int b = clientPlayer.worldObj.getBlockId((int) i.x, (int) i.y, (int) i.z);
				if (b != 0)
					if (Block.opaqueCubeLookup[b])
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
		if (isPlaying) {
			isPlaying = false;
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
}
