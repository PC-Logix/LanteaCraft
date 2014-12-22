package lc.common.network.packets;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.io.IOException;

import lc.common.network.packets.abs.LCTargetPacket;
import lc.common.util.math.DimensionPos;

public class LCClientUpdate extends LCTargetPacket {

	/** Create a blank sync packet */
	public LCClientUpdate() {
	}

	/**
	 * Create a new sync packet
	 *
	 * @param target
	 *            The target element
	 */
	public LCClientUpdate(DimensionPos target) {
		this.target = target;
	}

	@Override
	public void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer) throws IOException {
		writeDimensionPosToBuffer(buffer, target);
	}

	@Override
	public void decodeFrom(ChannelHandlerContext ctx, ByteBuf buffer) throws IOException {
		target = readDimensionPosFromBuffer(buffer);
	}

}
