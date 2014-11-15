package lc.api.defs;

import net.minecraft.world.WorldProvider;

public interface IDimensionDefinition extends IGameDef {

	public Class<? extends WorldProvider> getWorldProviderClass();

}
