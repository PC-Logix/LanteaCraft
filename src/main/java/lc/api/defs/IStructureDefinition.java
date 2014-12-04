/**
 * This file is part of the official LanteaCraft API. Please see the usage guide and
 * restrictions on use in the package-info file.
 */
package lc.api.defs;

import net.minecraft.world.gen.structure.MapGenStructure;

/**
 * Game world-generation structure container interface
 *
 * @author AfterLifeLochie
 *
 */
public interface IStructureDefinition extends IGameDef {

	/**
	 * Get the name of the structure type
	 *
	 * @return The name of the structure type
	 */
	public abstract String getName();

	/**
	 * Get the class responsible for the structure generation
	 *
	 * @return The class responsible for the structure generation
	 */
	public abstract Class<? extends MapGenStructure> getStructureClass();

}
