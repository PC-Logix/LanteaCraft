package lc.common.network;

/**
 * Network exception class.
 *
 * @author AfterLifeLochie
 *
 */
public class LCNetworkException extends Exception {

	/**
	 *
	 */
	private static final long serialVersionUID = -4524788574933106545L;

	/**
	 * Create a new network exception.
	 *
	 * @param reason
	 *            The reason for the exception.
	 */
	public LCNetworkException(String reason) {
		super(reason);
	}

	/**
	 * Create a new network exception.
	 *
	 * @param reason
	 *            The reason for the exception.
	 * @param t
	 *            The cause of the exception (parent throwable).
	 */
	public LCNetworkException(String reason, Throwable t) {
		super(reason, t);
	}

}
