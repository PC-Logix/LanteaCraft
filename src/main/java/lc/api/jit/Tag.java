package lc.api.jit;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Tag {

	String name();

	int[] intParams() default {};

	double[] doubleParams() default {};

	float[] floatParams() default {};

	String[] stringParams() default {};
}
