package pcl.lc.api.internal;

/**
 * Represents an object which requires regulated advance updates.
 * 
 * @author AfterLifeLochie
 */
public interface ITickAgent {
	/**
	 * Perform any extended processing or logic.
	 */
	public void advance();
}
