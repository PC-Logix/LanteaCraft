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
}
