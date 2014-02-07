package pcl.common.xmlcfg;

import java.util.ArrayList;

/**
 * Represents a configuration node which contains children nodes of any count
 * and type.
 * 
 * @author AfterLifeLochie
 * 
 */
public class ConfigNode extends ConfigObject {

	public ConfigNode() {
		super();
	}

	public ConfigNode(String name) {
		super(name);
	}

	public ConfigNode(String name, String comment) {
		super(name, comment);
	}

	/**
	 * The children of the object.
	 */
	private ArrayList<ConfigObject> children;

	public ArrayList<ConfigObject> children() {
		return children;
	}

	public void setChildren(ArrayList<ConfigObject> children) {
		this.children = children;
	}

}
