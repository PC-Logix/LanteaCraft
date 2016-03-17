package lc.digital.vm.peripheral;

import lc.digital.vm.IDeviceAccess;

public interface ILCPeripheral {

	public String getLCPType();

	public String[] getLCPMethods();

	public Object invokeLCPMethod(String label, Object[] arguments) throws Exception;

	public void onLCPConnect(IDeviceAccess network);

	public void onLCPDisconnect(IDeviceAccess network);

}
