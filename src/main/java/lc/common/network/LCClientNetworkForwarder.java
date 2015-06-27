package lc.common.network;

import java.util.ArrayList;

import lc.common.network.packets.LCServerToServerEnvelope;

public class LCClientNetworkForwarder {

	private final ArrayList<LCServerToServerEnvelope> queue = new ArrayList<LCServerToServerEnvelope>();

	public LCClientNetworkForwarder() {
		// TODO Auto-generated constructor stub
	}

	public void handle(LCServerToServerEnvelope packet) {
		if (!queue.contains(packet))
			queue.add(packet);
	}

	public LCServerToServerEnvelope[] forward() {
		return queue.toArray(new LCServerToServerEnvelope[0]);
	}

	public void unwind() {
		queue.clear();
	}
}
