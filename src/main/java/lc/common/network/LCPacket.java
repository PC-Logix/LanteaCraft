package lc.common.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.io.IOException;

/**
 * Generic packet implementation
 * 
 * @author AfterLifeLochie
 * 
 */
public abstract class LCPacket {

	/**
	 * Encode a packet into the network stream.
	 * 
	 * @param ctx
	 *            The handler context
	 * @param buffer
	 *            The write buffer
	 * @throws IOException
	 *             If a problem occurs, an I/O exception may be thrown.
	 */
	public abstract void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer) throws IOException;

	/**
	 * Decode a packet from the network stream.
	 * 
	 * @param ctx
	 *            The handler context
	 * @param buffer
	 *            The read buffer
	 * @throws IOException
	 *             If a problem occurs, an I/O exception may be thrown.
	 */
	public abstract void decodeFrom(ChannelHandlerContext ctx, ByteBuf buffer) throws IOException;

}
