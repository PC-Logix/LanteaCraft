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
		addComponents(world, rng, chunkX, chunkZ);
		updateBoundingBox();
	}

	protected abstract void addComponents(World world, Random rng, int cx, int cz);
}
