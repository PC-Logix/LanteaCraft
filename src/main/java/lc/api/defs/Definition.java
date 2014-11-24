package lc.api.defs;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import lc.api.components.ComponentType;
import lc.common.base.LCBlock;
import lc.common.base.LCItem;
import lc.common.base.LCItemBlock;
import lc.common.base.LCTile;

/**
 * Annotation to describe elements for {@link IContainerDefinition} definitions.
 * Elements with these annotations are initialized by the runtime and
 * transformed into definitions which LanteaCraft can interpret.
 *
 * @author AfterLifeLochie
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Definition {

	/**
	 * The name of the definition. Must be unique, cannot be null.
	 *
	 * @return The name of the definition.
	 */
	String name();

	/**
	 * The component type of this definition. Must be one of
	 * {@link ComponentType}. Cannot be null.
	 *
	 * @return The component type of this definition.
	 */
	ComponentType type();

	/**
	 * The block class of this definition. May be any child class of
	 * {@link LCBlock} or {@link Void} (implicit). If a block class is
	 * configured, an {@link Definition#itemBlockClass()} must also be
	 * configured.
	 *
	 * @return The block class of this definition.
	 */
	Class<?> blockClass() default Void.class;

	/**
	 * The item-block class of this definition. May be any child class of
	 * {@link LCItemBlock} or {@link Void} (implicit). If an item block class is
	 * configured, an {@link Definition#blockClass()} must also be configured.
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
	 * {@link LCTile} or {@link Void} (implicit). If a tile class is configured,
	 * a {@link Definition#blockClass()} must also be configured.
	 *
	 * @return The tile class of this definition.
	 */
	Class<?> tileClass() default Void.class;

}
