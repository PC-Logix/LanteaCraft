package lc.core;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import lc.api.ILCAPIProxy;
import lc.api.components.IRegistryContainer;
import lc.api.defs.Blocks;
import lc.api.defs.Items;
import lc.common.LCLog;
import lc.common.impl.DefinitionRegistry;
import lc.common.impl.RecipeRegistry;
import lc.common.impl.RegistryContainer;
import lc.common.util.LCCreativeTabManager;

public class LCRuntime implements ILCAPIProxy {

	public static final LCRuntime runtime = new LCRuntime();

	private Blocks blocks = new Blocks();
	private Items items = new Items();
	private RegistryContainer registries = new RegistryContainer();
	private RegistrationContainer container = new RegistrationContainer();

	private LCRuntime() {
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

	public void preinit(FMLPreInitializationEvent event) {
		LCLog.debug("LCRuntime entering phase preinit");
		LCCreativeTabManager.registerTab("LanteaCraft", null);
		container.preinit(this, event);
	}

	public void init(FMLInitializationEvent event) {
		LCLog.debug("LCRuntime entering phase init");
		container.init(this, event);
		((DefinitionRegistry) registries().definitions()).init(this, event);
		((RecipeRegistry) registries().recipes()).init(this, event);
	}

	public void postinit(FMLPostInitializationEvent event) {
		LCLog.debug("LCRuntime entering phase postinit");
		container.postinit(this, event);
	}

}
