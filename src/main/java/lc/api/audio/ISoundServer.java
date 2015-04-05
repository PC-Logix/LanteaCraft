/**
 * This file is part of the official LanteaCraft API. Please see the usage guide and
 * restrictions on use in the package-info file.
 */
package lc.api.audio;

import net.minecraft.client.audio.SoundCategory;

public interface ISoundServer {

	void initialize();

	float master();

	float volume(SoundCategory cat);

	ISound assign(Object owner, String f, ISoundPosition pos, ISoundProperties props);

}
