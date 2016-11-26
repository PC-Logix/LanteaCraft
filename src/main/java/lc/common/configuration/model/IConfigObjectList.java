package lc.common.configuration.model;

/**
 * The model for configuration list types. All configuration objects which
 * implement list types must implement this interface.
 * 
 * @author AfterLifeLochie
 *
 */
public interface IConfigObjectList {

	/**
	 * Add a child configuration object to this list. If it already exists as a
	 * leaf, do nothing.
	 * 
	 * @param child
	 *            The child to add
	 */
	public void addChild(IConfigObject child);

	/**
	 * Remove a child configuration object from this list. If it does not exist
	 * as a leaf, do nothing.
	 * 
	 * @param child
	 */
	public void removeChild(IConfigObject child);

}
