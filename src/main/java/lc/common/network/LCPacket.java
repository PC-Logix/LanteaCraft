package lc.common.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.io.IOException;

public abstract class LCPacket {

	public abstract void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer) throws IOException;

	public abstract void decodeFrom(ChannelHandlerContext ctx, ByteBuf buffer) throws IOException;

}
