package lc.common.network.packets;

import io.netty.buffer.ByteBuf;
import java.io.IOException;

import net.minecraft.nbt.NBTTagCompound;
import lc.common.network.packets.abs.LCNBTPacket;
import lc.common.util.math.DimensionPos;

public class LCDHDPacket extends LCNBTPacket {

	/** The tag compound */
	public NBTTagCompound compound;

	/** Create a blank DHD packet */
	public LCDHDPacket() {
	}

	/**
	 * Create a new DHD packet
	 *
	 * @param target
	 *            The target element
	 * @param compound
	 *            The tag compound
	 */
	public LCDHDPacket(DimensionPos target, NBTTagCompound compound) {
		this.target = target;
		this.compound = compound;
	}

	@Override
	public void encodeInto(ByteBuf buffer) throws IOException {
		writeDimensionPosToBuffer(buffer, target);
		writeNBTTagCompoundToBuffer(buffer, compound);
	}

	@Override
	public void decodeFrom(ByteBuf buffer) throws IOException {
		target = readDimensionPosFromBuffer(buffer);
		compound = readNBTTagCompoundFromBuffer(buffer);
	}

}
