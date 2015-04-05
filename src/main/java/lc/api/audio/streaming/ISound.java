package lc.api.audio.streaming;

public interface ISound {

	void play();

	void pause();

	void stop();

	void remove();

	void cull();

	void activate();

	void think(ISoundServer server, Object player);

	float realvol();

	boolean playing();

	boolean paused();

	ISoundProperties properties();

}
