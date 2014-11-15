package lc.common.impl.registry;

import lc.api.components.ComponentType;
import lc.api.components.IComponentRegistry;

/**
 * Component registry implementation.
 *
 * @author AfterLifeLochie
 *
 */
public class ComponentRegistry implements IComponentRegistry {

	@Override
	public boolean isEnabled(ComponentType type) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isLoaded(ComponentType type) {
		// TODO Auto-generated method stub
		return false;
	}

}
