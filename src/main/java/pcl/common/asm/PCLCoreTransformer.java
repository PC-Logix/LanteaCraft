package pcl.common.asm;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import net.minecraft.launchwrapper.IClassTransformer;

/**
 * {@link PCLCoreTransformer} acts as a proxy for children transformers in the
 * {@link PCLCoreTransformerPlugin} instance.
 * 
 * @author AfterLifeLochie
 */
public class PCLCoreTransformer implements IClassTransformer {

	/**
	 * Declaration of all registered transformers
	 */
	private final List<IClassTransformer> transformers;
	/**
	 * Static to establish if the ASM hooking was completed at runtime
	 */
	public static boolean ASM_SUCCESS = false;

	/**
	 * Initializes the core transformer. This notifies that hooking at runtime was a success,
	 * and sets up all transformers registered in the {@link PCLCoreTransformerPlugin}
	 * instance.
	 */
	public PCLCoreTransformer() {
		PCLCoreTransformer.ASM_SUCCESS = true;

		String[] tnames = PCLCoreTransformerPlugin.getTransformers();
		transformers = new ArrayList<IClassTransformer>(tnames.length);
		for (String transformer : tnames)
			try {
				transformers.add((IClassTransformer) Class.forName(transformer).newInstance());
				PCLCoreTransformerPlugin.getLogger().log(Level.FINE, "Instantiated transformer " + transformer);
			} catch (Throwable e) {
				PCLCoreTransformerPlugin.getLogger().log(Level.WARNING,
						"Could not instantiate transformer " + transformer);
				e.printStackTrace();
			}
	}

	/**
	 * Called when Forge is passing a class for transformation to us. We should pass this
	 * through all our children, then return the result.
	 */
	@Override
	public byte[] transform(String name, String transformedName, byte[] bytes) {
		if (bytes == null)
			return bytes;

		for (IClassTransformer transformer : transformers)
			try {
				bytes = transformer.transform(name, transformedName, bytes);
				if (bytes == null)
					PCLCoreTransformerPlugin.getLogger().log(Level.SEVERE,
							"Transformer " + transformer + " corrupted class " + name);
			} catch (Throwable e) {
				PCLCoreTransformerPlugin.getLogger().log(Level.WARNING,
						"Could not transform class " + name + " using " + transformer);
				e.printStackTrace();
			}
		return bytes;
	}

}
