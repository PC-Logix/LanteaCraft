package pcl.lc.module.galaxy.abydos;

import net.minecraft.world.gen.structure.MapGenStructureIO;
import net.minecraftforge.common.DimensionManager;
import pcl.lc.LanteaCraft;
import pcl.lc.module.ModuleGalaxy.DimensionConfig;
import pcl.lc.module.galaxy.IDimension;

public class AbydosDimension implements IDimension {

	public AbydosDimension(DimensionConfig config) {
		DimensionManager.registerProviderType(config.getProviderId(), AbydosWorldProvider.class, false);
		DimensionManager.registerDimension(config.getDimensionId(), config.getProviderId());
		MapGenStructureIO.func_143031_a(AbydosPyramid.class, LanteaCraft.getAssetKey() + ":AbydosPyramid");
	}

}
