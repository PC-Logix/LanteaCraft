package lc.common.configuration.xml;

import java.util.ArrayList;

/**
 * A list of modules in a configuration file
 *
 * @author AfterLifeLochie
 *
 */
public class ComponentConfigList extends ConfigNode {

	/** A blank list */
	public ComponentConfigList() {
		super();
	}

	/**
	 * A list with one name
	 *
	 * @param name
	 *            The module name
	 */
	public ComponentConfigList(String name) {
		super(name);
	}

	/**
	 * A list with one name and a comment
	 *
	 * @param name
	 *            The module name
	 * @param comment
	 *            The module comment
	 */
	public ComponentConfigList(String name, String comment) {
		super(name, comment);
	}

	/**
	 * The children of the object.
	 */
	private ArrayList<ComponentConfig> children = new ArrayList<ComponentConfig>();

	/**
	 * Get the children of the object.
	 *
	 * @return The children of the object.
	 */
	public ArrayList<ComponentConfig> children() {
		return children;
	}

	/**
	 * Set the children of the object.
	 *
	 * @param children
	 *            The children of the object.
	 */
	public void setChildren(ArrayList<ComponentConfig> children) {
		this.children = children;
	}

}
