package lc.api.defs;

import net.minecraft.world.WorldProvider;

/**
 * Dimension definition interface
 * 
 * @author AfterLifeLochie
 *
 */
public interface IDimensionDefinition extends IGameDef {

	/**
	 * Get the class responsible for providing the world management.
	 * 
	 * @return The class responsible for world management.
	 */
	public Class<? extends WorldProvider> getWorldProviderClass();

}
