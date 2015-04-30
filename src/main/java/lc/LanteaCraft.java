package lc;

import lc.common.LCLog;

import org.apache.logging.log4j.Level;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLFingerprintViolationEvent;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms.IMCEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerAboutToStartEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppedEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
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
	public static volatile LanteaCraft instance;

	/** Default constructor */
	public LanteaCraft() {
		LanteaCraft.instance = this;
		FMLRelaunchLog.log(Level.INFO, "LanteaCraft ready for action!");
	}

	/**
	 * Handler for FML preinit event
	 *
	 * @param event
	 *            An event
	 */
	@Mod.EventHandler
	public void preinit(FMLPreInitializationEvent event) {
		LCLog.setLogger(event.getModLog());
		LCLog.showRuntimeInfo();
		if (!Loader.isModLoaded("LanteaCraft-Core")) {
			if (BuildInfo.$.development()) {
				LCLog.fatal("The development envrionment is not configured correctly. Please add");
				LCLog.fatal("-Dfml.coreMods.load=lc.coremod.LCCoreMod to the VM parameters or");
				LCLog.fatal("install a dummy JAR with the coremod configuration added. Aborting!");
			}
			throw new RuntimeException("LanteaCraft coremod is not loaded. Broken or otherwise modified JAR file.");
		}
		if (!BuildInfo.$.development() && !BuildInfo.IS_SIGNED) {
			LCLog.fatal("This build of LanteaCraft is not signed, which means it has been modified.");
			LCLog.fatal("You should attempt to reinstall a clean copy of LanteaCraft if you did not");
			LCLog.fatal("modify it yourself. Going to proceed with loading anyway.");
		}
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
	 * Handler for FML postinit event
	 *
	 * @param event
	 *            An event
	 */
	@Mod.EventHandler
	public void postinit(FMLPostInitializationEvent event) {
		LCRuntime.runtime.postinit(event);
	}

	/**
	 * Handler for FML beforeServerStarting event
	 *
	 * @param event
	 *            An event
	 */
	@Mod.EventHandler
	public void beforeServerStarting(FMLServerAboutToStartEvent event) {
		LCRuntime.runtime.beforeServerStarting(event);
	}

	/**
	 * Handler for FML serverStarting event
	 *
	 * @param event
	 *            An event
	 */
	@Mod.EventHandler
	public void serverStarting(FMLServerStartingEvent event) {
		LCRuntime.runtime.serverStarting(event);
	}

	/**
	 * Handler for FML serverStarted event
	 *
	 * @param event
	 *            An event
	 */
	@Mod.EventHandler
	public void serverStarted(FMLServerStartedEvent event) {
		LCRuntime.runtime.serverStarted(event);
	}

	/**
	 * Handler for FML serverStopping event
	 *
	 * @param event
	 *            An event
	 */
	@Mod.EventHandler
	public void serverStopping(FMLServerStoppingEvent event) {
		LCRuntime.runtime.serverStopping(event);
	}

	/**
	 * Handler for FML serverStopped event
	 *
	 * @param event
	 *            An event
	 */
	@Mod.EventHandler
	public void serverStopped(FMLServerStoppedEvent event) {
		LCRuntime.runtime.serverStopped(event);
	}

	/**
	 * Handler for FML signatureViolation event
	 *
	 * @param event
	 *            An event
	 */
	@Mod.EventHandler
	public void signatureViolation(FMLFingerprintViolationEvent event) {
		LCRuntime.runtime.signatureViolation(event);
	}

	/**
	 * Handler for FML receiveIMC event
	 *
	 * @param event
	 *            An event
	 */
	@Mod.EventHandler
	public void receiveIMC(IMCEvent event) {
		LCRuntime.runtime.receiveIMC(event);
	}
}
