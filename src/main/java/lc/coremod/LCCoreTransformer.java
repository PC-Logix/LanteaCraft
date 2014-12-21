package lc.coremod;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import lc.BuildInfo;
import lc.common.LCLog;
import net.minecraft.launchwrapper.IClassTransformer;

/**
 * {@link LCCoreTransformer} acts as a proxy for children transformers in the
 * {@link LCCoreMod} instance.
 *
 * @author AfterLifeLochie
 */
public class LCCoreTransformer implements IClassTransformer {

	/**
	 * List of all registered transformers
	 */
	private static final List<String> defs = new ArrayList<String>();

	static {
		defs.add("lc.coremod.ClassOptionalTransformer");
		defs.add("lc.coremod.HintInjectionTransformer");
		defs.add("lc.coremod.DriverBindingTransformer");
	}

	/**
	 * Declaration of all registered transformers
	 */
	private final List<IClassTransformer> transformers = new ArrayList<IClassTransformer>();

	/**
	 * Static to establish if the ASM hooking was completed at runtime
	 */
	public static boolean ASM_SUCCESS = false;

	/**
	 * Initializes the core transformer. This notifies that hooking at runtime
	 * was a success, and sets up all transformers registered in the
	 * {@link LCCoreMod} instance.
	 */
	public LCCoreTransformer() {
		LCCoreTransformer.ASM_SUCCESS = true;

		for (String transformer : defs)
			try {
				transformers.add((IClassTransformer) Class.forName(transformer).newInstance());
				LCLog.debug("Instantiated transformer %s.", transformer);
			} catch (Throwable e) {
				LCLog.fatal("Could not instantiate transformer %s.", transformer, e);
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
		for (IClassTransformer transformer : transformers)
			try {
				result = transformer.transform(name, transformedName, transformed);
				if (result == null)
					LCLog.fatal("Transformer %s has corrupted class %s, ignoring the transformer's result.",
							transformer, name);
				else {
					transformed = result;
					result = new byte[transformed.length];
					System.arraycopy(transformed, 0, result, 0, result.length);
				}
			} catch (Throwable e) {
				LCLog.fatal(
						"Transformer %s failed to transform class %s (exception raised), ignoring transformer result. ",
						transformer, name, e);
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

		return transformed;
	}

}
