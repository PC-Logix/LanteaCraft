package lc.common.impl;

import lc.api.components.IComponentRegistry;
import lc.api.components.IDefinitionRegistry;
import lc.api.components.IRegistryContainer;

public class RegistryContainer implements IRegistryContainer {

	private ComponentRegistry components = new ComponentRegistry();
	private DefinitionRegistry definitions = new DefinitionRegistry();

	@Override
	public IComponentRegistry components() {
		return components;
	}

	@Override
	public IDefinitionRegistry definitions() {
		return definitions;
	}

}
