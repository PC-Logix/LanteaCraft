package pcl.lc.module;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;

import pcl.lc.LanteaCraft;

public class ModuleManager {

	public static enum Module {
		CORE(new ModuleCore()), STARGATE(new ModuleStargates()), POWER(new ModulePower()), WORLDGEN(
				new ModuleWorldGenerator()), CRITTERS(new ModuleCritters()), DECOR(new ModuleDecor()), INTEGRATION(
				new ModuleIntegration());

		private final IModule moduleOf;
		private boolean loaded = false;

		private Module(IModule moduleOf) {
			this.moduleOf = moduleOf;
		}

		public IModule moduleOf() {
			return moduleOf;
		}

		public boolean isLoaded() {
			return loaded;
		}
	}

	private static ArrayList<Module> loadedModules = new ArrayList<Module>();

	public void preInit() {
		Set<Module> modules = EnumSet.allOf(Module.class);
		Iterator<Module> i = modules.iterator();
		while (i.hasNext()) {
			Module theModule = (Module) i.next();
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
				Module theModule = (Module) j.next();
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
				Module theModule = (Module) k.next();
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
		if (p == 255)
			LanteaCraft.getLogger().log(Level.WARNING,
					"There were issues resolving dependencies; full resolution was aborted.");

		loadedModules.addAll(loadStack);

		StringBuilder stack = new StringBuilder().append("Load order: ");
		for (int m = 0; m < loadedModules.size(); m++)
			stack.append("[").append(m).append("=>").append(loadedModules.get(m)).append("] ");
		LanteaCraft.getLogger().log(Level.INFO, stack.toString());

		for (Module mod : loadedModules) {
			mod.loaded = true;
			mod.moduleOf().preInit();
		}
	}

	public void init() {
		for (Module mod : loadedModules)
			mod.moduleOf().init();
	}

	public void postInit() {
		for (Module mod : loadedModules)
			mod.moduleOf().postInit();
	}

	private boolean isModuleEnabled(Module theModule) {
		return true;
	}

}
