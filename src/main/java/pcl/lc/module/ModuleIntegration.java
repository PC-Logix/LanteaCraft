package pcl.lc.module;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;

import pcl.lc.LanteaCraft;
import pcl.lc.api.internal.Agent;
import pcl.lc.api.internal.IIntegrationAgent;
import pcl.lc.api.internal.IModule;
import pcl.lc.core.ModuleManager.Module;
import pcl.lc.module.integration.ComputerCraftLegacyAgent;
import pcl.lc.module.integration.OpenComputersAgent;
import cpw.mods.fml.common.Loader;

public class ModuleIntegration implements IModule {

	static {
		ModuleIntegration.clazz_integration = new ArrayList<Class<? extends IIntegrationAgent>>();
		// ModuleIntegration.registerIntegrationAgent(ComputerCraftLegacyAgent.class);
		ModuleIntegration.registerIntegrationAgent(OpenComputersAgent.class);
	}

	private static ArrayList<Class<? extends IIntegrationAgent>> clazz_integration;

	public static void registerIntegrationAgent(Class<? extends IIntegrationAgent> theAgent) {
		LanteaCraft.getLogger().log(Level.INFO, "Integration agent registration: " + theAgent.getName());
		if (!ModuleIntegration.clazz_integration.contains(theAgent))
			ModuleIntegration.clazz_integration.add(theAgent);
	}

	private ArrayList<IIntegrationAgent> agents = new ArrayList<IIntegrationAgent>(clazz_integration.size());

	@Override
	public Set<Module> getDependencies() {
		return EnumSet.of(Module.CORE);
	}

	@Override
	public Set<Module> getLoadDependenciesAfter() {
		return EnumSet.of(Module.CORE);
	}

	@Override
	public void preInit() {
		LanteaCraft.getLogger().log(Level.INFO, "Preparing integration module loading.");
		Iterator<Class<? extends IIntegrationAgent>> agents = clazz_integration.iterator();
		while (agents.hasNext()) {
			Class<? extends IIntegrationAgent> agent = agents.next();
			Annotation[] annotations = agent.getAnnotations();
			for (Annotation annotate : annotations) {
				if (annotate.annotationType().equals(Agent.class)) {
					Agent theAgent = (Agent) annotate;
					if (Loader.isModLoaded(theAgent.modname()))
						try {
							LanteaCraft.getLogger().log(Level.INFO,
									String.format("Hot-loading agent %s", agent.getName()));
							IIntegrationAgent singleton = agent.newInstance();
							this.agents.add(singleton);
							LanteaCraft.getLogger().log(Level.INFO, String.format("Loaded agent %s.", agent.getName()));
							break;
						} catch (Throwable t) {
							LanteaCraft.getLogger().log(Level.WARNING, "Exception in integration agent initalizer.", t);
						}
					else
						LanteaCraft.getLogger().log(
								Level.INFO,
								String.format("Not loading agent %s, missing mod %s.", agent.getName(),
										theAgent.modname()));
				}
			}
		}
		LanteaCraft.getLogger().log(Level.INFO, "Done hotloading integration modules.");
	}

	@Override
	public void init() {
		Iterator<IIntegrationAgent> agents = this.agents.iterator();
		while (agents.hasNext())
			try {
				agents.next().init();
			} catch (Throwable t) {
				LanteaCraft.getLogger().log(Level.WARNING, "Integration agent threw init-time exception.", t);
			}
	}

	@Override
	public void postInit() {
		// Agents don't really need a postInit (for now).
	}

}
