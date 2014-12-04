package lc.common.base.generation.structure;

import java.util.Random;

import lc.common.LCLog;
import lc.generation.AbydosPyramid;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureStart;

/**
 * LanteaCraft structure generator feature start.
 * 
 * @author AfterLifeLochie
 *
 */
public class LCFeatureStart extends StructureStart {
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
		/*
		 * TODO: Need to make a way to make MapGenFeatureStructureStart abstract
		 * to allow multiple types of generators!
		 */
		AbydosPyramid pyramid = new AbydosPyramid(rng, chunkX * 16, chunkZ * 16);
		LCLog.info(String.format("Placing pyramid at (%s, %s)", chunkX * 16, chunkZ * 16));
		components.add(pyramid);
		updateBoundingBox();
	}
}