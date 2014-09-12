package lc.common.network.packets;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.io.IOException;

import lc.common.network.packets.abs.LCNBTPacket;

public class LCTileSync extends LCNBTPacket {

	@Override
	public void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void decodeFrom(ChannelHandlerContext ctx, ByteBuf buffer) throws IOException {
		// TODO Auto-generated method stub
		
	}

}
