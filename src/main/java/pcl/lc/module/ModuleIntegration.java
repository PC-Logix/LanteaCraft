package pcl.lc.module;

import java.util.EnumSet;
import java.util.Set;

import dan200.computer.api.ComputerCraftAPI;
import pcl.lc.api.internal.IModule;
import pcl.lc.core.ModuleManager.Module;
import pcl.lc.module.integration.ComputerCraftAgent;
import pcl.lc.module.integration.OpenComputersAgent;

public class ModuleIntegration implements IModule {

	private ComputerCraftAgent integrationComputerCraftAgent;
	private OpenComputersAgent integrationOpenComputersAgent;

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
		// TODO Auto-generated method stub

	}

	@Override
	public void init() {
		integrationComputerCraftAgent = new ComputerCraftAgent();
		integrationOpenComputersAgent = new OpenComputersAgent();
	}

	@Override
	public void postInit() {
		// TODO Auto-generated method stub

	}

}
