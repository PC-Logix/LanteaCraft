package lc.common.network;

import java.util.ArrayList;

public class LCPacketBuffer<T> {

	private final ArrayList<T> buffer = new ArrayList<T>();

	public void addPacket(T t) {
		if (!buffer.contains(t))
			buffer.add(t);
	}

	public <T> T[] packets() {
		return (T[]) buffer.toArray();
	}

	public void clear() {
		buffer.clear();
	}

	public int size() {
		return buffer.size();
	}

}
