package lc.server.database;

import lc.common.util.math.ChunkPos;
import lc.server.StargateAddress;

public class StargateRecord {

	public int type;
	public StargateAddress address;
	public byte[] xdata;

	public int dimension;
	public ChunkPos chunk;

	public StargateRecord() {
	}
}
