package lc.common.configuration;

import java.util.ArrayList;

/**
 * A list of modules in a configuration file
 * 
 * @author AfterLifeLochie
 * 
 */
public class ModuleList extends ConfigNode {

	/** A blank list */
	public ModuleList() {
		super();
	}

	/**
	 * A list with one name
	 * 
	 * @param name
	 *            The module name
	 */
	public ModuleList(String name) {
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
	public ModuleList(String name, String comment) {
		super(name, comment);
	}

	/**
	 * The children of the object.
	 */
	private ArrayList<ModuleConfig> children = new ArrayList<ModuleConfig>();

	/**
	 * Get the children of the object.
	 * 
	 * @return The children of the object.
	 */
	public ArrayList<ModuleConfig> children() {
		return children;
	}

	/**
	 * Set the children of the object.
	 * 
	 * @param children
	 *            The children of the object.
	 */
	public void setChildren(ArrayList<ModuleConfig> children) {
		this.children = children;
	}

}
