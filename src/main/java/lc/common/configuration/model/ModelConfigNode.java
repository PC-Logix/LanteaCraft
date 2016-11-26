package lc.common.configuration.model;

import java.lang.ref.WeakReference;
import java.util.HashMap;

/**
 * <p>
 * The generic ConfigurationNode type. Configuration implementations can extend
 * this model to leverage basic node features by default (from
 * {@link IConfigObject}).
 * </p>
 * 
 * @author AfterLifeLochie
 *
 */
public class ModelConfigNode implements IConfigObject {

	/**
	 * The name of the object.
	 */
	private String name;

	/**
	 * The comment of the object (if set).
	 */
	private String comment;

	/**
	 * The object's parameter map (if set).
	 */
	private HashMap<String, Object> parameters;

	/**
	 * The parent node, if it exists.
	 */
	private WeakReference<ModelConfigNode> parent;

	/**
	 * The modified state flag.
	 */
	private boolean modified;

	/** Default constructor, no initial values */
	public ModelConfigNode() {
		this(null, null, null);
	}

	/**
	 * Named configuration node
	 *
	 * @param name
	 *            The name
	 */
	public ModelConfigNode(String name) {
		this(name, null, null);
	}

	/**
	 * Named and parented configuration node
	 *
	 * @param name
	 *            The name
	 * @param parent
	 *            The parent node
	 */
	public ModelConfigNode(String name, ModelConfigNode parent) {
		this(name, null, parent);
	}

	/**
	 * Named and commented configuration node
	 *
	 * @param name
	 *            The name
	 * @param comment
	 *            A comment
	 */
	public ModelConfigNode(String name, String comment) {
		this(name, comment, null);
	}

	/**
	 * Named, commented and parented configuration node
	 *
	 * @param name
	 *            The name
	 * @param comment
	 *            A comment
	 * @param parent
	 *            The parent node
	 */
	public ModelConfigNode(String name, String comment, ModelConfigNode parent) {
		this.name = name;
		this.comment = comment;
		this.parent = new WeakReference<ModelConfigNode>(parent);
		parameters = new HashMap<String, Object>();
	}

	@Override
	public String name() {
		return name;
	}

	/**
	 * Set the node name.
	 *
	 * @param name
	 *            The new name.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Get the comment for the node.
	 *
	 * @return The node comment
	 */
	public String comment() {
		return comment;
	}

	/**
	 * Set the comment for the node.
	 *
	 * @param comment
	 *            The new comment
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}

	/**
	 * Get the node parameters
	 *
	 * @return A list of parameters
	 */
	public HashMap<String, Object> parameters() {
		return parameters;
	}

	/**
	 * Set the node parameters
	 *
	 * @param parameters
	 *            A list of parameters
	 */
	public void setParameters(HashMap<String, Object> parameters) {
		this.parameters = parameters;
	}

	@Override
	public IConfigObject getParent() {
		return (IConfigObject) parent.get();
	}

	/** Flag the node as modified */
	public void modify() {
		modified = true;
		if (parent != null && parent.get() != null)
			parent.get().modify();
	}

	/**
	 * Get the modified node state.
	 *
	 * @return If the node has been modified
	 */
	public boolean modified() {
		return modified;
	}

	@Override
	public void visit(IConfigObjectVisitor visitor) {
		visitor.visitNode(this);
		visitor.finishVisitNode(this);
	}
}
