package pcl.lc.module;

import java.util.EnumSet;
import java.util.Set;

import pcl.common.helpers.RegistrationHelper;
import pcl.lc.LanteaCraft.Blocks;
import pcl.lc.blocks.BlockLanteaDecor;
import pcl.lc.blocks.BlockNaquadah;
import pcl.lc.items.ItemLanteaDecor;
import pcl.lc.module.ModuleManager.Module;

public class ModuleDecor implements IModule {

	@Override
	public Set<Module> getDependencies() {
		return EnumSet.of(Module.CORE);
	}

	@Override
	public Set<Module> getLoadDependenciesAfter() {
		return null;
	}

	@Override
	public void preInit() {
		// TODO Auto-generated method stub

	}

	@Override
	public void init() {
		Blocks.decorBlock = RegistrationHelper.registerBlock(BlockLanteaDecor.class, ItemLanteaDecor.class, "lanteaDecor");

	}

	@Override
	public void postInit() {
		// TODO Auto-generated method stub

	}

}
