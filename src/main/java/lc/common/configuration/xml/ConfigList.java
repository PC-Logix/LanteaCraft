package lc.common.configuration.xml;

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

	/**
	 * Gets or set a parameter on a node in a list if it does not exist.
	 *
	 * @param clazz
	 *            The class of object
	 * @param name
	 *            The name of the qualifier
	 * @param paramName
	 *            The name of the parameter
	 * @param comment
	 *            The parameter comment, if any
	 * @param value
	 *            The value to set
	 * @return The value which exists or was set
	 */
	public Object getOrSetParam(String clazz, String name, String paramName, String comment, Object value) {
		ConfigNode targetNode = null;
		for (ConfigNode child : children())
			if (child.name().equals(clazz))
				if (child.parameters().containsKey("name") && child.parameters().get("name") instanceof String)
					if (((String) child.parameters().get("name")).equalsIgnoreCase(name))
						targetNode = child;
		if (targetNode == null) {
			targetNode = new ConfigNode(clazz, comment, this);
			targetNode.parameters().put("name", name);
			children().add(targetNode);
			targetNode.modify();
		}
		if (!targetNode.parameters().containsKey(paramName)) {
			targetNode.parameters().put(paramName, value);
			targetNode.modify();
		}
		return targetNode.parameters().get(paramName);
	}

	/**
	 * Gets or set a boolean parameter on a node in a list if it does not exist.
	 *
	 * @param clazz
	 *            The class of object
	 * @param name
	 *            The name of the qualifier
	 * @param paramName
	 *            The name of the parameter
	 * @param comment
	 *            The parameter comment, if any
	 * @param state
	 *            The value to set
	 * @return The value which exists or was set
	 */
	public Boolean getOrSetBooleanParam(String clazz, String name, String paramName, String comment, Boolean state) {
		Object value = getOrSetParam(clazz, name, paramName, comment, state);
		return DOMHelper.popBoolean(value.toString(), false);
	}

	/**
	 * Gets or set an integer parameter on a node in a list if it does not
	 * exist.
	 *
	 * @param clazz
	 *            The class of object
	 * @param name
	 *            The name of the qualifier
	 * @param paramName
	 *            The name of the parameter
	 * @param comment
	 *            The parameter comment, if any
	 * @param state
	 *            The value to set
	 * @return The value which exists or was set
	 */
	public Integer getOrSetIntParam(String clazz, String name, String paramName, String comment, Integer state) {
		Object value = getOrSetParam(clazz, name, paramName, comment, state);
		return Integer.parseInt(value.toString());
	}

	/**
	 * Gets or set an double parameter on a node in a list if it does not exist.
	 *
	 * @param clazz
	 *            The class of object
	 * @param name
	 *            The name of the qualifier
	 * @param paramName
	 *            The name of the parameter
	 * @param comment
	 *            The parameter comment, if any
	 * @param state
	 *            The value to set
	 * @return The value which exists or was set
	 */
	public Double getOrSetDoubleParam(String clazz, String name, String paramName, String comment, Double state) {
		Object value = getOrSetParam(clazz, name, paramName, comment, state);
		return Double.parseDouble(value.toString());
	}

}
