package lc.common.network.packets;

import io.netty.buffer.ByteBuf;

import java.io.IOException;

import lc.common.network.LCPacket;

public class LCNetworkHandshake extends LCPacket {

	public static enum HandshakeReason {
		SERVER_HELLO, CLIENT_HELLO, NEGOTIATION_ERROR, SECURITY_ERROR;
	}

	public HandshakeReason reason;
	public Object[] parameters;

	/** Default constructor */
	public LCNetworkHandshake() {

	}

	public LCNetworkHandshake(HandshakeReason reason, Object... params) {
		this.reason = reason;
		this.parameters = params;
	}

	@Override
	public void encodeInto(ByteBuf buffer) throws IOException {
		buffer.writeInt(reason.ordinal());
		encodePrimitiveArrayInto(buffer, parameters);
	}

	@Override
	public void decodeFrom(ByteBuf buffer) throws IOException {
		reason = HandshakeReason.values()[buffer.readInt()];
		parameters = decodePrimitiveArrayFrom(buffer);
	}

}
