package lc.common.impl.registry;

import lc.LCRuntime;
import lc.api.components.ComponentType;
import lc.api.components.IComponentRegistry;
import lc.common.configuration.xml.ComponentConfig;

/**
 * Component registry implementation.
 *
 * @author AfterLifeLochie
 *
 */
public class ComponentRegistry implements IComponentRegistry {

	@Override
	public boolean isEnabled(ComponentType type) {
		return LCRuntime.runtime.config().config(type).enabled();
	}

	@Override
	public boolean isLoaded(ComponentType type) {
		// TODO Auto-generated method stub
		return false;
	}

}
