package lc.common.base.generation.structure;

import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureStart;

/**
 * LanteaCraft structure generator feature start. You should create and generate
 * components here.
 *
 * @author AfterLifeLochie
 *
 */
public abstract class LCFeatureStart extends StructureStart {
	/** Default constructor */
	public LCFeatureStart() {
	}

	/**
	 * Create a new Structure Start
	 *
	 * @param world
	 *            The world
	 * @param rng
	 *            The random number generator
	 * @param chunkX
	 *            The chunk x-coordinate
	 * @param chunkZ
	 *            The chunk y-coordinate
	 */
	@SuppressWarnings("unchecked")
	public LCFeatureStart(World world, Random rng, int chunkX, int chunkZ) {
		addComponents(world, rng, chunkX, chunkZ);
		updateBoundingBox();
	}

	protected abstract void addComponents(World world, Random rng, int cx, int cz);
}