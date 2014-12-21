package lc.server;

import java.util.ArrayList;
import java.util.HashMap;

import lc.common.util.math.DimensionPos;
import lc.tiles.TileStargateBase;

public class StargateManager {

	private final HintProviderServer server;

	private final HashMap<Integer, ArrayList<StargateConnection>> connections;

	public StargateManager(HintProviderServer server) {
		this.server = server;
		this.connections = new HashMap<Integer, ArrayList<StargateConnection>>();
	}

	public char[] getStargateAddress(TileStargateBase tile) {
		return server.universeMgr.findAddress(new DimensionPos(tile));
	}

	public void openConnection(TileStargateBase tile, String address) {

	}

	public void closeConnection(TileStargateBase tile, String address) {

	}

	public void closeConnectionsIn(int dimensionId) {

	}

	public void closeAllConnections() {

	}

}
