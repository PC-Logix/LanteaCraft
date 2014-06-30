package pcl.lc.module;

import java.util.EnumSet;
import java.util.Set;

import pcl.common.helpers.RegistrationHelper;
import pcl.lc.LanteaCraft;
import pcl.lc.api.internal.IModule;
import pcl.lc.core.ModuleManager.Module;
import pcl.lc.module.power.block.BlockNaquadahGenerator;
import pcl.lc.module.power.gui.ContainerNaquadahGenerator;
import pcl.lc.module.power.item.ItemEnergyCrystal;
import pcl.lc.module.power.item.ItemZPM;
import pcl.lc.module.power.render.BlockNaquadahGeneratorRenderer;
import pcl.lc.module.power.render.NaquadahGeneratorModel;
import pcl.lc.module.power.render.TileEntityNaquadahGeneratorRenderer;
import pcl.lc.module.power.tile.TileEntityNaquadahGenerator;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;

public class ModulePower implements IModule {

	public static class Blocks {
		public static BlockNaquadahGenerator naquadahGenerator;
	}

	public static class Items {
		public static ItemEnergyCrystal energyCrystal;
		public static ItemZPM zpm;
	}

	public static class Render {
		public static NaquadahGeneratorModel modelNaquadahGenerator;
		public static TileEntityNaquadahGeneratorRenderer tileEntityNaquadahGeneratorRenderer;
		public static BlockNaquadahGeneratorRenderer blockNaquadahGeneratorRenderer;

	}

	@Override
	public Set<Module> getDependencies() {
		return EnumSet.of(Module.CORE);
	}

	@Override
	public Set<Module> getLoadDependenciesAfter() {
		return EnumSet.of(Module.CORE, Module.STARGATE);
	}

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		Blocks.naquadahGenerator = RegistrationHelper.registerBlock(BlockNaquadahGenerator.class, "naquadahGenerator");
		GameRegistry.registerTileEntity(TileEntityNaquadahGenerator.class, "tileEntityNaquadahGenerator");

		Items.energyCrystal = RegistrationHelper.registerItem(ItemEnergyCrystal.class, "energyCrystal");
		Items.zpm = RegistrationHelper.registerItem(ItemZPM.class, "zpm");
	}

	@Override
	public void init(FMLInitializationEvent event) {
		LanteaCraft.getProxy().addContainer(LanteaCraft.EnumGUIs.NaquadahGenerator.ordinal(),
				ContainerNaquadahGenerator.class);

		if (event.getSide() == Side.CLIENT) {
			Render.modelNaquadahGenerator = new NaquadahGeneratorModel(
					LanteaCraft.getResource("models/naquadah_generator.obj"));

			Render.tileEntityNaquadahGeneratorRenderer = new TileEntityNaquadahGeneratorRenderer();
			RegistrationHelper.addTileEntityRenderer(TileEntityNaquadahGenerator.class,
					Render.tileEntityNaquadahGeneratorRenderer);

			Render.blockNaquadahGeneratorRenderer = new BlockNaquadahGeneratorRenderer();
			RegistrationHelper.registerRenderer(Render.blockNaquadahGeneratorRenderer);
		}
	}

	@Override
	public void postInit(FMLPostInitializationEvent event) {
		// TODO Auto-generated method stub

	}

}
