package pcl.lc.module;

import java.util.EnumSet;
import java.util.Set;

import dan200.computer.api.ComputerCraftAPI;
import pcl.lc.module.ModuleManager.Module;
import pcl.lc.module.integration.ComputerCraftAgent;

public class ModuleIntegration implements IModule {

	private ComputerCraftAgent integrationComputerCraftAgent;

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
	}

	@Override
	public void postInit() {
		// TODO Auto-generated method stub

	}

}
