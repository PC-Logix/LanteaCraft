package lc.common.configuration;

/**
 * Module configuration node
 * 
 * @author AfterLifeLochie
 * 
 */
public class ModuleConfig extends ConfigList {

	/** Default constructor */
	public ModuleConfig() {
		super();
	}

	/**
	 * Named configuration node constructor
	 * 
	 * @param name
	 *            The name
	 */
	public ModuleConfig(String name) {
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
	public ModuleConfig(String name, String comment) {
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
	public ModuleConfig(String name, ConfigNode parent) {
		super(name, parent);
	}

}
