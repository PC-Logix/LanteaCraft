package lc.api.audio;

public enum SoundPlaybackChannel {
	MASTER("master", 0), MUSIC("music", 1), RECORDS("record", 2), WEATHER("weather", 3), BLOCKS("block", 4), MOBS(
			"hostile", 5), ANIMALS("neutral", 6), PLAYERS("player", 7), AMBIENT("ambient", 8);

	public final String categoryName;
	public final int categoryId;

	private SoundPlaybackChannel(String n, int i) {
		categoryName = n;
		categoryId = i;
	}
}
