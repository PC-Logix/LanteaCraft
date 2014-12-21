package lc.common.base.generation;

import java.util.Random;

import lc.common.LCLog;
import lc.common.base.generation.scattered.LCScatteredFeatureGenerator;
import lc.common.base.generation.structure.LCFeatureGenerator;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import cpw.mods.fml.common.IWorldGenerator;

/**
 * LanteaCraft world decorator hook.
 * 
 * @author AfterLifeLochie
 *
 */
public class LCMasterWorldGen implements IWorldGenerator {

	private final LCScatteredFeatureGenerator scatteredGenerator = new LCScatteredFeatureGenerator();
	private final LCFeatureGenerator featureGenerator = new LCFeatureGenerator();

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator,
			IChunkProvider chunkProvider) {
		/*
		 * Ask the master generators to generate all known structures. It
		 * doesn't matter if the Block[] array is null at this stage.
		 */
		try {
			scatteredGenerator.func_151539_a(chunkProvider, world, chunkX, chunkZ, null);
		} catch (Throwable t) {
			LCLog.warn("Problem populating scattered features for chunk.", t);
		}
		try {
			featureGenerator.func_151539_a(chunkProvider, world, chunkX, chunkX, null);
		} catch (Throwable t) {
			LCLog.warn("Problem populating features for chunk.", t);
		}

		/*
		 * Ask the mater generators to actually place the structures it has
		 * declared in the world.
		 */
		boolean flag = false;
		try {
			scatteredGenerator.generateStructuresInChunk(world, random, chunkX, chunkZ);
		} catch (Throwable t) {
			LCLog.warn("Failed to generate scattered structures.", t);
		}
		if (!flag)
			try {
				flag = featureGenerator.generateStructuresInChunk(world, random, chunkX, chunkZ);
			} catch (Throwable t) {
				LCLog.warn("Failed to generate structures.", t);
			}
	}
}
