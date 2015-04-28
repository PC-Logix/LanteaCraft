package lc.common.configuration.xml;

import lc.api.components.IConfigurationProvider;

/**
 * Module configuration node
 *
 * @author AfterLifeLochie
 *
 */
public class ComponentConfig extends ConfigList implements IConfigurationProvider {

	/** Default constructor */
	public ComponentConfig() {
		super();
	}

	/**
	 * Named configuration node constructor
	 *
	 * @param name
	 *            The name
	 */
	public ComponentConfig(String name) {
		super(name);
	}

	/**
	 * Named and commented configuration node constructor
	 *
	 * @param name
	 *            The name
	 * @param comment
	 *            A comment
	 */
	public ComponentConfig(String name, String comment) {
		super(name, comment);
	}

	/**
	 * Named and parented configuration node constructor
	 *
	 * @param name
	 *            The name
	 * @param parent
	 *            A parent node
	 */
	public ComponentConfig(String name, ConfigNode parent) {
		super(name, parent);
	}

	/**
	 * Ask the configuration node if the Component represented by it is enabled
	 * 
	 * @return If this Component is enabled
	 */
	public boolean enabled() {
		return (Boolean) parameters().get("enabled");
	}

	/**
	 * Set the enabled state of the Component represented by this configuration
	 * node
	 * 
	 * @param z
	 *            The enabled state
	 */
	public void enabled(boolean z) {
		parameters().put("enabled", z);
		modify();
	}

}
