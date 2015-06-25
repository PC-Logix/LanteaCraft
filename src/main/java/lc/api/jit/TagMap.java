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
 * ASM-accessible data tag list. Can be applied to types, fields, methods,
 * parameters, constructors and local variables in order to contain a list of
 * tags.
 * 
 * @author AfterLifeLochie
 *
 */
@Target({ TYPE, FIELD, METHOD, PARAMETER, CONSTRUCTOR, LOCAL_VARIABLE })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface TagMap {
	/**
	 * @return The list of all embedded tags on this object.
	 */
	Tag[] value();
}
