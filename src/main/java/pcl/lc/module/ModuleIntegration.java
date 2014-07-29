package pcl.lc.module;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.logging.log4j.Level;

import pcl.lc.LanteaCraft;
import pcl.lc.api.internal.Agent;
import pcl.lc.api.internal.IIntegrationAgent;
import pcl.lc.api.internal.IModule;
import pcl.lc.cfg.ConfigHelper;
import pcl.lc.cfg.ModuleConfig;
import pcl.lc.core.ModuleManager;
import pcl.lc.core.ModuleManager.Module;
import pcl.lc.module.integration.OpenComputersAgent;
import pcl.lc.module.integration.WailaAgent;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class ModuleIntegration implements IModule {

	static {
		ModuleIntegration.clazz_integration = new ArrayList<Class<? extends IIntegrationAgent>>();
		// ModuleIntegration.registerIntegrationAgent(ComputerCraftAgent.class);
		ModuleIntegration.registerIntegrationAgent(OpenComputersAgent.class);
		ModuleIntegration.registerIntegrationAgent(WailaAgent.class);
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
	public void preInit(FMLPreInitializationEvent event) {
		ModuleConfig config = ModuleManager.getConfig(this);
		LanteaCraft.getLogger().log(Level.INFO, "Preparing integration module loading.");
		Iterator<Class<? extends IIntegrationAgent>> agents = clazz_integration.iterator();
		while (agents.hasNext()) {
			Class<? extends IIntegrationAgent> agent = agents.next();
			Annotation[] annotations = agent.getAnnotations();
			if (annotations.length == 0)
				LanteaCraft.getLogger()
						.log(Level.INFO,
								String.format("Not loading class %s because it is missing an Agent directive.",
										agent.getName()));
			for (Annotation annotate : annotations)
				if (annotate.annotationType().equals(Agent.class)) {
					Agent theAgent = (Agent) annotate;
					String modName = theAgent.modname();
					boolean configEnabled = ConfigHelper.getOrSetBooleanParam(config, "ExternalMod", modName, "enabled",
							String.format("Enable integration with the external mod %s.", modName), true);
					if (Loader.isModLoaded(theAgent.modname()) && configEnabled)
						try {
							LanteaCraft.getLogger().log(Level.INFO,
									String.format("Hot-loading agent %s", agent.getName()));
							IIntegrationAgent singleton = agent.newInstance();
							this.agents.add(singleton);
							LanteaCraft.getLogger().log(Level.INFO, String.format("Loaded agent %s.", agent.getName()));
						} catch (Throwable t) {
							LanteaCraft.getLogger().log(Level.WARN, "Exception in integration agent initalizer.", t);
						}
					else
						LanteaCraft.getLogger().log(
								Level.INFO,
								String.format("Not loading agent %s, missing mod %s.", agent.getName(),
										theAgent.modname()));
				}
		}
		LanteaCraft.getLogger().log(Level.INFO, "Done hotloading integration modules.");
	}

	@Override
	public void init(FMLInitializationEvent event) {
		Iterator<IIntegrationAgent> agents = this.agents.iterator();
		while (agents.hasNext())
			try {
				agents.next().init();
			} catch (Throwable t) {
				LanteaCraft.getLogger().log(Level.WARN, "Integration agent threw init-time exception.", t);
			}
	}

	@Override
	public void postInit(FMLPostInitializationEvent event) {
		Iterator<IIntegrationAgent> agents = this.agents.iterator();
		while (agents.hasNext())
			try {
				agents.next().postInit();
			} catch (Throwable t) {
				LanteaCraft.getLogger().log(Level.WARN, "Integration agent threw postinit-time exception.", t);
			}
	}

}
