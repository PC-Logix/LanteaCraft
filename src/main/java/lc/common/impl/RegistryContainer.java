package lc.common.impl;

import lc.api.components.IComponentRegistry;
import lc.api.components.IDefinitionRegistry;
import lc.api.components.IRegistryContainer;

public class RegistryContainer implements IRegistryContainer {

	public static final RegistryContainer instance = new RegistryContainer();

	private ComponentRegistry components = new ComponentRegistry();
	private DefinitionRegistry definitions = new DefinitionRegistry();

	private RegistryContainer() {
	}

	@Override
	public IComponentRegistry components() {
		return components;
	}

	@Override
	public IDefinitionRegistry definitions() {
		return definitions;
	}

}
