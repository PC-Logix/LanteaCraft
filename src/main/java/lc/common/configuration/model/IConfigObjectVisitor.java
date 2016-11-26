package lc.common.configuration.model;

/**
 * <p>
 * Configuration object visitor model. Classes which implement this interface
 * may walk the configuration object tree at any node in the tree.
 * </p>
 * 
 * @author AfterLifeLochie
 *
 */
public interface IConfigObjectVisitor {

	/**
	 * <p>
	 * Called by the visit chain to indicate the entry into a new configuration
	 * object node.
	 * </p>
	 * 
	 * @param node
	 *            The node being entered
	 */
	public void visitNode(IConfigObject node);

	/**
	 * <p>
	 * Called by the visit chain to indicate the exit from an entered
	 * configuration object node.
	 * 
	 * @param node
	 *            The node being exited
	 */
	public void finishVisitNode(IConfigObject node);

}
