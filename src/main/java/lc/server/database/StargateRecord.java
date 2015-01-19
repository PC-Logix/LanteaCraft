package lc.server.database;

import lc.common.util.math.ChunkPos;
import lc.server.StargateAddress;

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
