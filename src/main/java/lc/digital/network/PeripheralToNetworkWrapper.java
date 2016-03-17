package lc.digital.network;

import net.minecraft.tileentity.TileEntity;
import lc.api.components.DriverMap;
import lc.common.impl.drivers.LanteaCraftDriverManager;
import lc.digital.vm.IDeviceAccess;
import lc.digital.vm.peripheral.ILCPeripheral;

public class PeripheralToNetworkWrapper implements INetDevice, IDeviceAccess {

	private TileEntity tile;
	private ILCPeripheral peripheral;
	private INetwork network;

	public PeripheralToNetworkWrapper(TileEntity tile) {
		Object mpObject = DriverMap.LANTEACRAFT.managerObject;
		LanteaCraftDriverManager dm = (LanteaCraftDriverManager) mpObject;
		this.tile = tile;
		this.peripheral = dm.getPeripheral(tile.getWorldObj(), tile.xCoord, tile.yCoord, tile.zCoord);
	}

	@Override
	public NetDevicePosition position() {
		return new NetDevicePosition(tile.xCoord, tile.yCoord, tile.zCoord);
	}

	@Override
	public void setNetwork(INetwork network) {
		if (this.network != null)
			this.peripheral.onLCPDisconnect(this);
		this.network = network;
		if (this.network != null)
			this.peripheral.onLCPConnect(this);
	}

	@Override
	public void signal(ILCPeripheral peripheral, String label, Object[] data) {
		network.sendEvent(this, label, data);
	}

	@Override
	public void receiveEvent(INetDevice addresser, String event, Object[] data) {
		// TODO Auto-generated method stub
		
	}

}
