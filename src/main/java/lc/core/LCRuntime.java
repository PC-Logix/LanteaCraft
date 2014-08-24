package lc.core;

import lc.api.ILCAPIProxy;
import lc.api.components.IRegistryContainer;
import lc.api.defs.Blocks;
import lc.api.defs.Items;
import lc.common.impl.RegistryContainer;

public class LCRuntime implements ILCAPIProxy {

	public static final LCRuntime runtime = new LCRuntime();

	private Blocks blocks = new Blocks();
	private Items items = new Items();
	private RegistryContainer registries = new RegistryContainer();

	@Override
	public Blocks blocks() {
		return blocks;
	}

	@Override
	public Items items() {
		return items;
	}

	@Override
	public IRegistryContainer registries() {
		return registries;
	}

}
