package lc.core;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import lc.api.ILCAPIProxy;
import lc.api.components.IRegistryContainer;
import lc.api.defs.Blocks;
import lc.api.defs.HintProvider;
import lc.api.defs.Items;
import lc.common.IHintProvider;
import lc.common.LCLog;
import lc.common.impl.registry.DefinitionRegistry;
import lc.common.impl.registry.RecipeRegistry;
import lc.common.impl.registry.RegistryContainer;
import lc.common.network.LCPacketPipeline;
import lc.common.util.LCCreativeTabManager;

/**
 * LanteaCraft main mod container
 * 
 * @author AfterLifeLochie
 * 
 */
public class LCRuntime implements ILCAPIProxy {

	/** The runtime instance */
	public static final LCRuntime runtime = new LCRuntime();

	/** The LC blocks container */
	private Blocks blocks = new Blocks();
	/** The LC items container */
	private Items items = new Items();
	/** Container of all API registries */
	private RegistryContainer registries = new RegistryContainer();

	/** Container of all registrations */
	private RegistrationContainer container = new RegistrationContainer();
	/** Network driver */
	private LCPacketPipeline network = new LCPacketPipeline();

	/** Hints provider */
	@HintProvider(serverClass = "lc.core.HintProviderServer", clientClass = "lc.core.HintProviderClient")
	private IHintProvider hints;

	private LCRuntime() {
	}

	@Override
	public boolean signedState() {
		return !BuildInfo.isDevelopmentEnvironment();
	}

	@Override
	public Blocks blocks() {
		return blocks;
	}

	@Override
	public Items items() {
		return items;
	}

	@Override
	public IRegistryContainer registries() {
		return registries;
	}

	public LCPacketPipeline network() {
		return network;
	}

	public IHintProvider hints() {
		return hints;
	}

	public void preinit(FMLPreInitializationEvent event) {
		LCLog.debug("LCRuntime entering phase preinit");
		LCCreativeTabManager.registerTab("LanteaCraft", null);
		container.preinit(this, event);
		hints.preInit();
	}

	public void init(FMLInitializationEvent event) {
		LCLog.debug("LCRuntime entering phase init");
		container.init(this, event);
		network.init(BuildInfo.modID);
		((DefinitionRegistry) registries().definitions()).init(this, event);
		((RecipeRegistry) registries().recipes()).init(this, event);
		hints.init();
	}

	public void postinit(FMLPostInitializationEvent event) {
		LCLog.debug("LCRuntime entering phase postinit");
		container.postinit(this, event);
		hints.postInit();
	}

}
