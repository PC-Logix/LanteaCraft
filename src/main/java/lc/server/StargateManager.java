package lc.server;

import java.util.ArrayList;
import java.util.HashMap;

import lc.common.util.math.ChunkPos;
import lc.common.util.math.DimensionPos;
import lc.server.database.StargateRecord;
import lc.tiles.TileStargateBase;

public class StargateManager {

	private final HintProviderServer server;
	private final HashMap<Integer, ArrayList<StargateConnection>> connections;

	public StargateManager(HintProviderServer server) {
		this.server = server;
		this.connections = new HashMap<Integer, ArrayList<StargateConnection>>();
	}

	public StargateAddress getStargateAddress(TileStargateBase tile) {
		return server.universeMgr.findAddress(tile.getWorldObj().provider.dimensionId, new ChunkPos(tile));
	}

	public void openConnection(TileStargateBase tile, StargateAddress address) {
		StargateRecord what = server.universeMgr.findRecord(address);

	}

	public void closeConnection(TileStargateBase tile, char[] address) {

	}

	public void closeConnectionsIn(int dimensionId) {

	}

	public void closeAllConnections() {

	}

}
