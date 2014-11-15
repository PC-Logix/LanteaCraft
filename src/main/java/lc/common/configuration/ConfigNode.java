package lc.common.configuration;

import java.lang.ref.WeakReference;
import java.util.HashMap;

/**
 * Represents a generic configuration node.
 *
 * @author AfterLifeLochie
 *
 */
public class ConfigNode {

	/**
	 * The name of the object.
	 */
	private String name;

	/**
	 * The comment of the object.
	 */
	private String comment;

	/**
	 * The object's parameter map.
	 */
	private HashMap<String, Object> parameters;

	/**
	 * The parent node, if it exists.
	 */
	private WeakReference<ConfigNode> parent;

	/**
	 * The modified state flag.
	 */
	private boolean modified;

	/** Default constructor, no initial values */
	public ConfigNode() {
		this(null, null, null);
	}

	/**
	 * Named configuration node
	 *
	 * @param name
	 *            The name
	 */
	public ConfigNode(String name) {
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
	public ConfigNode(String name, ConfigNode parent) {
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
	public ConfigNode(String name, String comment) {
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
	public ConfigNode(String name, String comment, ConfigNode parent) {
		this.name = name;
		this.comment = comment;
		this.parent = new WeakReference<ConfigNode>(parent);
		parameters = new HashMap<String, Object>();
	}

	/**
	 * Get the name of the node.
	 *
	 * @return The node name
	 */
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

}
