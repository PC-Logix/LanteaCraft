package lc.api.stargate;

public interface IStargateAccess {

	public StargateType getStargateType();

	public StargateAddress getStargateAddress();

	public IrisType getIrisType();

	public IrisState getIrisState();
	
	public void transmit(MessagePayload payload);
	
	public void receive(MessagePayload payload);

}
