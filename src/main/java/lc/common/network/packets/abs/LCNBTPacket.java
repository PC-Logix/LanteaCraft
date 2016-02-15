package lc.common.network.packets.abs;

import io.netty.buffer.ByteBuf;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.IOException;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTSizeTracker;
import net.minecraft.nbt.NBTTagCompound;

/**
 * NBT packet type implementation
 *
 * @author AfterLifeLochie
 *
 */
public abstract class LCNBTPacket extends LCTargetPacket {

	@Override
	public abstract void encodeInto(ByteBuf buffer) throws IOException;

	@Override
	public abstract void decodeFrom(ByteBuf buffer) throws IOException;

	/**
	 * Read an NBT tag compound from the byte buffer input at the current read
	 * pointer.
	 *
	 * @param buffer
	 *            The buffer
	 * @return A tag compound
	 * @throws IOException
	 *             Any I/O problem which occurs
	 */
	public NBTTagCompound readNBTTagCompoundFromBuffer(ByteBuf buffer) throws IOException {
		short size = buffer.readShort();
		if (size < 0)
			return null;
		if (size == 0)
			return new NBTTagCompound();
		byte[] bytes = new byte[size];
		buffer.readBytes(bytes);
		ByteArrayInputStream ios = new ByteArrayInputStream(bytes);
		return CompressedStreamTools.readCompressed(ios);
	}

	/**
	 * Write an NBT tag compound to the byte buffer output at the current write
	 * pointer.
	 *
	 * @param buffer
	 *            The buffer
	 * @param tag
	 *            A tag compound
	 * @throws IOException
	 *             Any I/O problem which occurs
	 */
	public void writeNBTTagCompoundToBuffer(ByteBuf buffer, NBTTagCompound tag) throws IOException {
		if (tag == null)
			buffer.writeShort(-1);
		else if (tag.hasNoTags())
			buffer.writeShort(0);
		else {
			ByteArrayOutputStream ois = new ByteArrayOutputStream();
			CompressedStreamTools.writeCompressed(tag, ois);
			byte[] bytes = ois.toByteArray();
			buffer.writeShort((short) bytes.length);
			buffer.writeBytes(bytes);
		}
	}
}
