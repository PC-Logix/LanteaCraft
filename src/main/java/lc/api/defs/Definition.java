package lc.api.defs;

public @interface Definition {

	String name();

	Class<?> blockClass() default Void.class;

	Class<?> itemBlockClass() default Void.class;

	Class<?> itemClass() default Void.class;

	Class<?> tileClass() default Void.class;

}
