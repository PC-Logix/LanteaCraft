package lc.coremod;

import java.util.ArrayList;
import java.util.List;

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
	 * Declaration of all registered transformers
	 */
	private final List<IClassTransformer> transformers;
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

		String[] tnames = LCCoreMod.getTransformers();
		transformers = new ArrayList<IClassTransformer>(tnames.length);
		for (String transformer : tnames)
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
		return bytes;
	}

}
