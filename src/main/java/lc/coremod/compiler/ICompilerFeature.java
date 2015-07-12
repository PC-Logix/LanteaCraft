package lc.coremod.compiler;

/**
 * Contract interface for at-runtime-compiler features.
 * 
 * @author AfterLifeLochie
 *
 */
public interface ICompilerFeature {

	/**
	 * <p>
	 * Called by the ASM rewriter to force this feature to perform any
	 * transformations on the class provided. The compiler feature should
	 * perform and finalize any and all required transformations on the class
	 * provided, then return the class.
	 * </p>
	 * <p>
	 * If the compiler does not need to change any of the data in the class, the
	 * compiler can (and should) return the original byte-array provided; it
	 * does not (and should not) copy the byte-array again.
	 * </p>
	 * 
	 * @param name
	 *            The name of the class
	 * @param transformedName
	 *            The transformed name of the class
	 * @param basicClass
	 *            The basic class data
	 * @return The compiled class
	 */
	public byte[] compile(String name, String transformedName, byte[] basicClass);

}
