package lc.common.configuration;

/**
 * XML parse exception container
 * 
 * @author AfterLifeLochie
 * 
 */
public class XMLParserException extends Exception {
	/**
	 * Creates a new XML parse exception
	 * 
	 * @param reason
	 *            The reason for the failure
	 */
	public XMLParserException(String reason) {
		super(reason);
	}

	/**
	 * Creates a new XML parse exception
	 * 
	 * @param reason
	 *            The reason for the failure
	 * @param inner
	 *            The inner cause
	 */
	public XMLParserException(String reason, Throwable inner) {
		super(reason, inner);
	}
}