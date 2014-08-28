package lc.api.defs;

import java.lang.annotation.RetentionPolicy;

import java.lang.annotation.Retention;

import lc.api.components.ComponentType;

@Retention(RetentionPolicy.RUNTIME)
public @interface Definition {

	String name();

	ComponentType type();

	Class<?> blockClass() default Void.class;

	Class<?> itemBlockClass() default Void.class;

	Class<?> itemClass() default Void.class;

	Class<?> tileClass() default Void.class;

}
