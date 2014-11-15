package lc.core;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import lc.api.ILCAPIProxy;
import lc.api.components.IRegistryContainer;
import lc.api.defs.HintProvider;
import lc.api.init.Biomes;
import lc.api.init.Blocks;
import lc.api.init.Dimensions;
import lc.api.init.Items;
import lc.api.init.Recipes;
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
	/** The LC biomes container */
	private Biomes biomes = new Biomes();
	/** The LC dimensions container */
	private Dimensions dimensions = new Dimensions();
	/** The LC recipes container */
	private Recipes recipes = new Recipes();

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

}
