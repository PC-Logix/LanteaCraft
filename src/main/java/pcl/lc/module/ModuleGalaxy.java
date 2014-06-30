package pcl.lc.module;

import java.util.EnumSet;
import java.util.Set;

import net.minecraft.world.gen.structure.MapGenStructureIO;
import net.minecraftforge.common.DimensionManager;
import pcl.lc.LanteaCraft;
import pcl.lc.api.internal.IModule;
import pcl.lc.core.ModuleManager.Module;
import pcl.lc.module.galaxy.MapGenFeatureStructureStart;
import pcl.lc.module.galaxy.abydos.AbydosPyramid;
import pcl.lc.module.galaxy.abydos.AbydosWorldProvider;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class ModuleGalaxy implements IModule {

	// TODO: Change this to a config option
	public static final int __TMP_ABYDOS_IDX = 5;
	// TODO: Move the Biome ID somewhere sensible (is it even needed??)
	public static final int __TMP_ABYDOX_BMX = 1;

	@Override
	public Set<Module> getDependencies() {
		return EnumSet.of(Module.CORE, Module.CRITTERS, Module.DECOR, Module.STARGATE);
	}

	@Override
	public Set<Module> getLoadDependenciesAfter() {
		return EnumSet.of(Module.CORE, Module.CRITTERS, Module.DECOR, Module.STARGATE);
	}

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		// TODO Auto-generated method stub
	}

	@Override
	public void init(FMLInitializationEvent event) {
		DimensionManager.registerProviderType(__TMP_ABYDOS_IDX, AbydosWorldProvider.class, false);
		DimensionManager.registerDimension(__TMP_ABYDOS_IDX, __TMP_ABYDOS_IDX);
		MapGenStructureIO.registerStructure(MapGenFeatureStructureStart.class, LanteaCraft.getAssetKey()
				+ ":LanteaCraft");
		MapGenStructureIO.func_143031_a(AbydosPyramid.class, LanteaCraft.getAssetKey() + ":AbydosPyramid");
	}

	@Override
	public void postInit(FMLPostInitializationEvent event) {
		// TODO Auto-generated method stub

	}

}
