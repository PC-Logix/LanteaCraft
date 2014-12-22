package lc.server.database;

import lc.common.util.math.ChunkPos;

public class StargateRecord {

	public int type;
	public char[] address;
	public byte[] xdata;

	public int dimension;
	public ChunkPos chunk;

	public StargateRecord() { }
}
