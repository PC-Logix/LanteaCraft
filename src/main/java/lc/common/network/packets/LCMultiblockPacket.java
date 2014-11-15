package lc.common.network.packets;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.io.IOException;

import lc.common.network.packets.abs.LCNBTPacket;
import lc.common.util.math.DimensionPos;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Multi-block network sync packet.
 *
 * @author AfterLifeLochie
 *
 */
public class LCMultiblockPacket extends LCNBTPacket {

	/** The tag compound */
	public NBTTagCompound compound;

	/** Create a blank sync packet */
	public LCMultiblockPacket() {
	}

	/**
	 * Create a new sync packet
	 *
	 * @param target
	 *            The target element
	 * @param compound
	 *            The tag compound
	 */
	public LCMultiblockPacket(DimensionPos target, NBTTagCompound compound) {
		this.target = target;
		this.compound = compound;
	}

	@Override
	public void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer) throws IOException {
		writeDimensionPosToBuffer(buffer, target);
		writeNBTTagCompoundToBuffer(buffer, compound);
	}

	@Override
	public void decodeFrom(ChannelHandlerContext ctx, ByteBuf buffer) throws IOException {
		target = readDimensionPosFromBuffer(buffer);
		compound = readNBTTagCompoundFromBuffer(buffer);
	}

}
