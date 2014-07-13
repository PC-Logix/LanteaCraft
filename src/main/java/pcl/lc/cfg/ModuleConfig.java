package pcl.lc.cfg;

public class ModuleConfig extends ConfigList {

	public ModuleConfig() {
		super();
	}

	public ModuleConfig(String name) {
		super(name);
	}

	public ModuleConfig(String name, String comment) {
		super(name, comment);
	}

	public ModuleConfig(String name, ConfigNode parent) {
		super(name, parent);
	}

}
