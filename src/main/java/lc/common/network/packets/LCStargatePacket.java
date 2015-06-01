package lc.common.network.packets;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.io.IOException;

import lc.api.stargate.StargateState;
import lc.common.network.packets.abs.LCTargetPacket;
import lc.common.util.math.DimensionPos;

public class LCStargatePacket extends LCTargetPacket {

	/** The Stargate state */
	public StargateState state;
	/** The timeout until this state ends */
	public int stateTimeout;

	/** The dial-progress */
	public int diallingProgress;
	/** The dial-state timeout */
	public int diallingTimeout;

	/** Create a blank Stargate packet */
	public LCStargatePacket() {
	}

	/**
	 * Create a new Stargate packet
	 *
	 * @param target
	 *            The target element
	 */
	public LCStargatePacket(DimensionPos target, StargateState state, int stateTimeout, int diallingProgress,
			int diallingTimeout) {
		this.target = target;
		this.state = state;
		this.stateTimeout = stateTimeout;
		this.diallingProgress = diallingProgress;
		this.diallingTimeout = diallingTimeout;
	}

	@Override
	public void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer) throws IOException {
		writeDimensionPosToBuffer(buffer, target);
		buffer.writeInt(state.ordinal());
		buffer.writeInt(stateTimeout);
		buffer.writeInt(diallingProgress);
		buffer.writeInt(diallingTimeout);
	}

	@Override
	public void decodeFrom(ChannelHandlerContext ctx, ByteBuf buffer) throws IOException {
		target = readDimensionPosFromBuffer(buffer);
		this.state = StargateState.values()[buffer.readInt()];
		this.stateTimeout = buffer.readInt();
		this.diallingProgress = buffer.readInt();
		this.diallingTimeout = buffer.readInt();
	}
}
