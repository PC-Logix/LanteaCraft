package lc.common.configuration;

/**
 * XML save exception container
 *
 * @author AfterLifeLochie
 *
 */
public class XMLSaverException extends Exception {
	/**
	 *
	 */
	private static final long serialVersionUID = 5131700771210355969L;

	/**
	 * Creates a new XML save exception
	 *
	 * @param reason
	 *            The reason for the failure
	 */
	public XMLSaverException(String reason) {
		super(reason);
	}

	/**
	 * Creates a new XML save exception
	 *
	 * @param reason
	 *            The reason for the failure
	 * @param inner
	 *            The inner cause
	 */
	public XMLSaverException(String reason, Throwable inner) {
		super(reason, inner);
	}
}
