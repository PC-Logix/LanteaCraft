package lc.coremod;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import lc.BuildInfo;
import lc.common.LCLog;
import lc.coremod.compiler.ICompilerFeature;
import net.minecraft.launchwrapper.IClassTransformer;

/**
 * {@link LCCoreTransformer} acts as a proxy for children transformers in the
 * {@link LCCoreMod} instance.
 *
 * @author AfterLifeLochie
 */
public class LCCoreTransformer implements IClassTransformer {

	/**
	 * List of all registered compiler classes.
	 */
	private static final List<String> compilerDefs = new ArrayList<String>();

	static {
		compilerDefs.add("lc.coremod.compiler.ClassOptionalCompiler");
		compilerDefs.add("lc.coremod.compiler.HintInjectionCompiler");
		compilerDefs.add("lc.coremod.compiler.DriverBindingCompiler");
	}

	/**
	 * Declaration of all registered compilers
	 */
	private final List<ICompilerFeature> compilers = new ArrayList<ICompilerFeature>();

	/**
	 * Static to establish if the ASM hooking was completed at runtime
	 */
	public static boolean ASM_SUCCESS = false;

	public static LCCoreTransformer $;

	public final HotClassCache classCache = new HotClassCache();

	/**
	 * Initializes the core transformer. This notifies that hooking at runtime
	 * was a success, and sets up all transformers registered in the
	 * {@link LCCoreMod} instance.
	 */
	public LCCoreTransformer() {
		LCCoreTransformer.$ = this;
		LCCoreTransformer.ASM_SUCCESS = true;

		for (String compiler : compilerDefs)
			try {
				compilers.add((ICompilerFeature) Class.forName(compiler).newInstance());
				LCLog.debug("Instantiated compiler %s.", compiler);
			} catch (Throwable e) {
				LCLog.fatal("Could not instantiate compiler %s.", compiler, e);
			}
		if (BuildInfo.DEBUG)
			try {
				File vmdir = new File("vm/");
				if (vmdir.exists()) {
					File[] files = vmdir.listFiles();
					for (File f : files)
						if (f.isFile() && f.exists() && !f.equals(vmdir))
							try {
								f.delete();
							} catch (Throwable t) {
							}
				}
			} catch (Throwable t) {
				LCLog.fatal("Failed to set up VM runtime save-to-disk debugger.", t);
			}
	}

	/**
	 * Called when Forge is passing a class for transformation to us. We should
	 * pass this through all our children, then return the result.
	 */
	@Override
	public byte[] transform(String name, String transformedName, final byte[] bytes) {
		if (bytes == null)
			return bytes;

		/* Blank result array, initial transformed array */
		byte[] result = null, transformed = new byte[bytes.length];
		/* Set initial transformed array = bytes of original source */
		System.arraycopy(bytes, 0, transformed, 0, transformed.length);

		/*
		 * Store the result of the transformation on transformed in result. If
		 * the transformation fails, don't care about the result in result. If
		 * successful, put result into transformed and update result with the
		 * last transformation.
		 */
		for (ICompilerFeature compiler : compilers)
			try {
				result = compiler.compile(name, transformedName, transformed);
				if (result == null)
					LCLog.fatal("ICompilerFeature %s has corrupted class %s, ignoring the compiler result.", compiler,
							name);
				else {
					transformed = result;
					result = new byte[transformed.length];
					System.arraycopy(transformed, 0, result, 0, result.length);
				}
			} catch (Throwable e) {
				LCLog.fatal(
						"ICompilerFeature %s failed to recompile class %s (exception raised), ignoring compiler result.",
						compiler, name, e);
			}

		if (BuildInfo.DEBUG && name.startsWith("lc."))
			try {
				File vmdir = new File("vm/");
				File saveObj = new File(vmdir, name.replace("/", "_").replace(".", "_") + ".class");
				if (!vmdir.exists())
					vmdir.mkdir();
				if (saveObj.exists())
					saveObj.delete();
				FileOutputStream output = new FileOutputStream(saveObj, false);
				output.write(transformed);
				output.close();
			} catch (Throwable t) {
				LCLog.fatal("Failed to save runtime implementation of class %s.", name, t);
			}

		classCache.suggestCache(name, transformed);
		return transformed;
	}

}
