package lc.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import cpw.mods.fml.relauncher.Side;
import lc.LCRuntime;
import lc.api.event.ITickEventHandler;
import lc.api.stargate.StargateAddress;
import lc.api.stargate.StargateConnectionType;
import lc.common.util.math.ChunkPos;
import lc.server.database.StargateRecord;
import lc.tiles.TileStargateBase;

public class StargateManager implements ITickEventHandler {

	private final HintProviderServer server;
	private final HashMap<Integer, ArrayList<StargateConnection>> connections;

	public StargateManager(HintProviderServer server) {
		this.server = server;
		this.connections = new HashMap<Integer, ArrayList<StargateConnection>>();
		LCRuntime.runtime.ticks().register(this);
	}

	public StargateAddress getStargateAddress(TileStargateBase tile) {
		return server.universeMgr.findAddress(tile.getWorldObj().provider.dimensionId, new ChunkPos(tile));
	}

	public void openConnection(TileStargateBase tile, StargateAddress address) {
		StargateRecord what = server.universeMgr.findRecord(address);
		StargateConnectionType type = (what.server != null) ? StargateConnectionType.SERVERTOSERVER
				: StargateConnectionType.LOCAL;
		StargateConnection connection = new StargateConnection(type, tile.getStargateAddress(), what.address);
	}

	public void closeConnection(TileStargateBase tile, char[] address) {

	}

	public void closeConnectionsIn(int dimensionId) {

	}

	public void closeAllConnections(boolean now) {
		
		
	}

	@Override
	public void think(Side what) {
		if (what == Side.SERVER) {
			Iterator<Integer> dimensions = connections.keySet().iterator();
			while (dimensions.hasNext()) {
				int dimension = dimensions.next();

			}
		}
	}

}
