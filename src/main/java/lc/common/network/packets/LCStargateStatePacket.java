package lc.common.network.packets;

import io.netty.buffer.ByteBuf;
import java.io.IOException;

import lc.common.network.packets.abs.LCTargetPacket;
import lc.common.util.math.DimensionPos;

public class LCStargateStatePacket extends LCTargetPacket {

	public int type;
	public double duration;
	public Object[] args;

	public LCStargateStatePacket() {
	}

	public LCStargateStatePacket(DimensionPos target, int type, double duration, Object[] args) {
		this.target = target;
		this.type = type;
		this.duration = duration;
		this.args = args;
	}

	@Override
	public void encodeInto(ByteBuf buffer) throws IOException {
		writeDimensionPosToBuffer(buffer, target);
		buffer.writeInt(type);
		buffer.writeDouble(duration);
		encodePrimitiveArrayInto(buffer, args);
	}

	@Override
	public void decodeFrom(ByteBuf buffer) throws IOException {
		target = readDimensionPosFromBuffer(buffer);
		this.type = buffer.readInt();
		this.duration = buffer.readDouble();
		this.args = decodePrimitiveArrayFrom(buffer);
	}

}
