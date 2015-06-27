package lc.common.network;

import java.util.ArrayList;

import lc.common.network.packets.LCServerToServerEnvelope;

public class LCClientNetworkForwarder {

	private final ArrayList<LCServerToServerEnvelope> queue = new ArrayList<LCServerToServerEnvelope>();

	public LCClientNetworkForwarder() {
		// TODO Auto-generated constructor stub
	}

	public void forward(LCServerToServerEnvelope packet) {
		if (!queue.contains(packet))
			queue.add(packet);
	}

	public LCServerToServerEnvelope[] expand() {
		return queue.toArray(new LCServerToServerEnvelope[0]);
	}
}
