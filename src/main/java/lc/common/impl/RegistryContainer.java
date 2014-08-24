package lc.common.impl;

import lc.api.components.IComponentRegistry;
import lc.api.components.IRegistryContainer;

public class RegistryContainer implements IRegistryContainer {

	private ComponentRegistry components = new ComponentRegistry();

	@Override
	public IComponentRegistry components() {
		return components;
	}

}
