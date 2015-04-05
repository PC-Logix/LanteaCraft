package lc.api.audio;

import lc.api.audio.channel.IMixer;
import lc.api.audio.streaming.ISoundServer;

public interface ISoundController {

	ISoundServer getSoundService();

	IMixer findMixer(Object key);
}
