package pcl.lc.module;

import java.util.EnumSet;
import java.util.Set;
import java.util.logging.Level;

import cpw.mods.fml.common.Loader;
import dan200.computer.api.ComputerCraftAPI;
import pcl.lc.LanteaCraft;
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
		//Lochie Kill me later... I HAD to get the mod working, and wanted OC support working.
		//Couldn't figure out how you got CC support working without stripping interfaces so blah.
		if (Loader.isModLoaded("OpenComputers")) {
            try { integrationOpenComputersAgent = new OpenComputersAgent();

                LanteaCraft.getLogger().log(Level.INFO, "Loaded OpenComputers Support!");
            }
            catch (Exception e) {
            	LanteaCraft.getLogger().log(Level.INFO, "OpenComputers not found!");
                e.printStackTrace(System.err);
            }
        }
	}

	@Override
	public void postInit() {
		// TODO Auto-generated method stub

	}

}
