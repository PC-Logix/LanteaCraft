package lc.api.jit;

import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.LOCAL_VARIABLE;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * ASM-accessible data tags. Can be applied to types, fields, methods,
 * parameters, constructors and local variables. The implementation of the ASM
 * transformer is soley responsible for scanning and interpreting the values of
 * the Tags it supports.
 * 
 * @author AfterLifeLochie
 *
 */
@Target({ TYPE, FIELD, METHOD, PARAMETER, CONSTRUCTOR, LOCAL_VARIABLE })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Tag {

	/**
	 * The unique name of the tag.
	 * 
	 * @return The unique name of the tag.
	 */
	String name();

	/**
	 * @return An array of integer parameters.
	 */
	int[] intParams() default {};

	/**
	 * @return An array of double parameters.
	 */
	double[] doubleParams() default {};

	/**
	 * @return An array of float parameters.
	 */
	float[] floatParams() default {};

	/**
	 * @return An array of String parameters.
	 */
	String[] stringParams() default {};
}
