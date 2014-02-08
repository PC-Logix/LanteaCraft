package pcl.common.xmlcfg;

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
	
	public ConfigNode() {
	}

	public ConfigNode(String name) {
		this.name = name;
	}

	public ConfigNode(String name, String comment) {
		this.name = name;
		this.comment = comment;
	}

	public String name() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String comment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public HashMap<String, Object> parameters() {
		return parameters;
	}

	public void setParameters(HashMap<String, Object> parameters) {
		this.parameters = parameters;
	}
	
	public void modify() {
		this.modified = true;
		if (this.parent != null && this.parent.get() != null)
			this.parent.get().modify();
	}
	
	public boolean modified() {
		return this.modified;
	}

}
