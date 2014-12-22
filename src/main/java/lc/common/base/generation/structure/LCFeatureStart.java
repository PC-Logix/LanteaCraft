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
	public LCFeatureStart(World world, Random rng, int chunkX, int chunkZ) {
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