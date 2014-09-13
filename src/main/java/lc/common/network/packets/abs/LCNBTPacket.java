package lc.common.network.packets.abs;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.io.IOException;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTSizeTracker;
import net.minecraft.nbt.NBTTagCompound;

public abstract class LCNBTPacket extends LCTargetPacket {

	@Override
	public abstract void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer) throws IOException;

	public abstract void decodeFrom(ChannelHandlerContext ctx, ByteBuf buffer) throws IOException;

	public NBTTagCompound readNBTTagCompoundFromBuffer(ByteBuf buffer) throws IOException {
		short size = buffer.readShort();
		if (size < 0)
			return null;
		byte[] bytes = new byte[size];
		buffer.readBytes(bytes);
		return CompressedStreamTools.func_152457_a(bytes, new NBTSizeTracker(2097152L));
	}

	public void writeNBTTagCompoundToBuffer(ByteBuf buffer, NBTTagCompound tag) throws IOException {
		if (tag == null)
			buffer.writeShort(-1);
		else {
			byte[] bytes = CompressedStreamTools.compress(tag);
			buffer.writeShort((short) bytes.length);
			buffer.writeBytes(bytes);
		}
	}
}
