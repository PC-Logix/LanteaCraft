package pcl.common.asm;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public final class ClassMerge {

	private ClassMerge() {
	}

	/**
	 * Merge the fromClass specified into this class.
	 * 
	 * @author AfterLifeLochie
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	public @interface Merge {
		public String fromClass();
	}

	/**
	 * Merge the fromClass specified into this class only if modName is loaded.
	 * 
	 * @author AfterLifeLochie
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	public @interface MergeMod {
		public String fromClass();

		public String modName();
	}

}
