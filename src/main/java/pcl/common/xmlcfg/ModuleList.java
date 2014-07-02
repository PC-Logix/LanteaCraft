package pcl.common.xmlcfg;

import java.util.ArrayList;

public class ModuleList extends ConfigNode {

	public ModuleList() {
		super();
	}

	public ModuleList(String name) {
		super(name);
	}

	public ModuleList(String name, String comment) {
		super(name, comment);
	}
	
	/**
	 * The children of the object.
	 */
	private ArrayList<ModuleConfig> children;

	public ArrayList<ModuleConfig> children() {
		return children;
	}

	public void setChildren(ArrayList<ModuleConfig> children) {
		this.children = children;
	}
	
}
