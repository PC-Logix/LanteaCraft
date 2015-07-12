package lc.common.network.packets;

import io.netty.buffer.ByteBuf;
import java.io.IOException;

import lc.common.network.packets.abs.LCTargetPacket;
import lc.common.util.math.DimensionPos;

/**
 * Packet sent from client to server when the client sending the packet doesn't
 * have information or required information about the object being targeted.
 * 
 * @author AfterLifeLochie
 */
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
	public void encodeInto(ByteBuf buffer) throws IOException {
		writeDimensionPosToBuffer(buffer, target);
	}

	@Override
	public void decodeFrom(ByteBuf buffer) throws IOException {
		target = readDimensionPosFromBuffer(buffer);
	}

}
