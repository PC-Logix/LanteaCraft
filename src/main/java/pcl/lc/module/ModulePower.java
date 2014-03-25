package pcl.lc.module;

import java.util.EnumSet;
import java.util.Set;

import pcl.common.helpers.RegistrationHelper;
import pcl.lc.LanteaCraft;
import pcl.lc.LanteaCraft.Blocks;
import pcl.lc.LanteaCraft.Items;
import pcl.lc.api.internal.IModule;
import pcl.lc.blocks.BlockNaquadahGenerator;
import pcl.lc.containers.ContainerNaquadahGenerator;
import pcl.lc.core.ModuleManager.Module;
import pcl.lc.items.ItemEnergyCrystal;
import pcl.lc.items.ItemZPM;
import pcl.lc.tileentity.TileEntityNaquadahGenerator;
import cpw.mods.fml.common.registry.GameRegistry;

public class ModulePower implements IModule {

	@Override
	public Set<Module> getDependencies() {
		return EnumSet.of(Module.CORE);
	}

	@Override
	public Set<Module> getLoadDependenciesAfter() {
		return EnumSet.of(Module.CORE, Module.STARGATE);
	}

	@Override
	public void preInit() {
		Blocks.naquadahGenerator = RegistrationHelper.registerBlock(BlockNaquadahGenerator.class, "naquadahGenerator");
		GameRegistry.registerTileEntity(TileEntityNaquadahGenerator.class, "tileEntityNaquadahGenerator");

		Items.energyCrystal = RegistrationHelper.registerItem(ItemEnergyCrystal.class, "energyCrystal");
		Items.zpm = RegistrationHelper.registerItem(ItemZPM.class, "zpm");
	}

	@Override
	public void init() {
		LanteaCraft.getProxy().addContainer(LanteaCraft.EnumGUIs.NaquadahGenerator.ordinal(),
				ContainerNaquadahGenerator.class);
	}

	@Override
	public void postInit() {
		// TODO Auto-generated method stub

	}

}
