package pcl.lc.cfg;

public class XMLSaverException extends Exception {
	public XMLSaverException(String reason) {
		super(reason);
	}

	public XMLSaverException(String reason, Throwable inner) {
		super(reason, inner);
	}
}
