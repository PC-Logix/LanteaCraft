package lc.server;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraftforge.common.MinecraftForge;
import lc.api.defs.IContainerDefinition;
import lc.api.defs.IRecipeDefinition;
import lc.common.IHintProvider;
import lc.common.LCLog;
import lc.common.base.generation.LCMasterWorldGen;
import lc.common.base.generation.scattered.LCScatteredFeatureGenerator;
import lc.common.base.generation.structure.LCFeatureGenerator;

/**
 * Server-side hint provider implementation
 *
 * @author AfterLifeLochie
 *
 */
public class HintProviderServer implements IHintProvider {

	/** The server hook bus */
	private final ServerEventHooks serverHookBus;

	/** The master world generator service */
	private LCMasterWorldGen worldGenerator;

	/** Default constructor */
	public HintProviderServer() {
		LCLog.debug("HintProviderServer providing server-side hints");
		this.serverHookBus = new ServerEventHooks();
	}

	@Override
	public void preInit() {
		LCLog.debug("Providing base initialization helpers.");
		FMLCommonHandler.instance().bus().register(serverHookBus);
		MinecraftForge.EVENT_BUS.register(serverHookBus);
	}

	@Override
	public void init() {
		worldGenerator = new LCMasterWorldGen();
		GameRegistry.registerWorldGenerator(worldGenerator, 0);

	}

	@Override
	public void postInit() {
		// TODO Auto-generated method stub

	}

	@Override
	public void provideHints(IContainerDefinition definition) {
		// TODO Auto-generated method stub

	}

	@Override
	public void provideHints(IRecipeDefinition definition) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onServerStarting(FMLServerStartingEvent event) {
		serverHookBus.onServerStarting(event);
	}

	@Override
	public void onServerStopping(FMLServerStoppingEvent event) {
		serverHookBus.onServerStopping(event);
	}

}
