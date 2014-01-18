package pcl.common.asm;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public final class ClassOptional {
	/**
	 * Not constructable
	 */
	private ClassOptional() {
	}

	/**
	 * Mark a list of interfaces as removable
	 * 
	 * @author AfterLifeLochie
	 * 
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	public @interface ClassFilterInterfaceList {
		/**
		 * Mark a list of interfaces for optional removal.
		 * 
		 * @return a list of interfaces for optional removal
		 */
		public ClassFilterInterface[] value();
	}

	/**
	 * Used to remove optional interfaces by class typeof
	 * 
	 * @author AfterLifeLochie
	 * 
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	public @interface ClassFilterInterface {
		/**
		 * The fully qualified name of the interface to be stripped
		 * 
		 * @return the interface name
		 */
		public String iface();

		/**
		 * The class that is required for stripping NOT to occur
		 * 
		 * @return the classname
		 */
		public String classname();
	}

	/**
	 * Used to remove optional methods
	 * 
	 * @author AfterLifeLochie
	 * 
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	public @interface ClassFilterMethod {
		/**
		 * The class that is required for stripping NOT to occur
		 * 
		 * @return the classname
		 */
		public String classname();
	}
}
