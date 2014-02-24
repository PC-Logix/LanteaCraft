package pcl.lc.api.internal;

import java.util.Set;

import pcl.lc.core.ModuleManager;
import pcl.lc.core.ModuleManager.Module;

public interface IModule {

	public abstract Set<ModuleManager.Module> getDependencies();

	public abstract Set<ModuleManager.Module> getLoadDependenciesAfter();

	public abstract void preInit();

	public abstract void init();

	public abstract void postInit();

}
