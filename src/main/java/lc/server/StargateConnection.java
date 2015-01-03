package lc.server;

import lc.api.stargate.StargateState;
import lc.tiles.TileStargateBase;

public class StargateConnection {

	public final String source;
	public final String dest;

	public TileStargateBase tileFrom;
	public TileStargateBase tileTo;

	public StargateState state;
	public int stateTimeout;

	public StargateConnection(String source, String dest) {
		this.source = source;
		this.dest = dest;
		this.state = StargateState.IDLE;
	}

	public void sendUpdates() {
		if (tileFrom != null)
			tileFrom.notifyState(this);
		if (tileTo != null)
			tileTo.notifyState(this);
	}

	public void deleteConnection() {
		if (tileFrom != null)
			tileFrom.notifyState(null);
		if (tileTo != null)
			tileTo.notifyState(null);
		tileFrom = null;
		tileTo = null;
	}

	public void openConnection() {
		if (state != StargateState.IDLE)
			return;
		state = StargateState.DIALLING;

	}

	public void closeConnection() {
		if (state != StargateState.CONNECTED)
			return;
		state = StargateState.DISCONNECTING;
	}
}
