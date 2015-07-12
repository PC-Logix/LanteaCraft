package lc.common.network.packets;

import io.netty.buffer.ByteBuf;
import java.io.IOException;

import lc.api.stargate.StargateState;
import lc.common.network.packets.abs.LCTargetPacket;
import lc.common.util.math.DimensionPos;

public class LCStargateConnectionPacket extends LCTargetPacket {

	/** The Stargate state */
	public StargateState state;
	/** The timeout until this state ends */
	public int stateTimeout;

	/** If this Stargate is the source */
	public boolean isSource;

	/** Create a blank Stargate packet */
	public LCStargateConnectionPacket() {
	}

	/**
	 * Create a new Stargate packet
	 *
	 * @param target
	 *            The target element
	 * @param state
	 *            The new stargate state
	 * @param stateTimeout
	 *            The state timeout
	 * @param isSource
	 *            If this connection is the source
	 */
	public LCStargateConnectionPacket(DimensionPos target, StargateState state, int stateTimeout, boolean isSource) {
		this.target = target;
		this.state = state;
		this.stateTimeout = stateTimeout;
		this.isSource = isSource;
	}

	@Override
	public void encodeInto(ByteBuf buffer) throws IOException {
		writeDimensionPosToBuffer(buffer, target);
		buffer.writeInt(state.ordinal());
		buffer.writeInt(stateTimeout);
		buffer.writeBoolean(isSource);
	}

	@Override
	public void decodeFrom(ByteBuf buffer) throws IOException {
		target = readDimensionPosFromBuffer(buffer);
		this.state = StargateState.values()[buffer.readInt()];
		this.stateTimeout = buffer.readInt();
		this.isSource = buffer.readBoolean();
	}
}
