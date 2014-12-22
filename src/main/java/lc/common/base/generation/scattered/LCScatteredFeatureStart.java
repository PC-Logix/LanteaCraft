package lc.common.base.generation.scattered;

import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureStart;

/**
 * LanteaCraft scattered feature start. You should create and generate
 * components here.
 *
 * @author AfterLifeLochie
 *
 */
public abstract class LCScatteredFeatureStart extends StructureStart {

	/** Default constructor. */
	public LCScatteredFeatureStart() {

	}

	/**
	 * Construct a new feature start.
	 *
	 * @param world
	 *            The world object
	 * @param rng
	 *            The random number generator
	 * @param chunkX
	 *            The chunk x-coordinate
	 * @param chunkZ
	 *            The chunk z-coordinate
	 */
	public LCScatteredFeatureStart(World world, Random rng, int chunkX, int chunkZ) {
		addComponents(world, rng, 16 * chunkX, 16 * chunkZ);
		updateBoundingBox();
	}

	/**
	 * Called by the generation controller to place blocks for this structure in
	 * the world.
	 * 
	 * @param world
	 *            The world to write to
	 * @param rng
	 *            The random number generator
	 * @param cx
	 *            The x-coordinate
	 * @param cz
	 *            The y-coordinate
	 */
	protected abstract void addComponents(World world, Random rng, int cx, int cz);
}
