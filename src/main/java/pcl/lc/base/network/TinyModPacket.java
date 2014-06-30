package pcl.lc.base.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import pcl.common.util.WorldLocation;

public class TinyModPacket extends ModPacket {

	private DataInputStream instream;
	private DataOutputStream outstream;
	private ByteArrayOutputStream outbuff;
	private WorldLocation origin;
	private boolean toServer = false;

	public TinyModPacket() {
	}

	public TinyModPacket(WorldLocation creationOrigin) {
		outbuff = new ByteArrayOutputStream();
		outstream = new DataOutputStream(outbuff);
		origin = creationOrigin;
	}

	public TinyModPacket(DataInputStream data, WorldLocation sourceOrigin) {
		instream = data;
		origin = sourceOrigin;
	}

	@Override
	public WorldLocation getOriginLocation() {
		return origin;
	}

	public DataInputStream getIn() {
		return instream;
	}

	public DataOutputStream getOut() {
		return outstream;
	}

	public void setIsForServer(boolean b) {
		toServer = b;
	}

	@Override
	public boolean getPacketIsForServer() {
		return toServer;
	}

	@Override
	public String getType() {
		return "TinyPacket";
	}

	@SuppressWarnings("unchecked")
	@Override
	public void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer) throws IOException {
		buffer.writeByte((toServer) ? 1 : 0);
		IStreamPackable<WorldLocation> packer = (IStreamPackable<WorldLocation>) ModPacket
				.findPacker(WorldLocation.class);
		ByteArrayOutputStream vec = new ByteArrayOutputStream();
		DataOutputStream wrapper = new DataOutputStream(vec);
		packer.pack(origin, wrapper);
		wrapper.flush();
		wrapper.close();
		buffer.writeBytes(vec.toByteArray());
		outstream.flush();
		outstream.close();
		buffer.writeBytes(outbuff.toByteArray());
	}

	@Override
	public void decodeFrom(ChannelHandlerContext ctx, ByteBuf buffer) throws IOException {
		byte[] b = new byte[buffer.readableBytes() - buffer.readerIndex()];
		buffer.readBytes(b);
		DataInputStream data = new DataInputStream(new ByteArrayInputStream(b));
		IStreamPackable<?> unpacker = ModPacket.findPacker(WorldLocation.class);
		toServer = data.readByte() == 1;
		origin = (WorldLocation) unpacker.unpack(data);
		instream = data;
	}
}
