package pcl.lc.coremod;

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
@IFMLLoadingPlugin.TransformerExclusions({ "pcl.lc.coremod" })
public class LCCoreMod implements IFMLLoadingPlugin {

	/**
	 * The logger
	 */
	private static final Logger log = LogManager.getFormatterLogger("LCCoreMod");

	/**
	 * Gets the logger
	 * 
	 * @return The logger
	 */
	public static Logger getLogger() {
		return LCCoreMod.log;
	}

	/**
	 * Initializes the tweak plugin
	 */
	public LCCoreMod() {
		FMLLog.getLogger();
		log.log(Level.INFO, "LCCoreMod ready for action!");
	}

	@Override
	public String[] getASMTransformerClass() {
		return new String[] { "pcl.lc.coremod.LCCoreTransformer" };
	}

	@Override
	public String getModContainerClass() {
		return "pcl.lc.coremod.LCCoreModContainer";
	}

	@Override
	public String getSetupClass() {
		return null;
	}

	@Override
	public void injectData(Map<String, Object> data) {

	}

	public static String[] getTransformers() {
		return new String[] { "pcl.lc.coremod.ClassOptionalTransformer" };
	}

	@Override
	public String getAccessTransformerClass() {
		// TODO Auto-generated method stub
		return null;
	}

}
