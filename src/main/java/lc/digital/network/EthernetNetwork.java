package lc.digital.network;

import java.util.HashMap;
import java.util.Iterator;

public class EthernetNetwork implements INetwork {

	private HashMap<NetDevicePosition, INetDevice> devices;

	public boolean addDevice(INetDevice device) {
		if (devices.containsKey(device.position()))
			return false;
		if (devices.containsValue(device))
			return false;
		devices.put(device.position(), device);
		return true;
	}

	public void removeDevice(INetDevice device) {
		devices.remove(device.position());
	}

	public void mergeNetwork(EthernetNetwork that) {
		Iterator<INetDevice> q = that.devices.values().iterator();
		while (q.hasNext()) {
			INetDevice device = q.next();
			that.removeDevice(device);
			this.addDevice(device);
		}
	}

	@Override
	public void sendEvent(INetDevice device, String event, Object[] data) {
		if (!devices.containsValue(device))
			return;
		Iterator<INetDevice> q = devices.values().iterator();
		while (q.hasNext()) {
			INetDevice d = q.next();
			if (d.equals(device))
				continue;
			d.receiveEvent(device, event, data);
		}

	}

}
