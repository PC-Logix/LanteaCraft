package lc;

import lc.common.LCLog;
import lc.common.util.Tracer;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLFingerprintViolationEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms.IMCEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.common.event.FMLServerStartedEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import net.minecraftforge.fml.relauncher.FMLRelaunchLog;

import org.apache.logging.log4j.Level;

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
		Tracer.begin(this);
		LCRuntime.runtime.preinit(event);
		Tracer.end();
	}

	/**
	 * Handler for FML init event
	 *
	 * @param event
	 *            An event
	 */
	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		Tracer.begin(this);
		LCRuntime.runtime.init(event);
		Tracer.end();
	}

	/**
	 * Handler for FML postinit event
	 *
	 * @param event
	 *            An event
	 */
	@Mod.EventHandler
	public void postinit(FMLPostInitializationEvent event) {
		Tracer.begin(this);
		LCRuntime.runtime.postinit(event);
		Tracer.end();
	}

	/**
	 * Handler for FML beforeServerStarting event
	 *
	 * @param event
	 *            An event
	 */
	@Mod.EventHandler
	public void beforeServerStarting(FMLServerAboutToStartEvent event) {
		Tracer.begin(this);
		LCRuntime.runtime.beforeServerStarting(event);
		Tracer.end();
	}

	/**
	 * Handler for FML serverStarting event
	 *
	 * @param event
	 *            An event
	 */
	@Mod.EventHandler
	public void serverStarting(FMLServerStartingEvent event) {
		Tracer.begin(this);
		LCRuntime.runtime.serverStarting(event);
		Tracer.end();
	}

	/**
	 * Handler for FML serverStarted event
	 *
	 * @param event
	 *            An event
	 */
	@Mod.EventHandler
	public void serverStarted(FMLServerStartedEvent event) {
		Tracer.begin(this);
		LCRuntime.runtime.serverStarted(event);
		Tracer.end();
	}

	/**
	 * Handler for FML serverStopping event
	 *
	 * @param event
	 *            An event
	 */
	@Mod.EventHandler
	public void serverStopping(FMLServerStoppingEvent event) {
		Tracer.begin(this);
		LCRuntime.runtime.serverStopping(event);
		Tracer.end();
	}

	/**
	 * Handler for FML serverStopped event
	 *
	 * @param event
	 *            An event
	 */
	@Mod.EventHandler
	public void serverStopped(FMLServerStoppedEvent event) {
		Tracer.begin(this);
		LCRuntime.runtime.serverStopped(event);
		Tracer.end();
	}

	/**
	 * Handler for FML signatureViolation event
	 *
	 * @param event
	 *            An event
	 */
	@Mod.EventHandler
	public void signatureViolation(FMLFingerprintViolationEvent event) {
		Tracer.begin(this);
		LCRuntime.runtime.signatureViolation(event);
		Tracer.end();
	}

	/**
	 * Handler for FML receiveIMC event
	 *
	 * @param event
	 *            An event
	 */
	@Mod.EventHandler
	public void receiveIMC(IMCEvent event) {
		Tracer.begin(this);
		LCRuntime.runtime.receiveIMC(event);
		Tracer.end();
	}
}
