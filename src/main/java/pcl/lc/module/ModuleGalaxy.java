package pcl.lc.module;

import java.util.EnumSet;
import java.util.Set;

import net.minecraft.world.gen.structure.MapGenStructureIO;
import net.minecraftforge.common.DimensionManager;
import pcl.lc.LanteaCraft;
import pcl.lc.api.internal.IModule;
import pcl.lc.core.ModuleManager.Module;
import pcl.lc.dimension.MapGenFeatureStructureStart;
import pcl.lc.dimension.abydos.AbydosPyramid;
import pcl.lc.dimension.abydos.AbydosWorldProvider;

public class ModuleGalaxy implements IModule {

	public static final int __TMP_ABYDOS_IDX = 5;
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
	public void preInit() {
		// TODO Auto-generated method stub
	}

	@Override
	public void init() {
		DimensionManager.registerProviderType(__TMP_ABYDOS_IDX, AbydosWorldProvider.class, false);
		DimensionManager.registerDimension(__TMP_ABYDOS_IDX, __TMP_ABYDOS_IDX);
		MapGenStructureIO.registerStructure(MapGenFeatureStructureStart.class, LanteaCraft.getAssetKey()
				+ ":LanteaCraft");
		MapGenStructureIO.func_143031_a(AbydosPyramid.class, LanteaCraft.getAssetKey() + ":AbydosPyramid");
	}

	@Override
	public void postInit() {
		// TODO Auto-generated method stub

	}

}
