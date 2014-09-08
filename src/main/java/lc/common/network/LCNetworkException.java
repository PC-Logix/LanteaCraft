package lc.common.network;

public class LCNetworkException extends Exception {

	public LCNetworkException(String reason) {
		super(reason);
	}

	public LCNetworkException(String reason, Throwable t) {
		super(reason, t);
	}

}
