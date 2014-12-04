/**
 * This file is part of the official LanteaCraft API. Please see the usage guide and
 * restrictions on use in the package-info file.
 */
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
