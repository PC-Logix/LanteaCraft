package pcl.lc.module;

import java.util.EnumSet;
import java.util.Set;

import net.minecraft.item.ItemBlock;
import pcl.lc.LanteaCraft;
import pcl.lc.api.internal.IModule;
import pcl.lc.core.ModuleManager.Module;
import pcl.lc.core.ResourceAccess;
import pcl.lc.module.power.block.BlockNaquadahGenerator;
import pcl.lc.module.power.gui.ContainerNaquadahGenerator;
import pcl.lc.module.power.item.ItemEnergyCrystal;
import pcl.lc.module.power.item.ItemZPM;
import pcl.lc.module.power.render.BlockNaquadahGeneratorRenderer;
import pcl.lc.module.power.render.ModelNaquadahGenerator;
import pcl.lc.module.power.render.TileNaquadahGeneratorRenderer;
import pcl.lc.module.power.tile.TileNaquadahGenerator;
import pcl.lc.util.CreativeTabHelper;
import pcl.lc.util.RegistrationHelper;
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
		public static ModelNaquadahGenerator modelNaquadahGenerator;
		public static TileNaquadahGeneratorRenderer tileEntityNaquadahGeneratorRenderer;
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

	}

	@Override
	public void init(FMLInitializationEvent event) {
		Blocks.naquadahGenerator = RegistrationHelper.registerBlock(BlockNaquadahGenerator.class, ItemBlock.class,
				"naquadahGenerator", CreativeTabHelper.getTab("LanteaCraft: Machines"));
		GameRegistry.registerTileEntity(TileNaquadahGenerator.class, "tileEntityNaquadahGenerator");

		Items.energyCrystal = RegistrationHelper.registerItem(ItemEnergyCrystal.class, "energyCrystal",
				CreativeTabHelper.getTab("LanteaCraft"));
		Items.zpm = RegistrationHelper.registerItem(ItemZPM.class, "zpm", CreativeTabHelper.getTab("LanteaCraft"));

		RegistrationHelper.registerContainer(LanteaCraft.EnumGUIs.NaquadahGenerator.ordinal(),
				ContainerNaquadahGenerator.class);

		if (event.getSide() == Side.CLIENT) {
			Render.modelNaquadahGenerator = new ModelNaquadahGenerator(
					ResourceAccess.getNamedResource("models/model_naquadah_generator.obj"));

			Render.tileEntityNaquadahGeneratorRenderer = new TileNaquadahGeneratorRenderer();
			RegistrationHelper.addTileEntityRenderer(TileNaquadahGenerator.class,
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
