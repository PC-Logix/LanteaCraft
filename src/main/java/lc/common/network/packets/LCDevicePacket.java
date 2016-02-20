package lc.common.network.packets;

import io.netty.buffer.ByteBuf;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import lc.common.network.LCPacket;
import lc.digital.vm.Device;

public class LCDevicePacket extends LCPacket {

	private byte[] device;

	public LCDevicePacket() {
		this.device = null;
	}

	public LCDevicePacket(Device device) throws IOException {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		device.serialize(new DataOutputStream(stream));
		this.device = stream.toByteArray();
	}
	
	public void apply(Device device) throws IOException {
		ByteArrayInputStream stream = new ByteArrayInputStream(this.device);
		device.unserialize(new DataInputStream(stream));
	}

	@Override
	public void encodeInto(ByteBuf buffer) throws IOException {
		buffer.writeInt(device.length);
		buffer.writeBytes(device);
	}

	@Override
	public void decodeFrom(ByteBuf buffer) throws IOException {
		int length = buffer.readInt();
		if (length == 0)
			this.device = new byte[0];
		else {
			this.device = new byte[length];
			buffer.readBytes(this.device);
		}
	}

}
