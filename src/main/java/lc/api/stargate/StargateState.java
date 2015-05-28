package lc.api.stargate;

public enum StargateState {
	/** Nothing is happening */
	IDLE,
	/** The gate is dialling something */
	DIALLING,

	/** The gate has connected */
	CONNECTED,
	/** The gate failed to connect */
	FAILED,

	/** The gate is disconnecting */
	DISCONNECTING;

	public static StargateState nextState(StargateState state) {
		switch (state) {
		case CONNECTED:
			return DISCONNECTING;
		case DIALLING:
			return CONNECTED;
		case DISCONNECTING:
			return IDLE;
		case FAILED:
			return IDLE;
		case IDLE:
		default:
			return IDLE;
		}
	}

	public StargateState nextState() {
		return StargateState.nextState(this);
	}
}
