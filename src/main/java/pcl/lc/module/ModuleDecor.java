package pcl.lc.module;

import java.util.EnumSet;
import java.util.Set;
import java.util.logging.Level;

import cpw.mods.fml.common.registry.GameRegistry;
import pcl.common.helpers.RegistrationHelper;
import pcl.lc.LanteaCraft;
import pcl.lc.LanteaCraft.Blocks;
import pcl.lc.blocks.BlockLanteaDecor;
import pcl.lc.blocks.BlockLanteaDecorStair;
import pcl.lc.items.ItemLanteaDecor;
import pcl.lc.module.ModuleManager.Module;

public class ModuleDecor implements IModule {

	private BlockLanteaDecorStair lanteaSteelDecorStair;
	private BlockLanteaDecorStair lanteaPatternedSteelDecorStair;
	private BlockLanteaDecorStair goauldGoldDecorStair;
	private BlockLanteaDecorStair goauldPatternedGoldDecorStair;

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
		lanteaSteelDecorStair = RegistrationHelper.registerStairDecal("lanteaSteelDecorStair", 1);
		lanteaPatternedSteelDecorStair = RegistrationHelper.registerStairDecal("lanteaPatternedSteelDecorStair", 2);
		goauldGoldDecorStair = RegistrationHelper.registerStairDecal("goauldGoldDecorStair", 3);
		goauldPatternedGoldDecorStair = RegistrationHelper.registerStairDecal("goauldPatternedGoldDecorStair", 4);
	}

	@Override
	public void postInit() {
		// TODO Auto-generated method stub

	}

}
