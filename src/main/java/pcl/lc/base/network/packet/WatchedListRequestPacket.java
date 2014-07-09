package pcl.lc.base.network.packet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import pcl.common.util.WorldLocation;
import pcl.lc.base.network.IStreamPackable;

/**
 * Sent from a client to the server to request the entire contents of a
 * WatchedList instance.
 * 
 * @author AfterLifeLochie
 * 
 */
public class WatchedListRequestPacket extends ModPacket {

	private WorldLocation origin;

	public WatchedListRequestPacket() {
	}

	public WatchedListRequestPacket(WorldLocation location) {
		this.origin = location;
	}

	@Override
	public boolean getPacketIsForServer() {
		return true;
	}

	@Override
	public String getType() {
		return "WatchedListRequestPacket";
	}

	@Override
	public WorldLocation getOriginLocation() {
		return origin;
	}

	@Override
	public void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer) throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		DataOutputStream data = new DataOutputStream(output);
		IStreamPackable<WorldLocation> packer = (IStreamPackable<WorldLocation>) ModPacket
				.findPacker(WorldLocation.class);
		if (origin == null)
			throw new IOException("Cannot encode void location packets.");
		packer.pack(origin, data);
		data.flush();
		data.close();
		output.flush();
		buffer.writeBytes(output.toByteArray());
	}

	@Override
	public void decodeFrom(ChannelHandlerContext ctx, ByteBuf buffer) throws IOException {
		byte[] b = new byte[buffer.readableBytes() - buffer.readerIndex()];
		buffer.readBytes(b);
		DataInputStream data = new DataInputStream(new ByteArrayInputStream(b));
		IStreamPackable<?> unpacker = ModPacket.findPacker(WorldLocation.class);
		origin = (WorldLocation) unpacker.unpack(data);
	}

}
