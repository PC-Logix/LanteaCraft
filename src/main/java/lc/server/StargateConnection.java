package lc.server;

import lc.api.stargate.MessagePayload;
import lc.api.stargate.StargateAddress;
import lc.api.stargate.StargateConnectionType;
import lc.api.stargate.StargateState;
import lc.tiles.TileStargateBase;

public class StargateConnection {

	public final StargateConnectionType type;
	public final StargateAddress source, dest;
	public TileStargateBase tileFrom, tileTo;

	public StargateState state, next;
	public int stateTimeout;
	
	public int diallingState, diallingTimeout;

	public StargateConnection(StargateConnectionType type, StargateAddress source, StargateAddress dest) {
		this.type = type;
		this.source = source;
		this.dest = dest;
		this.state = StargateState.IDLE;
	}
	
	public void think() {
		
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
	
	public void shutdown() {
		state = StargateState.IDLE;
		deleteConnection();
	}

	public void transmit(TileStargateBase source, MessagePayload payload) {
		if (source == tileFrom)
			tileTo.receive(payload);
		if (source == tileTo)
			tileFrom.receive(payload);

	}
}
