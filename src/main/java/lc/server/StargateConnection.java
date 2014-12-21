package lc.server;

import lc.tiles.TileStargateBase;

public class StargateConnection {

	public final String source;
	public final String dest;

	public TileStargateBase tileFrom;
	public TileStargateBase tileTo;

	// TODO: Stargate state

	public StargateConnection(String source, String dest) {
		this.source = source;
		this.dest = dest;
	}

}
