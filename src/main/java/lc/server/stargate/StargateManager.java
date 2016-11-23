package lc.server.stargate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import cpw.mods.fml.relauncher.Side;
import lc.LCRuntime;
import lc.api.event.ITickEventHandler;
import lc.api.stargate.StargateAddress;
import lc.api.stargate.StargateConnectionType;
import lc.common.LCLog;
import lc.common.util.math.ChunkPos;
import lc.server.HintProviderServer;
import lc.tiles.TileStargateBase;

/**
 * Stargate state manager
 * 
 * @author AfterLifeLochie
 *
 */
public class StargateManager implements ITickEventHandler {

	private final UniverseManager universeMgr;
	private final HashMap<Integer, ArrayList<StargateConnection>> connections;

	/**
	 * Default constructor
	 * 
	 * @param server
	 *            The hint server
	 */
	public StargateManager(UniverseManager universeMgr) {
		this.universeMgr = universeMgr;
		this.connections = new HashMap<Integer, ArrayList<StargateConnection>>();
		LCRuntime.runtime.ticks().register(this);
	}

	/**
	 * Get a Stargate address for a tile
	 * 
	 * @param tile
	 *            The tile
	 * @return The address
	 */
	public StargateAddress getStargateAddress(TileStargateBase tile) {
		return universeMgr.findAddress(tile.getWorldObj().provider.dimensionId, new ChunkPos(tile));
	}

	/**
	 * Open a connection from a Stargate
	 * 
	 * @param tile
	 *            The source tile
	 * @param address
	 *            The destination address
	 * @param connectTimeout
	 *            The time to wait before giving up on connecting
	 * @param establishedTimeout
	 *            The time to wait before force-closing the Stargate
	 * @return The connection
	 */
	public StargateConnection openConnection(TileStargateBase tile, StargateAddress address, int connectTimeout,
			int establishedTimeout) {
		StargateRecord what = universeMgr.findRecord(address);
		if (what == null) {
			LCLog.debug("No such address found: " + address);
			return null;
		}
		StargateConnectionType type = (what.server != null) ? StargateConnectionType.SERVERTOSERVER
				: StargateConnectionType.LOCAL;
		StargateConnection connection = new StargateConnection(type, tile, what, connectTimeout, establishedTimeout);
		synchronized (connections) {
			if (!connections.containsKey(tile.getWorldObj().provider.dimensionId))
				connections.put(tile.getWorldObj().provider.dimensionId, new ArrayList<StargateConnection>());
			connections.get(tile.getWorldObj().provider.dimensionId).add(connection);
		}
		connection.openConnection();
		return connection;
	}

	/**
	 * Close all connections in a dimension
	 * 
	 * @param dimensionId
	 *            The dimension
	 */
	public void closeConnectionsIn(int dimensionId) {
		LCLog.info("Closing connections in dimension %s.", dimensionId);
		ArrayList<StargateConnection> dc = null;
		synchronized (connections) {
			dc = connections.remove(dimensionId);
		}
		if (dc != null) {
			Iterator<StargateConnection> iter = dc.iterator();
			while (iter.hasNext())
				iter.next().deleteConnection();
		}
	}

	/**
	 * Close all connections active in the game
	 * 
	 * @param now
	 *            Force the close - don't wait for idle to arrive
	 */
	public void closeAllConnections(boolean now) {
		synchronized (connections) {
			Iterator<Integer> dimensions = connections.keySet().iterator();
			while (dimensions.hasNext()) {
				int dimension = dimensions.next();
				ArrayList<StargateConnection> dc = connections.get(dimension);
				Iterator<StargateConnection> iter = dc.iterator();
				while (iter.hasNext())
					iter.next().deleteConnection();
				dimensions.remove();
			}
		}
	}

	@Override
	public void think(Side what) {
		if (what == Side.SERVER) {
			synchronized (connections) {
				Iterator<Integer> dimensions = connections.keySet().iterator();
				while (dimensions.hasNext()) {
					int dimension = dimensions.next();
					ArrayList<StargateConnection> dc = connections.get(dimension);
					Iterator<StargateConnection> iter = dc.iterator();
					while (iter.hasNext()) {
						StargateConnection conn = iter.next();
						conn.think();
						if (conn.dead) {
							LCLog.debug("Removing dead connection %s.", conn);
							iter.remove();
						}
					}
				}
			}
		}
	}

}
