package lc.common.network.packets;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;

import java.io.IOException;

import lc.LCRuntime;
import lc.common.network.LCPacket;
import lc.common.network.LCPacketPipeline;

public class LCServerToServerEnvelope extends LCPacket {

	public static LCServerToServerEnvelope envelope(LCPacket data) throws IOException {
		LCPacketPipeline pipe = LCRuntime.runtime.network();
		ByteBuf buffer = Unpooled.buffer();
		buffer.writeByte((byte) pipe.discriminator(data.getClass()));
		data.encodeInto(null, buffer);
		return new LCServerToServerEnvelope(buffer.array(), null);
	}

	public static LCPacket unenvelope(LCServerToServerEnvelope envelope) throws IOException {
		LCPacketPipeline pipe = LCRuntime.runtime.network();
		ByteBuf buffer = Unpooled.wrappedBuffer(envelope.data);
		Class<? extends LCPacket> clazz = pipe.packetClass(buffer.readByte());
		LCPacket packet;
		try {
			packet = clazz.newInstance();
		} catch (Exception e) {
			throw new IOException("Illegal or unsupported encapsulated packet.", e);
		}
		packet.decodeFrom(null, buffer);
		return packet;
	}

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

	public boolean signed() {
		return signature != null;
	}

	@Override
	public void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer) throws IOException {
		if (data == null || signature == null || data.length == 0 || signature.length == 0)
			throw new IOException("Illegal encapsulated packet; no data or unsigned.");
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
