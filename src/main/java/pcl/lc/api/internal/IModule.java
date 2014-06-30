package pcl.lc.api.internal;

import java.util.Set;

import pcl.lc.LanteaCraft;
import pcl.lc.core.ModuleManager;

/**
 * LanteaCraft internal module code.
 * 
 * @author AfterLifeLochie
 * 
 */
public interface IModule {

	/**
	 * Get a list of dependencies for this Module. If not all dependencies can
	 * be resolved, the module will not be loaded at runtime.
	 * 
	 * @return A list of module dependencies.
	 */
	public abstract Set<ModuleManager.Module> getDependencies();

	/**
	 * Get a list of dependencies which must be loaded before this module should
	 * be loaded. If not all dependencies can be resolved, the module will not
	 * load at runtime.
	 * 
	 * @return A list of modules which must be loaded before this.
	 */
	public abstract Set<ModuleManager.Module> getLoadDependenciesAfter();

	/**
	 * Module pre-init event handler. Called as if from {@link LanteaCraft#preInit(cpw.mods.fml.common.event.FMLPreInitializationEvent)}.
	 */
	public abstract void preInit();

	/**
	 * Module init event handler. Called as if from {@link LanteaCraft#init(cpw.mods.fml.common.event.FMLInitializationEvent)}.
	 */
	public abstract void init();

	/**
	 * Module post-init event handler. Called as if from {@link LanteaCraft#postInit(cpw.mods.fml.common.event.FMLPostInitializationEvent)}.
	 */
	public abstract void postInit();

}
