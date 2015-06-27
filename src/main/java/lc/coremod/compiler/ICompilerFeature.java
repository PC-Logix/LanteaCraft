package lc.coremod.compiler;

public interface ICompilerFeature {

	public byte[] compile(String name, String transformedName, byte[] basicClass);

}
