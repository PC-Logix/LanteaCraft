package pcl.common.network;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.minecraft.network.packet.Packet250CustomPayload;

public class TinyModPacket extends ModPacket {

	private DataInputStream instream;
	private DataOutputStream outstream;
	private ByteArrayOutputStream outbuff;
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
		TinyModPacket pkt = new TinyModPacket(data);
		pkt.setIsForServer(data.readByte() == 1);
		return pkt;
	}

	public TinyModPacket() {
		outbuff = new ByteArrayOutputStream();
		outstream = new DataOutputStream(outbuff);
	}

	public TinyModPacket(DataInputStream data) {
		instream = data;
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
		bytes.write(1); // packet typeof
		bytes.write((toServer) ? 1 : 0);
		try {
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
