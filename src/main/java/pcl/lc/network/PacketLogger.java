package pcl.lc.network;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;

import net.minecraft.network.packet.Packet250CustomPayload;

import pcl.common.network.ModPacket;
import pcl.lc.LanteaCraft;

public class PacketLogger {
	private final File file;
	private FileOutputStream outstream;
	private DataOutputStream datastream;
	private long packetcount = 0L;

	public PacketLogger(File file) {
		this.file = file;
	}

	public void open() {
		try {
			if (outstream != null)
				throw new IOException("Can't reopen an existing PacketLogger.");
			outstream = new FileOutputStream(file, true);
			datastream = new DataOutputStream(outstream);
		} catch (IOException ioex) {
			LanteaCraft.getLogger().log(Level.WARNING, "Could not create PacketLogger, an error occured.", ioex);
		}
	}

	public void close() {
		try {
			datastream.flush();
			datastream.close();
			outstream.flush();
			outstream.close();
		} catch (IOException ioex) {
			LanteaCraft.getLogger().log(Level.WARNING, "Could not close PacketLogger, an error occured.", ioex);
		}
	}

	public void logPacket(ModPacket packet) {
		Packet250CustomPayload payload = packet.toPacket();
		try {
			datastream.writeLong(packetcount++);
			datastream.writeLong(payload.data.length);
			datastream.write(payload.data);
			// Prevent unhandled overflows
			if (packetcount >= Long.MAX_VALUE - 1L)
				packetcount %= Long.MAX_VALUE - 1L;
		} catch (IOException ioex) {
			LanteaCraft.getLogger().log(Level.WARNING, "Could not pack ModPacket into PacketLogger stream.", ioex);
		}
	}
}
