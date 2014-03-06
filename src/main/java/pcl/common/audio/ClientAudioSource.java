package pcl.common.audio;

import java.net.URL;
import java.util.logging.Level;

import net.minecraft.entity.player.EntityPlayer;
import paulscode.sound.SoundSystem;
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
		this.name = tag;
		this.configuredVolume = volume;
		ClientAudioEngine engine = (ClientAudioEngine) LanteaCraft.getProxy().getAudioEngine();

		URL path = ClientAudioSource.class.getClassLoader().getResource("assets/pcl_pc/sounds/" + file);
		if (path == null) {
			LanteaCraft.getLogger().log(Level.WARNING,
					String.format("Sound `%s` requested, but file doesn't exist!", file));
		}

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
