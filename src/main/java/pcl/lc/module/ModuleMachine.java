package pcl.lc.module;

import java.util.EnumSet;
import java.util.Set;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import pcl.common.helpers.RegistrationHelper;
import pcl.lc.LanteaCraft;
import pcl.lc.api.internal.IModule;
import pcl.lc.core.ModuleManager.Module;
import pcl.lc.module.core.render.BlockModelRenderer;
import pcl.lc.module.machine.block.BlockCrystalInfuser;
import pcl.lc.module.machine.block.BlockTablePress;
import pcl.lc.module.machine.gui.ContainerCrystalInfuser;
import pcl.lc.module.machine.render.ModelCrystalInfuser;
import pcl.lc.module.machine.render.ModelTablePress;
import pcl.lc.module.machine.render.TileCrystalInfuserRenderer;
import pcl.lc.module.machine.render.TileTablePressRenderer;
import pcl.lc.module.machine.tile.TileCrystalInfuser;
import pcl.lc.module.machine.tile.TileTablePress;

public class ModuleMachine implements IModule {

	public static class Blocks {
		public static BlockCrystalInfuser infuser;
		public static BlockTablePress press;
	}

	public static class Items {

	}

	public static class Render {
		public static TileCrystalInfuserRenderer tileCrystalInfuserRenderer;
		public static TileTablePressRenderer tileTablePressRenderer;
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
		Blocks.press = RegistrationHelper.registerBlock(BlockTablePress.class, "blockTablePress");
		GameRegistry.registerTileEntity(TileCrystalInfuser.class, "tileEntityCrystalInfuser");
		GameRegistry.registerTileEntity(TileTablePress.class, "tileEntityTablePress");

		RegistrationHelper.registerContainer(LanteaCraft.EnumGUIs.CrystalInfuser.ordinal(),
				ContainerCrystalInfuser.class);

		if (event.getSide() == Side.CLIENT) {
			Render.tileCrystalInfuserRenderer = new TileCrystalInfuserRenderer();
			Render.tileTablePressRenderer = new TileTablePressRenderer();
			RegistrationHelper.addTileEntityRenderer(TileCrystalInfuser.class, Render.tileCrystalInfuserRenderer);
			RegistrationHelper.addTileEntityRenderer(TileTablePress.class, Render.tileTablePressRenderer);
			
			BlockModelRenderer.registerModelForBlock(Blocks.infuser, new ModelCrystalInfuser(), false, true);
			BlockModelRenderer.registerModelForBlock(Blocks.press, new ModelTablePress(), false, true);
		}
	}

	@Override
	public void postInit(FMLPostInitializationEvent event) {
		// TODO Auto-generated method stub

	}

}
