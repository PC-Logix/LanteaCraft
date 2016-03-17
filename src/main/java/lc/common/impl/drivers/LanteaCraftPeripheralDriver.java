package lc.common.impl.drivers;

import java.lang.reflect.Method;
import java.util.ArrayList;

import lc.api.components.IntegrationType;
import lc.api.jit.DeviceDrivers.DriverProvider;
import lc.api.jit.DeviceDrivers.DriverRTCallback;
import lc.api.jit.ASMTag;
import lc.api.jit.Tag;
import lc.common.LCLog;
import lc.digital.network.INetwork;
import lc.digital.vm.IDeviceAccess;
import lc.digital.vm.peripheral.ILCPeripheral;

@DriverProvider(type = IntegrationType.COMPUTERS)
public class LanteaCraftPeripheralDriver implements ILCPeripheral {

	private String[] lcpd_methodcache;
	private ArrayList<IDeviceAccess> lcpd_devices;
	
	private void lcpd_assertReady() {
		if (this.lcpd_devices == null) 
			this.lcpd_devices = new ArrayList<IDeviceAccess>();
	}

	@DriverRTCallback(event = "computerEvent")
	public void lcpd_handleEvent(String event, Object... args) {
		lcpd_assertReady();
		for (IDeviceAccess network : lcpd_devices)
			network.signal(this, event, args);
	}

	@Override
	public String getLCPType() {
		return getClass().getSimpleName().replace("Tile", "").replace("tile", "");
	}

	@Override
	public String[] getLCPMethods() {
		if (lcpd_methodcache == null) {
			ArrayList<String> alist = new ArrayList<String>();
			Class<?> zz = getClass();
			Method[] methods = zz.getMethods();
			for (Method m : methods) {
				LCLog.debug("LanteaCraft driver: assessing method %s (class %s).", m.getName(), zz.getSimpleName());
				Tag foundTag = ASMTag.findTag(getClass(), m, "ComputerCallable");
				if (foundTag == null)
					continue;
				LCLog.debug("LanteaCraft driver: adding method %s", m.getName());
				alist.add(m.getName());
			}
			lcpd_methodcache = alist.toArray(new String[0]);
		}
		return (lcpd_methodcache == null) ? new String[0] : lcpd_methodcache;
	}

	@Override
	public Object invokeLCPMethod(String label, Object[] arguments) throws Exception {
		Method foundMethod = null;
		for (Method m : getClass().getMethods())
			if (m.getName().equals(label))
				foundMethod = m;
		if (foundMethod == null)
			throw new Exception("No such method.");
		try {
			Class<?>[] types = foundMethod.getParameterTypes();
			if (arguments.length != types.length)
				throw new Exception("Incorrect number of parameters.");
			return foundMethod.invoke(this, arguments);
		} catch (Exception exception) {
			LCLog.warn("Problem calling method from LC proxy driver!", exception);
			throw new Exception(exception.getMessage());
		}
	}

	@Override
	public void onLCPConnect(IDeviceAccess network) {
		lcpd_assertReady();
		this.lcpd_devices.add(network);
	}

	@Override
	public void onLCPDisconnect(IDeviceAccess network) {
		lcpd_assertReady();
		this.lcpd_devices.remove(network);
	}
}
