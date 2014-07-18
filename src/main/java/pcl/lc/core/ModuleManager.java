package pcl.lc.core;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.logging.log4j.Level;

import pcl.lc.LanteaCraft;
import pcl.lc.api.internal.IModule;
import pcl.lc.cfg.ConfigHelper;
import pcl.lc.cfg.ModuleConfig;
import pcl.lc.cfg.ModuleList;
import pcl.lc.module.ModuleCore;
import pcl.lc.module.ModuleCritters;
import pcl.lc.module.ModuleDecor;
import pcl.lc.module.ModuleIntegration;
import pcl.lc.module.ModuleMachine;
import pcl.lc.module.ModulePower;
import pcl.lc.module.ModuleStargates;
import pcl.lc.module.ModuleWorldGenerator;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

/**
 * LanteaCraft module compartment manager.
 * 
 * @author AfterLifeLochie
 * 
 */
public class ModuleManager {

	/**
	 * The map of all Modules.
	 * 
	 * @author AfterLifeLochie
	 * 
	 */
	public static enum Module {
		CORE(new ModuleCore()), 
		STARGATE(new ModuleStargates()), 
		POWER(new ModulePower()), 
		MACHINE(new ModuleMachine()), 
		WORLDGEN(new ModuleWorldGenerator()), 
		CRITTERS(new ModuleCritters()), 
		DECOR(new ModuleDecor()), 
		INTEGRATION(new ModuleIntegration()); /* , GALAXY(new ModuleGalaxy()); */

		private final IModule moduleOf;
		private boolean loaded = false;

		private Module(IModule moduleOf) {
			this.moduleOf = moduleOf;
		}

		/**
		 * Get access to the Module singleton this Module item contains.
		 * 
		 * @return An {@link IModule} singleton.
		 */
		public IModule moduleOf() {
			return moduleOf;
		}

		/**
		 * Determines if this Module has been loaded; a Module is in a loaded
		 * state if it meets all pre-conditions for loading and passes through
		 * preInit, init and postInit without raising an Exception.
		 * 
		 * @return If the Module is currently loaded.
		 */
		public boolean isLoaded() {
			return loaded;
		}
	}

	/**
	 * The container for all loaded Module instances in the session.
	 */
	private static ArrayList<Module> loadedModules = new ArrayList<Module>();

	/**
	 * The global module config container
	 */
	private static ModuleList moduleConfig;

	public ModuleManager(ModuleList moduleConfig) {
		ModuleManager.moduleConfig = moduleConfig;
	}

	/**
	 * Pre-initialize the ModuleManager and all child Modules.
	 */
	public void preInit(FMLPreInitializationEvent event) {
		Set<Module> modules = EnumSet.allOf(Module.class);
		Iterator<Module> i = modules.iterator();
		while (i.hasNext()) {
			Module theModule = i.next();
			if (!isModuleEnabled(theModule)) {
				LanteaCraft.getLogger().log(Level.INFO,
						"Removing module " + theModule + " because it is disabled in config.");
				i.remove();
			}
		}

		boolean flag = true;
		while (flag) {
			flag = false;
			Iterator<Module> j = modules.iterator();
			while (j.hasNext()) {
				Module theModule = j.next();
				Set<Module> dependencies = theModule.moduleOf().getDependencies();
				if (dependencies != null && !modules.containsAll(dependencies)) {
					LanteaCraft.getLogger().log(Level.INFO,
							"Removing module " + theModule + " because it is missing dependencies.");
					j.remove();
					flag = true;
				}
			}
		}

		ArrayList<Module> loadStack = new ArrayList<Module>();
		loadStack.add(Module.CORE);

		int p = 0;
		while (modules.size() > 0 && 255 > p) {
			Iterator<Module> k = modules.iterator();
			p++;
			while (k.hasNext()) {
				Module theModule = k.next();
				if (theModule == Module.CORE) {
					k.remove();
					continue;
				}
				Set<Module> loadAfter = theModule.moduleOf().getLoadDependenciesAfter();
				if (loadAfter == null) {
					if (!loadStack.contains(theModule)) {
						LanteaCraft.getLogger().log(Level.INFO,
								"Adding module " + theModule + " as it has no load order.");
						loadStack.add(theModule);
						k.remove();
					}
				} else if (loadStack.containsAll(loadAfter)) {
					int l = 0;
					for (Module mod : loadAfter)
						if (loadStack.indexOf(mod) > l)
							l = loadStack.indexOf(mod);
					LanteaCraft.getLogger().log(Level.INFO,
							"Adding module " + theModule + " as it has all parents resolved.");
					loadStack.add(l + 1, theModule);
					k.remove();
				} else
					LanteaCraft.getLogger().log(Level.INFO,
							"Cannot add module " + theModule + "; waiting for other loading data.");

			}
		}
		if (p >= 254)
			LanteaCraft.getLogger().log(Level.WARN,
					"There were issues resolving dependencies; full resolution was aborted.");

		loadedModules.addAll(loadStack);

		StringBuilder stack = new StringBuilder().append("Load order: ");
		for (int m = 0; m < loadedModules.size(); m++)
			stack.append("[").append(m).append("=>").append(loadedModules.get(m)).append("] ");
		LanteaCraft.getLogger().log(Level.INFO, stack.toString());

		for (Module mod : loadedModules) {
			mod.loaded = true;
			mod.moduleOf().preInit(event);
		}
	}

	/**
	 * Initialize all child modules.
	 */
	public void init(FMLInitializationEvent event) {
		for (Module mod : loadedModules)
			mod.moduleOf().init(event);
	}

	/**
	 * Post-initialize all child modules.
	 */
	public void postInit(FMLPostInitializationEvent event) {
		for (Module mod : loadedModules)
			mod.moduleOf().postInit(event);
	}

	/**
	 * Determine if a Module is enabled in configuration.
	 * 
	 * @param theModule
	 *            The module object.
	 * @return If the Module is enabled.
	 */
	private boolean isModuleEnabled(Module theModule) {
		ModuleConfig config = ConfigHelper.findModuleConfigByName(moduleConfig, theModule.name());
		return (Boolean) config.parameters().get("enabled");
	}

	public static ModuleConfig getConfig(IModule module) {
		for (Module list : Module.values())
			if (list.loaded && list.moduleOf.equals(module))
				return ConfigHelper.findModuleConfigByName(moduleConfig, list.name());
		return null;
	}

}
