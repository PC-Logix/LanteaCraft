package lc.api.audio.channel;

import lc.api.audio.streaming.ISound;

public interface IMixer {

	void createChannel(String name, ISound sound);

	void deleteChannel(String name);

	void playChannel(String name);

	void pauseChannel(String name);

	void stopChannel(String name);

	boolean shutdown(boolean now);

	void think();

}
