package lc.common.configuration.xml;

import java.util.ArrayList;

/**
 * Configuration file helper class
 *
 * @author AfterLifeLochie
 *
 */
public class ConfigHelper {

	/**
	 * Find or create a configuration set for a module by name, case
	 * insensitive.
	 *
	 * @param list
	 *            The list of modules
	 * @param name
	 *            The name to find
	 * @return The module config, or null if it does not exist
	 */
	public static ComponentConfig findComponentContainer(ComponentConfigList list, String name) {
		for (ComponentConfig element : list.children())
			if (element.name().equalsIgnoreCase("Component") && element.parameters().containsKey("name")
					&& element.parameters().get("name").toString().equalsIgnoreCase(name))
				return element;
		ComponentConfig config = new ComponentConfig("Component", list);
		config.parameters().put("name", name);
		config.parameters().put("enabled", true);
		list.children().add(config);
		config.modify();
		return config;
	}

	/**
	 * Find a configuration node by a class
	 *
	 * @param list
	 *            The list
	 * @param clazz
	 *            A class
	 * @return The node or null if no node exists
	 */
	public static ConfigNode findConfigByClass(ConfigList list, String clazz) {
		ConfigNode targetNode = null;
		for (ConfigNode child : list.children())
			if (child.name().equals(clazz))
				targetNode = child;
		return targetNode;
	}

	/**
	 * Find all configuration nodes of a class
	 *
	 * @param list
	 *            The list
	 * @param clazz
	 *            A class
	 * @return A list of nodes
	 */
	public static ArrayList<ConfigNode> findAllConfigByClass(ConfigList list, String clazz) {
		ArrayList<ConfigNode> result = new ArrayList<ConfigNode>();
		for (ConfigNode child : list.children())
			if (child.name().equals(clazz))
				result.add(child);
		return result;
	}

	
}
