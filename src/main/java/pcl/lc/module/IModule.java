package pcl.lc.module;

import java.util.Set;

public interface IModule {

	public abstract Set<ModuleManager.Module> getDependencies();

	public abstract Set<ModuleManager.Module> getLoadDependenciesAfter();

	public abstract void preInit();

	public abstract void init();

	public abstract void postInit();

}
