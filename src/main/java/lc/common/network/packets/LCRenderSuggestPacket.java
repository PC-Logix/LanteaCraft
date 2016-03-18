package lc.common.network.packets;

import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

import lc.common.network.packets.abs.LCTargetPacket;
import lc.common.util.data.StateMap;
import lc.common.util.math.DimensionPos;

public class LCRenderSuggestPacket extends LCTargetPacket {

	private HashMap<String, Object> namedMap;

	public LCRenderSuggestPacket() {
		namedMap = new HashMap<String, Object>();
	}

	public LCRenderSuggestPacket(DimensionPos target, StateMap state) {
		this(target, state.raw());
	}

	public LCRenderSuggestPacket(DimensionPos target, HashMap<String, Object> namedMap) {
		this.target = target;
		this.namedMap = namedMap;
	}

	public StateMap toStateMap() {
		StateMap map = new StateMap();
		map.setAllNamed(this.namedMap);
		return map;
	}

	@Override
	public void encodeInto(ByteBuf buffer) throws IOException {
		writeDimensionPosToBuffer(buffer, target);
		buffer.writeInt(namedMap.keySet().size());
		for (Entry<String, Object> entry : namedMap.entrySet()) {
			encodePrimitiveInto(buffer, entry.getKey());
			encodePrimitiveInto(buffer, entry.getValue());
		}
	}

	@Override
	public void decodeFrom(ByteBuf buffer) throws IOException {
		target = readDimensionPosFromBuffer(buffer);
		int namedMapSize = buffer.readInt();
		for (int i = 0; i < namedMapSize; i++) {
			String key = (String) decodePrimitiveFrom(buffer);
			Object value = decodePrimitiveFrom(buffer);
			namedMap.put(key, value);
		}
	}

}
