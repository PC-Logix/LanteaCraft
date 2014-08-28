package lc.common.impl;

import java.util.HashMap;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import lc.api.components.IComponentRegistry;
import lc.api.components.IDefinitionRegistry;
import lc.api.defs.ILanteaCraftDefinition;
import lc.common.LCLog;
import lc.core.LCRuntime;

public class DefinitionRegistry implements IDefinitionRegistry {

	private final HashMap<String, ILanteaCraftDefinition> definitionPool;

	public DefinitionRegistry() {
		definitionPool = new HashMap<String, ILanteaCraftDefinition>();
	}

	@Override
	public void addDefinition(ILanteaCraftDefinition definition) {
		definitionPool.put(definition.getName(), definition);

	}

	@Override
	public ILanteaCraftDefinition getDefinition(String name) {
		return definitionPool.get(name);
	}

	public void init(LCRuntime runtime, FMLInitializationEvent event) {
		IComponentRegistry components = runtime.registries().components();
		LCLog.debug("Evaluating %s definitions for candidacy.", definitionPool.size());
		for (ILanteaCraftDefinition definition : definitionPool.values()) {
			if (definition instanceof BlockItemDefinition) {
				BlockItemDefinition element = (BlockItemDefinition) definition;
				if (components.isEnabled(element.getComponentOwner())) {
					LCLog.trace("Registering element %s, component %s enabled.", element.getName(),
							element.getComponentOwner());
					element.init();
				} else
					LCLog.trace("Dropping registration for element %s, component %s disabled.", element.getName(),
							element.getComponentOwner());
			} else
				LCLog.warn("Strange definition type %s, ignoring it.", definition.getClass().getName());
		}
	}

}
