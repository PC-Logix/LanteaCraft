package lc.common.network;

import java.util.ArrayList;

public class LCPacketBuffer<T> {

	private final ArrayList<T> buffer = new ArrayList<T>();

	public void addPacket(T t) {
		if (!buffer.contains(t))
			buffer.add(t);
	}

	public ArrayList<T> packets() {
		return buffer;
	}

	public void clear() {
		buffer.clear();
	}

	public int size() {
		return buffer.size();
	}

}
