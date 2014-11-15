package lc.coremod;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import lc.common.LCLog;
import lc.core.BuildInfo;
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
				LCLog.warn("Could not instantiate transformer %s.", transformer, e);
			}
	}

	/**
	 * Called when Forge is passing a class for transformation to us. We should
	 * pass this through all our children, then return the result.
	 */
	@Override
	public byte[] transform(String name, String transformedName, byte[] bytes) {
		if (bytes == null)
			return bytes;

		for (IClassTransformer transformer : transformers)
			try {
				bytes = transformer.transform(name, transformedName, bytes);
				if (bytes == null)
					LCLog.fatal("Transformer %s corrupted class %s!", transformer, name);
			} catch (Throwable e) {
				LCLog.fatal("Could not transform class %s using %s!", name, transformer, e);
			}

		if (BuildInfo.DEBUG)
			try {
				File vmdir = new File("vm/");
				File saveObj = new File(vmdir, name.replace("/", "_").replace(".", "_") + ".class");
				if (!vmdir.exists())
					vmdir.mkdir();
				if (saveObj.exists())
					saveObj.delete();
				FileOutputStream output = new FileOutputStream(saveObj, false);
				output.write(bytes);
				output.close();
			} catch (Throwable t) {
				LCLog.fatal("Failed to save runtime implementation of class %s.", t);
			}
		return bytes;
	}

}
