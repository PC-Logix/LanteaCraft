package pcl.lc.coremod;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.launchwrapper.IClassTransformer;

import org.apache.logging.log4j.Level;

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
				LCCoreMod.getLogger().log(Level.DEBUG, "Instantiated transformer " + transformer);
			} catch (Throwable e) {
				LCCoreMod.getLogger().log(Level.WARN, "Could not instantiate transformer " + transformer);
				e.printStackTrace();
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
					LCCoreMod.getLogger().log(Level.FATAL, "Transformer " + transformer + " corrupted class " + name);
			} catch (Throwable e) {
				LCCoreMod.getLogger().log(Level.WARN, "Could not transform class " + name + " using " + transformer);
				e.printStackTrace();
			}
		return bytes;
	}

}
