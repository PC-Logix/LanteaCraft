package lc.common.configuration.model;

/**
 * The model for configuration objects. All configuration objects must implement
 * this interface.
 * 
 * @author AfterLifeLochie
 *
 */
public interface IConfigObject {

	/**
	 * <p>
	 * Method to allow an {@link IConfigObjectVisitor} configuration object
	 * visitor to visit this {@link IConfigObject} configuration object within
	 * the configuration tree. The implementation of this method must notify the
	 * configuration object visitor of this object.
	 * </p>
	 * 
	 * <p>
	 * If an exception is thrown during the call to notify the configuration
	 * object visitor of this configuration object, you must ignore the
	 * exception. Exceptions must not be thrown from this method; the only
	 * uncaught exceptions that may ever exist must originate from the
	 * configuration object visitor code.
	 * </p>
	 * 
	 * <p>
	 * If this configuration object is not a leaf node type, you must invoke the
	 * {{@link #visit(IConfigObjectVisitor)} method on your child
	 * {@link IConfigObject} object(s). You must perform logical descent only;
	 * do not call {{@link #visit(IConfigObjectVisitor)} on the parent object.
	 * </p>
	 * 
	 * <p>
	 * When you have finished notifying the configuration object visitor of this
	 * object and after invoking the {{@link #visit(IConfigObjectVisitor)}
	 * method on any child configuration objects, you must notify the
	 * configuration object visitor that you have finished visiting this object.
	 * </p>
	 * 
	 * @param visitor
	 *            The configuration object visitor.
	 */
	public void visit(IConfigObjectVisitor visitor);

	/**
	 * Get the name of the node.
	 *
	 * @return The node name
	 */
	public String name();

	/**
	 * Get the parent node if it is known and not garbage collected. If this is
	 * a root node, return always null.
	 * 
	 * @return The parent node, or null if it is a root node, or null if the
	 *         part of the tree relevant has been garbage collected already
	 */
	public IConfigObject getParent();

}
