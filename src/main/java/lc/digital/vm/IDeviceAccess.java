package lc.digital.vm;

import lc.digital.vm.peripheral.ILCPeripheral;

public interface IDeviceAccess {

	public void signal(ILCPeripheral peripheral, String label, Object[] data);

}
