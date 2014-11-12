package lc.api.defs;

import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;

import lc.api.components.ComponentType;
import lc.common.base.LCBlock;
import lc.common.base.LCItem;
import lc.common.base.LCItemBlock;
import lc.common.base.LCTile;

/**
 * Annotation to describe elements for {@link IContainerDefinition}
 * definitions.
 * 
 * @author AfterLifeLochie
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Definition {

	/**
	 * The name of the definition. Must be unique.
	 * 
	 * @return The name of the definition.
	 */
	String name();

	/**
	 * The component type of this definition. Must be one of
	 * {@link ComponentType}.
	 * 
	 * @return The component type of this definition.
	 */
	ComponentType type();

	/**
	 * The block class of this definition. May be any child class of
	 * {@link LCBlock} or {@link Void} (implicit).
	 * 
	 * @return The block class of this definition.
	 */
	Class<?> blockClass() default Void.class;

	/**
	 * The item-block class of this definition. May be any child class of
	 * {@link LCItemBlock} or {@link Void} (implicit).
	 * 
	 * @return The item-block class of this definition.
	 */
	Class<?> itemBlockClass() default Void.class;

	/**
	 * The item class of this definition. May be any child class of
	 * {@link LCItem} or {@link Void} (implicit).
	 * 
	 * @return The item class of this definition.
	 */
	Class<?> itemClass() default Void.class;

	/**
	 * The tile class of this definition. May be any child class of
	 * {@link LCTile} or {@link Void} (implicit).
	 * 
	 * @return The tile class of this definition.
	 */
	Class<?> tileClass() default Void.class;

}
