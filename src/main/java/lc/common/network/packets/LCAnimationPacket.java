package lc.common.network.packets;

import io.netty.buffer.ByteBuf;
import java.io.IOException;

import net.minecraft.nbt.NBTTagCompound;
import lc.common.network.packets.abs.LCNBTPacket;
import lc.common.util.math.DimensionPos;

/**
 * Animation trigger packet. Only to be sent from server to client(s).
 * 
 * @author AfterLifeLochie
 */
public class LCAnimationPacket extends LCNBTPacket {

	/** The animation ID */
	public int animationId;

	/** The tag compound */
	public NBTTagCompound compound;

	/** Create a blank animation packet */
	public LCAnimationPacket() {
	}

	/**
	 * Create a new animation packet
	 *
	 * @param target
	 *            The target element
	 * @param animationId
	 *            The animation ID
	 * @param compound
	 *            The tag compound
	 */
	public LCAnimationPacket(DimensionPos target, int animationId, NBTTagCompound compound) {
		this.animationId = animationId;
		this.target = target;
		this.compound = compound;
	}

	@Override
	public void encodeInto(ByteBuf buffer) throws IOException {
		writeDimensionPosToBuffer(buffer, target);
		buffer.writeInt(animationId);
		writeNBTTagCompoundToBuffer(buffer, compound);
	}

	@Override
	public void decodeFrom(ByteBuf buffer) throws IOException {
		target = readDimensionPosFromBuffer(buffer);
		animationId = buffer.readInt();
		compound = readNBTTagCompoundFromBuffer(buffer);
	}
}
