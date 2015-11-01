package lc.common.impl.registry;

import java.lang.annotation.Annotation;

import net.minecraft.entity.Entity;
import lc.LCRuntime;
import lc.api.components.ComponentType;
import lc.api.defs.Definition;
import lc.api.defs.IContainerDefinition;
import lc.common.LCLog;
import lc.common.base.LCBlock;
import lc.common.base.LCItem;
import lc.common.base.LCItemBlock;
import lc.common.base.LCTile;

/**
 * Definition wrapper provider. Wraps classes with definitions into
 * ILanteaCraftDefintions automagically.
 *
 * @author AfterLifeLochie
 *
 */
public class DefinitionWrapperProvider {

	/**
	 * Converts a class with a definition into an ILanteaCraftDefinition, if the
	 * definition is correctly formed.
	 *
	 * @param clazz
	 *            The class to read
	 * @return A formed definition, or null if no valid definition is found.
	 */
	@SuppressWarnings("unchecked")
	public static IContainerDefinition provide(Class<?> clazz) {
		LCLog.debug("Attempting to provide definition for class %s.", clazz);
		IContainerDefinition result = null;
		Annotation[] annotations = clazz.getDeclaredAnnotations();
		for (Annotation annotation : annotations)
			if (annotation.annotationType().equals(Definition.class)) {
				Definition definition = (Definition) annotation;
				String name = definition.name();
				ComponentType type = definition.type();
				Class<? extends LCBlock> blockClass = null;
				Class<? extends LCItemBlock> itemBlockClass = null;
				Class<? extends LCItem> itemClass = null;
				Class<? extends LCTile> tileClass = null;
				Class<? extends Entity> entityClass = null;

				if (!definition.blockClass().equals(Void.class) && !definition.itemBlockClass().equals(Void.class)) {
					LCLog.doAssert(definition.itemClass().equals(Void.class),
							"Definition Block specifies Item, not allowed.");
					blockClass = (Class<? extends LCBlock>) definition.blockClass();
					itemBlockClass = (Class<? extends LCItemBlock>) definition.itemBlockClass();
					if (!definition.tileClass().equals(Void.class))
						tileClass = (Class<? extends LCTile>) definition.tileClass();
					result = new BlockItemDefinition(type, name, blockClass, itemBlockClass).setTileType(tileClass);
					LCLog.trace("Providing definition: %s: %s, block: %s, itemblock: %s, tile: %s", type, name,
							blockClass, itemBlockClass, tileClass);
				} else if (!definition.itemClass().equals(Void.class)) {
					LCLog.doAssert(definition.blockClass().equals(Void.class),
							"Definition Item specifies Block, not allowed.");
					LCLog.doAssert(definition.tileClass().equals(Void.class),
							"Definition Item specifies Tile, not allowed.");
					itemClass = (Class<? extends LCItem>) definition.itemClass();
					result = new BlockItemDefinition(type, name, null, itemClass);
					LCLog.trace("Providing definition: %s: %s, item: %s", type, name, itemClass);
				} else if (!definition.entityClass().equals(Void.class)) {
					entityClass = (Class<? extends Entity>) definition.entityClass();
					result = new EntityDefinition(type, name, entityClass);
				} else
					LCLog.warn("No valid definition found, ignoring.");
			}
		if (result != null)
			LCRuntime.runtime.registries().definitions().addDefinition(result);
		else
			LCLog.warn("Object %s requested for definition, none made.", clazz.getName());
		return result;
	}

}
