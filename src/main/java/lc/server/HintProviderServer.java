package lc.server;

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

}
