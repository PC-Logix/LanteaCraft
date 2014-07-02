package pcl.common.xmlcfg;

import java.util.ArrayList;

/**
 * Represents a configuration node which contains children nodes of any count
 * and type.
 * 
 * @author AfterLifeLochie
 * 
 */
public class ConfigList extends ConfigNode {

	public ConfigList() {
		super();
	}

	public ConfigList(String name) {
		super(name);
	}

	public ConfigList(String name, String comment) {
		super(name, comment);
	}

	public ConfigList(String name, ConfigNode parent) {
		super(name, parent);
	}
	
	/**
	 * The children of the object.
	 */
	private ArrayList<ConfigNode> children;

	public ArrayList<ConfigNode> children() {
		return children;
	}

	public void setChildren(ArrayList<ConfigNode> children) {
		this.children = children;
	}

}
