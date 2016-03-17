package lc.digital.network;

public interface INetwork {

	void sendEvent(INetDevice device, String event, Object[] data);

}
