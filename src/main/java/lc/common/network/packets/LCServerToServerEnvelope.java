package lc.common.network.packets;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.io.IOException;

import lc.common.network.LCPacket;

public class LCServerToServerEnvelope extends LCPacket {

	private byte[] data;
	private byte[] signature;

	public LCServerToServerEnvelope() {
	}

	public LCServerToServerEnvelope(byte[] data, byte[] signature) {
		this.data = data;
		this.signature = signature;
	}

	public byte[] data() {
		return data;
	}

	public byte[] signature() {
		return signature;
	}

	public void data(byte[] data) {
		this.data = data;
	}

	public void signature(byte[] signature) {
		this.signature = signature;
	}

	@Override
	public void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer) throws IOException {
		buffer.writeInt(data.length);
		buffer.writeInt(signature.length);
		buffer.writeBytes(data);
		buffer.writeBytes(signature);
	}

	@Override
	public void decodeFrom(ChannelHandlerContext ctx, ByteBuf buffer) throws IOException {
		data = new byte[buffer.readInt()];
		signature = new byte[buffer.readInt()];
		buffer.readBytes(data);
		buffer.readBytes(signature);
	}
}
