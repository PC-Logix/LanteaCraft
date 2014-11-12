package lc.dimensions.abydos;

import lc.api.defs.IDimensionDefinition;
import lc.core.ResourceAccess;
import lc.generation.AbydosPyramid;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.gen.structure.MapGenStructureIO;
import net.minecraftforge.common.DimensionManager;

public class AbydosDimension implements IDimensionDefinition {

	public AbydosDimension(final int providerId, final int dimensionId) {
		DimensionManager.registerProviderType(providerId,
				getWorldProviderClass(), false);
		DimensionManager.registerDimension(dimensionId, providerId);
		MapGenStructureIO
				.func_143031_a(AbydosPyramid.class, ResourceAccess
						.formatResourceName("${ASSET_KEY}:AbydosPyramid"));
	}

	@Override
	public Class<? extends WorldProvider> getWorldProviderClass() {
		return AbydosWorldProvider.class;
	}

}
