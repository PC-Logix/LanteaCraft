package lc.api.defs;

import lc.api.components.ComponentType;

public @interface Definition {

	String name();

	ComponentType type();

	Class<?> blockClass() default Void.class;

	Class<?> itemBlockClass() default Void.class;

	Class<?> itemClass() default Void.class;

	Class<?> tileClass() default Void.class;

}
