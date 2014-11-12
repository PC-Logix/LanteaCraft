package lc.api.defs;

import net.minecraft.world.WorldProvider;

public interface IDimensionDefinition {

	public Class<? extends WorldProvider> getWorldProviderClass();

}
