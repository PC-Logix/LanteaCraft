package pcl.common.asm;

import java.util.Map;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;

/**
 * The main IFMLLoadingPlugin hook. Forge should be told through arguments or
 * through the MANIFEST file that this code should be treated as a tweak in
 * Forge.
 * 
 * @author AfterLifeLochie
 */
@IFMLLoadingPlugin.TransformerExclusions({ "pcl.common.asm" })
public class PCLCoreTransformerPlugin implements IFMLLoadingPlugin {

	/**
	 * The logger
	 */
	private static final Logger log = LogManager.getFormatterLogger("PCLCoreTransformerPlugin");

	/**
	 * Gets the logger
	 * 
	 * @return The logger
	 */
	public static Logger getLogger() {
		return PCLCoreTransformerPlugin.log;
	}

	/**
	 * Initializes the tweak plugin
	 */
	public PCLCoreTransformerPlugin() {
		FMLLog.getLogger();
		log.log(Level.INFO, "PCLCoreTransformerPlugin ready for action!");
	}

	@Override
	public String[] getASMTransformerClass() {
		return new String[] { "pcl.common.asm.PCLCoreTransformer" };
	}

	@Override
	public String getModContainerClass() {
		return "pcl.common.asm.PCLCoreModContainer";
	}

	@Override
	public String getSetupClass() {
		return null;
	}

	@Override
	public void injectData(Map<String, Object> data) {

	}

	public static String[] getTransformers() {
		return new String[] { "pcl.common.asm.ClassOptionalTransformer" };
	}

	@Override
	public String getAccessTransformerClass() {
		// TODO Auto-generated method stub
		return null;
	}

}
