package lc.digital.network;

public interface INetDevice {

	public NetDevicePosition position();

	public void setNetwork(INetwork network);

	public void receiveEvent(INetDevice addresser, String event, Object[] data);
}
