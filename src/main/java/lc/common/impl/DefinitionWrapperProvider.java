package lc.common.impl;

import java.lang.annotation.Annotation;

import lc.api.components.ComponentType;
import lc.api.defs.Definition;
import lc.api.defs.ILanteaCraftDefinition;
import lc.common.base.LCBlock;
import lc.common.base.LCItem;
import lc.common.base.LCItemBlock;
import lc.common.base.LCTile;

public class DefinitionWrapperProvider {

	public static ILanteaCraftDefinition provide(Class<?> clazz) {
		ILanteaCraftDefinition result = null;
		Annotation[] annotations = clazz.getAnnotations();
		for (int i = 0, j = annotations.length; i < j; i++) {
			Annotation annotation = annotations[i];
			if (annotation.annotationType().equals(Definition.class)) {
				Definition definition = (Definition) annotation;
				String name = definition.name();
				ComponentType type = definition.type();
				Class<? extends LCBlock> blockClass = (Class<? extends LCBlock>) definition
						.blockClass();
				if (!blockClass.equals(Void.class)) {
					Class<? extends LCItemBlock> itemBlockClass = (Class<? extends LCItemBlock>) definition
							.itemBlockClass();
					Class<? extends LCTile> tileClass = (Class<? extends LCTile>) definition
							.tileClass();
					result = new BlockItemDefinition(type, name, blockClass,
							itemBlockClass).setTileType(tileClass);
				} else {
					Class<? extends LCItem> itemClass = (Class<? extends LCItem>) definition
							.itemClass();
					result = new BlockItemDefinition(type, name, null,
							itemClass);
				}
			}
		}
		return result;
	}

}
