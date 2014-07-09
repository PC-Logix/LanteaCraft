package pcl.lc.module;

import java.util.EnumSet;
import java.util.Set;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import pcl.common.helpers.RegistrationHelper;
import pcl.lc.api.internal.IModule;
import pcl.lc.core.ModuleManager.Module;
import pcl.lc.module.machine.block.BlockCrystalInfuser;
import pcl.lc.module.machine.render.ModelCrystalInfuser;
import pcl.lc.module.machine.render.TileCrystalInfuserRenderer;
import pcl.lc.module.machine.tile.TileCrystalInfuser;

public class ModuleMachine implements IModule {

	public static class Blocks {
		public static BlockCrystalInfuser infuser;
	}

	public static class Items {

	}

	public static class Render {
		public static ModelCrystalInfuser modelCrystalInfuser;
		public static TileCrystalInfuserRenderer tileCrystalInfuserRenderer;
	}

	@Override
	public Set<Module> getDependencies() {
		return EnumSet.of(Module.CORE);
	}

	@Override
	public Set<Module> getLoadDependenciesAfter() {
		return EnumSet.of(Module.CORE);
	}

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public void init(FMLInitializationEvent event) {
		Blocks.infuser = RegistrationHelper.registerBlock(BlockCrystalInfuser.class, "blockCrystalInfuser");
		GameRegistry.registerTileEntity(TileCrystalInfuser.class, "tileEntityCrystalInfuser");

		if (event.getSide() == Side.CLIENT) {
			Render.modelCrystalInfuser = new ModelCrystalInfuser();
			Render.tileCrystalInfuserRenderer = new TileCrystalInfuserRenderer();
			RegistrationHelper.addTileEntityRenderer(TileCrystalInfuser.class, Render.tileCrystalInfuserRenderer);
		}

	}

	@Override
	public void postInit(FMLPostInitializationEvent event) {
		// TODO Auto-generated method stub

	}

}
