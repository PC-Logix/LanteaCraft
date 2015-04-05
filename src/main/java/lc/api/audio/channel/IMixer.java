package lc.api.audio.channel;

import lc.api.audio.streaming.ISound;

public interface IMixer {

	void createChannel(String name, ISound sound);

	void deleteChannel(String name);

	IMixer playChannel(String name);
	
	IMixer replayChannel(String name);

	IMixer pauseChannel(String name);

	IMixer stopChannel(String name);

	boolean shutdown(boolean now);

	void think();

}
