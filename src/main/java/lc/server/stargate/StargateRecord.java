package lc.server.stargate;

import lc.api.stargate.StargateAddress;
import lc.common.util.math.ChunkPos;

public class StargateRecord {

	public StargateAddress address;
	public String server;
	public int dimension;
	public ChunkPos chunk;

	public StargateRecord() {
	}

	public StargateRecord(StargateAddress address, String server, int dimension, ChunkPos chunk) {
		this.address = address;
		this.server = server;
		this.dimension = dimension;
		this.chunk = chunk;
	}
}
