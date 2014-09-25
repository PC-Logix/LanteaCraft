package lc.common.impl.registry;

import java.lang.annotation.Annotation;

import lc.api.components.ComponentType;
import lc.api.defs.Definition;
import lc.api.defs.ILanteaCraftDefinition;
import lc.common.LCLog;
import lc.common.base.LCBlock;
import lc.common.base.LCItem;
import lc.common.base.LCItemBlock;
import lc.common.base.LCTile;
import lc.core.LCRuntime;

public class DefinitionWrapperProvider {

	@SuppressWarnings("unchecked")
	public static ILanteaCraftDefinition provide(Class<?> clazz) {
		LCLog.debug("Attempting to provide definition for class %s.", clazz);
		ILanteaCraftDefinition result = null;
		Annotation[] annotations = clazz.getDeclaredAnnotations();
		for (int i = 0, j = annotations.length; i < j; i++) {
			Annotation annotation = annotations[i];
			if (annotation.annotationType().equals(Definition.class)) {
				Definition definition = (Definition) annotation;
				String name = definition.name();
				ComponentType type = definition.type();
				Class<? extends LCBlock> blockClass = null;
				Class<? extends LCItemBlock> itemBlockClass = null;
				Class<? extends LCItem> itemClass = null;
				Class<? extends LCTile> tileClass = null;

				if (!definition.blockClass().equals(Void.class) && !definition.itemBlockClass().equals(Void.class)) {
					blockClass = (Class<? extends LCBlock>) definition.blockClass();
					itemBlockClass = (Class<? extends LCItemBlock>) definition.itemBlockClass();
					if (!definition.tileClass().equals(Void.class))
						tileClass = (Class<? extends LCTile>) definition.tileClass();
					result = new BlockItemDefinition(type, name, blockClass, itemBlockClass).setTileType(tileClass);
					LCLog.trace("Providing definition: %s: %s, block: %s, itemblock: %s, tile: %s", type, name, blockClass,
							itemBlockClass, tileClass);
				} else if (!definition.itemClass().equals(Void.class)) {
					itemClass = (Class<? extends LCItem>) definition.itemClass();
					result = new BlockItemDefinition(type, name, null, itemClass);
					LCLog.trace("Providing definition: %s: %s, item: %s", type, name, itemClass);
				} else {
					LCLog.warn("No valid definition found, ignoring.");
				}
			}
		}
		if (result != null)
			LCRuntime.runtime.registries().definitions().addDefinition(result);
		return result;
	}

}
