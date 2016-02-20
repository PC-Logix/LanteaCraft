package lc.digital.vm;

import java.io.IOException;

import lc.common.network.LCNetworkException;
import lc.common.network.packets.LCDevicePacket;

public class DeviceNetworkHelper {

	public static LCDevicePacket getStatePacket(Device device) throws LCNetworkException {
		try {
			return new LCDevicePacket(device);
		} catch (IOException ioex) {
			throw new LCNetworkException("Can't write device state", ioex);
		}
	}

	public static LCDevicePacket generateStatePacket(Device device) throws LCNetworkException {
		if (device.modified())
			return getStatePacket(device);
		return null;
	}

	public static void applyStatePacket(LCDevicePacket packet, Device device) throws LCNetworkException {
		try {
			packet.apply(device);
		} catch (IOException ioex) {
			throw new LCNetworkException("Can't read device state", ioex);
		}
	}

}
