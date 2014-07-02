package pcl.common.xmlcfg;

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
	public static ModuleConfig findConfigByName(ModuleList list, String name) {
		for (ModuleConfig element : list.children())
			if (element.name().equalsIgnoreCase("Module") && element.parameters().containsKey("name"))
				if (element.parameters().get("name") instanceof String
						&& ((String) element.parameters().get("name")).equalsIgnoreCase(name))
					return element;
		ModuleConfig config = new ModuleConfig("Module", list);
		config.parameters().put("name", name);
		config.parameters().put("enabled", "true");
		list.children().add(config);
		config.modify();
		return config;
	}

	/**
	 * Gets or set a parameter on a node in a list if it does not exist.
	 * 
	 * @param list
	 *            The list object.
	 * @param clazz
	 *            The class of object
	 * @param name
	 *            The name of the qualifier
	 * @param paramName
	 *            The name of the parameter
	 * @param value
	 *            The value to set
	 * @return The value which exists or was set
	 */
	public static Object getOrSetParam(ConfigList list, String clazz, String name, String paramName, String comment, Object value) {
		ConfigNode targetNode = null;
		for (ConfigNode child : list.children())
			if (child.name().equals(clazz))
				if (child.parameters().containsKey("name") && child.parameters().get("name") instanceof String)
					if (((String) child.parameters().get("name")).equalsIgnoreCase(name))
						targetNode = child;
		if (targetNode == null) {
			targetNode = new ConfigNode(clazz, comment, list);
			targetNode.parameters().put("name", name);
			targetNode.parameters().put(paramName, value);
			list.children().add(targetNode);
			targetNode.modify();
		}
		return targetNode.parameters().get(paramName);
	}
}
