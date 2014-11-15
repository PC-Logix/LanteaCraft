package lc.common.configuration;

import java.util.ArrayList;

/**
 * Represents a configuration node which contains children nodes of any count
 * and type.
 *
 * @author AfterLifeLochie
 *
 */
public class ConfigList extends ConfigNode {

	/** Default constructor, no initial values */
	public ConfigList() {
		super();
	}

	/**
	 * Named configuration node
	 *
	 * @param name
	 *            The name
	 */
	public ConfigList(String name) {
		super(name);
	}

	/**
	 * Named and commented configuration node
	 *
	 * @param name
	 *            The name
	 * @param comment
	 *            A comment
	 */
	public ConfigList(String name, String comment) {
		super(name, comment);
	}

	/**
	 * Named and parented configuration node
	 *
	 * @param name
	 *            The name
	 * @param parent
	 *            The parent node
	 */
	public ConfigList(String name, ConfigNode parent) {
		super(name, parent);
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
	public ConfigList(String name, String comment, ConfigNode parent) {
		super(name, comment, parent);
	}

	/**
	 * The children of the object.
	 */
	private ArrayList<ConfigNode> children = new ArrayList<ConfigNode>();

	/**
	 * Get the children of this node.
	 *
	 * @return The children of this node.
	 */
	public ArrayList<ConfigNode> children() {
		return children;
	}

	/**
	 * Set the children of this node.
	 *
	 * @param children
	 *            The children of this node.
	 */
	public void setChildren(ArrayList<ConfigNode> children) {
		this.children = children;
	}

}
