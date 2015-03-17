package lc.server.database;

import lc.api.stargate.StargateAddress;
import lc.common.util.math.ChunkPos;

public class StargateRecord {

	public StargateAddress address;
	public int dimension;
	public ChunkPos chunk;

	public StargateRecord() {
	}

	public StargateRecord(StargateAddress address, int dimension, ChunkPos chunk) {
		this.address = address;
		this.dimension = dimension;
		this.chunk = chunk;
	}
}
