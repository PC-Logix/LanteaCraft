/**
 * This file is part of the official LanteaCraft API. Please see the usage guide and
 * restrictions on use in the package-info file.
 */
package lc.api.defs;

import java.util.Map;
import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureStart;

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
	public abstract Class<? extends StructureStart> getStructureClass();

	/**
	 * Get a list of all valid structure component classes
	 * 
	 * @return A list of all valid structure component classes
	 */
	public abstract Map<String, Class<? extends StructureComponent>> getAllComponents();

	/**
	 * Ask the definition if generation is possible using this structure class
	 * 
	 * @param world
	 *            The world to generate in
	 * @param rng
	 *            The random number generator
	 * @param x
	 *            The x-coordinate
	 * @param z
	 *            The z-coordinate
	 * @return If world generation is allowed at this coordinate
	 */
	public abstract boolean canGenerateAt(World world, Random rng, int x, int z);

}
