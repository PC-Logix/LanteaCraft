package pcl.lc.base.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.logging.log4j.Level;

import pcl.lc.LanteaCraft;
import pcl.lc.base.network.packet.ModPacket;

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
			LanteaCraft.getLogger().log(Level.WARN, "Could not create PacketLogger, an error occured.", ioex);
		}
	}

	public void close() {
		try {
			datastream.flush();
			datastream.close();
			outstream.flush();
			outstream.close();
		} catch (IOException ioex) {
			LanteaCraft.getLogger().log(Level.WARN, "Could not close PacketLogger, an error occured.", ioex);
		}
	}

	public void logPacket(ModPacket packet) {
		if (datastream == null)
			return;
		try {
			ByteBuf virt_buff = Unpooled.buffer();
			packet.encodeInto(null, virt_buff);
			datastream.writeLong(packetcount++);
			datastream.writeLong(virt_buff.array().length);
			datastream.write(virt_buff.array());
			// Prevent unhandled overflows
			if (packetcount >= Long.MAX_VALUE - 1L)
				packetcount %= Long.MAX_VALUE - 1L;
		} catch (IOException ioex) {
			LanteaCraft.getLogger().log(Level.WARN, "Could not pack ModPacket into PacketLogger stream.", ioex);
		}
	}
}
