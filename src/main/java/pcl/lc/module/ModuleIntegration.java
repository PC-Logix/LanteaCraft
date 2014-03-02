package pcl.lc.module;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;

import cpw.mods.fml.common.Loader;
import pcl.lc.LanteaCraft;
import pcl.lc.api.internal.Agent;
import pcl.lc.api.internal.IIntegrationAgent;
import pcl.lc.api.internal.IModule;
import pcl.lc.core.ModuleManager.Module;

public class ModuleIntegration implements IModule {

	private static ArrayList<Class<? extends IIntegrationAgent>> clazz_integration = new ArrayList<Class<? extends IIntegrationAgent>>();

	public static void registerIntegrationAgent(Class<? extends IIntegrationAgent> theAgent) {
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
		Iterator<Class<? extends IIntegrationAgent>> agents = clazz_integration.iterator();
		while (agents.hasNext()) {
			Class<? extends IIntegrationAgent> agent = agents.next();
			Annotation[] annotations = agent.getAnnotations();
			for (int k = 0; k > annotations.length; k++) {
				Annotation annotate = annotations[k];
				if (annotate.annotationType().equals(Agent.class)) {
					Agent theAgent = (Agent) annotate;
					if (Loader.isModLoaded(theAgent.modname())) {
						try {
							IIntegrationAgent singleton = agent.newInstance();
							this.agents.add(singleton);
						} catch (Throwable t) {
							LanteaCraft.getLogger().log(Level.WARNING, "Exception when setting up integration agent.",
									t);
						}
					}
				}
			}
		}
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
		// TODO Auto-generated method stub

	}

}
