package lc.common.configuration.model;

import java.util.List;

/**
 * <p>
 * The generic ConfigurationList type. Configuration implementations can extend
 * this model to leverage basic list features by default (from
 * {@link IConfigObjectList}).
 * </p>
 * 
 * @author AfterLifeLochie
 *
 */
public class ModelConfigList extends ModelConfigNode implements IConfigObjectList {

	/** Default constructor, no initial values */
	public ModelConfigList() {
		super(null, null, null);
	}

	/**
	 * Named configuration list
	 *
	 * @param name
	 *            The name
	 */
	public ModelConfigList(String name) {
		super(name, null, null);
	}

	/**
	 * Named and parented configuration list
	 *
	 * @param name
	 *            The name
	 * @param parent
	 *            The parent node
	 */
	public ModelConfigList(String name, ModelConfigNode parent) {
		super(name, null, parent);
	}

	/**
	 * Named and commented configuration list
	 *
	 * @param name
	 *            The name
	 * @param comment
	 *            A comment
	 */
	public ModelConfigList(String name, String comment) {
		super(name, comment, null);
	}

	/**
	 * Named, commented and parented configuration list
	 *
	 * @param name
	 *            The name
	 * @param comment
	 *            A comment
	 * @param parent
	 *            The parent node
	 */
	public ModelConfigList(String name, String comment, ModelConfigNode parent) {
		super(name, comment, parent);
	}

	/**
	 * The list of children.
	 */
	private List<IConfigObject> children;

	@Override
	public void addChild(IConfigObject child) {
		if (!children.contains(child))
			children.add(child);
	}

	@Override
	public void removeChild(IConfigObject child) {
		children.remove(child);
	}

	@Override
	public void visit(IConfigObjectVisitor visitor) {
		visitor.visitNode(this);
		for (IConfigObject o : children)
			o.visit(visitor);
		visitor.finishVisitNode(this);
	}

}
