package lc.coremod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotations for run-time tagging.
 *
 * @author AfterLifeLochie
 *
 */
public final class RuntimeAnnotation {

	/**
	 * Represents an interface to import at runtime if the mod specified is
	 * loaded.
	 *
	 * @author AfterLifeLochie
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	public @interface RuntimeInterface {
		/**
		 * @return The mod ID to require.
		 */
		String modid();

		/**
		 * @return The classname of the interface.
		 */
		String clazz();
	}
}
