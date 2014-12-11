package lc.server;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import net.minecraftforge.common.MinecraftForge;
import lc.api.defs.IContainerDefinition;
import lc.api.defs.IRecipeDefinition;
import lc.common.IHintProvider;
import lc.common.LCLog;

/**
 * Server-side hint provider implementation
 *
 * @author AfterLifeLochie
 *
 */
public class HintProviderServer implements IHintProvider {

	/** The server hook bus */
	private final ServerEventHooks serverHookBus;

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
		// TODO Auto-generated method stub

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
