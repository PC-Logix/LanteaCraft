package lc;

import lc.api.ILCAPIProxy;
import lc.api.IModInfo;
import lc.api.components.IRegistryContainer;
import lc.api.defs.HintProvider;
import lc.api.init.Biomes;
import lc.api.init.Blocks;
import lc.api.init.Dimensions;
import lc.api.init.Interfaces;
import lc.api.init.Items;
import lc.api.init.Recipes;
import lc.api.init.Structures;
import lc.common.GUIHandler;
import lc.common.IHintProvider;
import lc.common.LCLog;
import lc.common.base.generation.scattered.LCScatteredFeatureGenerator;
import lc.common.base.generation.structure.LCFeatureGenerator;
import lc.common.impl.registry.DefinitionRegistry;
import lc.common.impl.registry.InterfaceRegistry;
import lc.common.impl.registry.RecipeRegistry;
import lc.common.impl.registry.RegistryContainer;
import lc.common.impl.registry.StructureRegistry;
import lc.common.network.LCPacketPipeline;
import lc.common.util.LCCreativeTabManager;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import cpw.mods.fml.common.network.NetworkRegistry;

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
	private final Blocks blocks = new Blocks();
	/** The LC items container */
	private final Items items = new Items();
	/** Container of all API registries */
	private final RegistryContainer registries = new RegistryContainer();
	/** The LC biomes container */
	private final Biomes biomes = new Biomes();
	/** The LC dimensions container */
	private final Dimensions dimensions = new Dimensions();
	/** The LC recipes container */
	private final Recipes recipes = new Recipes();
	/** The LC structures container */
	private final Structures structures = new Structures();
	/** The LC interfaces container */
	private final Interfaces interfaces = new Interfaces();

	/** Container of all registrations */
	private final LCInit container = new LCInit();
	/** Network driver */
	private final LCPacketPipeline network = new LCPacketPipeline();

	/** The LC GUI handler hook */
	private final GUIHandler interfaceHook = new GUIHandler();

	/** Hints provider */
	@HintProvider(serverClass = "lc.server.HintProviderServer", clientClass = "lc.client.HintProviderClient")
	private IHintProvider hints;

	private LCRuntime() {
	}

	@Override
	public IModInfo info() {
		return BuildInfo.$;
	}

	@Override
	public boolean signedState() {
		return BuildInfo.IS_SIGNED;
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

	@Override
	public Biomes biomes() {
		return biomes;
	}

	@Override
	public Dimensions dimensions() {
		return dimensions;
	}

	@Override
	public Recipes recipes() {
		return recipes;
	}

	@Override
	public Structures structures() {
		return structures;
	}

	@Override
	public Interfaces interfaces() {
		return interfaces;
	}

	/**
	 * Get the network pipeline
	 *
	 * @return The network pipeline
	 */
	public LCPacketPipeline network() {
		return network;
	}

	/**
	 * Get the current hint provider.
	 *
	 * @return The current hint provider.
	 */
	public IHintProvider hints() {
		return hints;
	}

	/**
	 * Called during pre-initialization
	 *
	 * @param event
	 *            The FML pre initialization event.
	 */
	public void preinit(FMLPreInitializationEvent event) {
		LCLog.debug("LCRuntime entering phase preinit");
		LCCreativeTabManager.registerTab("LanteaCraft", null);
		container.preinit(this, event);
		hints.preInit();
	}

	/**
	 * Called during initialization
	 *
	 * @param event
	 *            The FML initialization event.
	 */
	public void init(FMLInitializationEvent event) {
		LCLog.debug("LCRuntime entering phase init");
		container.init(this, event);
		network.init(BuildInfo.modID);
		((DefinitionRegistry) registries().definitions()).init(this, event);
		((RecipeRegistry) registries().recipes()).init(this, event);
		((StructureRegistry) registries().structures()).init(this, event);
		((InterfaceRegistry) registries().interfaces()).init(this, event);
		NetworkRegistry.INSTANCE.registerGuiHandler(LanteaCraft.instance, interfaceHook);
		hints.init();
	}

	/**
	 * Called during post-initialization
	 *
	 * @param event
	 *            The FML post initialization event.
	 */
	public void postinit(FMLPostInitializationEvent event) {
		LCLog.debug("LCRuntime entering phase postinit");
		container.postinit(this, event);
		hints.postInit();
	}

	/**
	 * Called when a server is started
	 * 
	 * @param event
	 *            The server.
	 */
	public void serverStarting(FMLServerStartingEvent event) {
		hints.onServerStarting(event);
	}

	/**
	 * Called when a server is stopped
	 * 
	 * @param event
	 *            The server.
	 */
	public void serverStopping(FMLServerStoppingEvent event) {
		hints.onServerStopping(event);
	}

}
