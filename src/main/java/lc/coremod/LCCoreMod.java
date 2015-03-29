package lc.coremod;

import java.io.FileNotFoundException;
import java.util.Map;

import lc.common.LCLog;

import org.apache.logging.log4j.LogManager;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;

/**
 * The main IFMLLoadingPlugin hook. Forge should be told through arguments or
 * through the MANIFEST file that this code should be treated as a tweak in
 * Forge.
 *
 * @author AfterLifeLochie
 */
@IFMLLoadingPlugin.TransformerExclusions({ "lc.coremod" })
public class LCCoreMod implements IFMLLoadingPlugin {

	/**
	 * Initializes the tweak plugin
	 */
	public LCCoreMod() {
		LCLog.setCoremodLogger(LogManager.getFormatterLogger("LCCoreMod"));
		try {
			LCLog.initPrintLoggers();
		} catch (FileNotFoundException e) {
			LCLog.warn("Can't open print state loggers, error.", e);
		}
		LCLog.info("LCCoreMod ready for action!");
	}

	@Override
	public String[] getASMTransformerClass() {
		return new String[] { "lc.coremod.LCCoreTransformer" };
	}

	@Override
	public String getModContainerClass() {
		return "lc.coremod.LCCoreModContainer";
	}

	@Override
	public String getSetupClass() {
		return null;
	}

	@Override
	public void injectData(Map<String, Object> data) {
		// TODO: Auto-generated method stub
	}

	@Override
	public String getAccessTransformerClass() {
		// TODO Auto-generated method stub
		return null;
	}

}
