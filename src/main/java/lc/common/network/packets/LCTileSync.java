package lc.common.network.packets;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.io.IOException;

import net.minecraft.nbt.NBTTagCompound;
import lc.common.network.packets.abs.LCNBTPacket;
import lc.common.util.math.DimensionPos;

/**
 * Tile synchronization packet
 * 
 * @author AfterLifeLochie
 * 
 */
public class LCTileSync extends LCNBTPacket {

	/** The tag compound */
	public NBTTagCompound compound;

	/** Create a blank sync packet */
	public LCTileSync() {
	}

	/**
	 * Create a new sync packet
	 * 
	 * @param target
	 *            The target element
	 * @param compound
	 *            The tag compound
	 */
	public LCTileSync(DimensionPos target, NBTTagCompound compound) {
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
