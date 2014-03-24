package pcl.common.network;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import pcl.common.util.WorldLocation;
import net.minecraft.network.packet.Packet250CustomPayload;

public class TinyModPacket extends ModPacket {

	private DataInputStream instream;
	private DataOutputStream outstream;
	private ByteArrayOutputStream outbuff;
	private WorldLocation origin;
	private boolean toServer = false;

	/**
	 * Reads a new TinyModPacket
	 * 
	 * @param data
	 *            The data stream
	 * @return The TinyModPacket result
	 * @throws IOException
	 *             Any network or read exception
	 */
	public static TinyModPacket createPacket(DataInputStream data) throws IOException {
		boolean isServer = (data.readByte() == 1);
		IStreamPackable<?> unpacker = ModPacket.findPacker(WorldLocation.class);
		WorldLocation location = (WorldLocation) unpacker.unpack(data);
		TinyModPacket pkt = new TinyModPacket(data, location);
		pkt.setIsForServer(isServer);
		return pkt;
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

	/**
	 * Converts this packet instance into a Forge payload packet
	 * 
	 * @return A custom Packet250CustomPayload packet for Forge networking
	 */
	@Override
	public Packet250CustomPayload toPacket() {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		Packet250CustomPayload pkt = new Packet250CustomPayload();
		try {
			bytes.write(1);
			bytes.write((toServer) ? 1 : 0);
			IStreamPackable<WorldLocation> packer = (IStreamPackable<WorldLocation>) ModPacket
					.findPacker(WorldLocation.class);
			DataOutputStream wrapper = new DataOutputStream(bytes);
			packer.pack(origin, wrapper);
			wrapper.flush();
			wrapper.close();
			outstream.flush();
			outstream.close();
			bytes.write(outbuff.toByteArray());
		} catch (IOException e) {
			Logger.getLogger("pcl.common").log(Level.WARNING, "Exception when writing packet!", e);
		}
		pkt.data = bytes.toByteArray();
		pkt.length = pkt.data.length;
		return pkt;
	}

	@Override
	public String getType() {
		return "TinyPacket";
	}
}
