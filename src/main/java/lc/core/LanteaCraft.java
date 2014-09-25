package lc.core;

import org.apache.logging.log4j.Level;

import lc.common.LCLog;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.FMLRelaunchLog;

/**
 * LanteaCraft FML mod interface
 * 
 * @author AfterLifeLochie
 * 
 */
@Mod(modid = BuildInfo.modID, name = BuildInfo.modName, version = BuildInfo.versionNumber + "-" + BuildInfo.buildNumber, dependencies = "after:ComputerCraft;after:OpenComputers;after:BuildCraft|Core;after:IC2;after:SGCraft")
public class LanteaCraft {

	/** The mod instanceof */
	public static LanteaCraft instance;

	/** Default constructor */
	public LanteaCraft() {
		LanteaCraft.instance = this;
		FMLRelaunchLog.log(Level.INFO, "LanteaCraft ready for action!");
	}

	/**
	 * Handler for FML preInit event
	 * 
	 * @param event
	 *            An event
	 */
	@Mod.EventHandler
	public void preinit(FMLPreInitializationEvent event) {
		LCLog.setLogger(event.getModLog());
		LCLog.showRuntimeInfo();
		if (!Loader.isModLoaded("LanteaCraft-Core"))
			throw new RuntimeException("LanteaCraft Core mod is missing. Cannot load the game.");
		LCRuntime.runtime.preinit(event);
	}

	/**
	 * Handler for FML init event
	 * 
	 * @param event
	 *            An event
	 */
	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		LCRuntime.runtime.init(event);
	}

	/**
	 * Handler for FML postInit event
	 * 
	 * @param event
	 *            An event
	 */
	@Mod.EventHandler
	public void postinit(FMLPostInitializationEvent event) {
		LCRuntime.runtime.postinit(event);
	}

}
