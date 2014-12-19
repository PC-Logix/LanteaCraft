package lc.common.base.generation;

import java.util.Random;

import lc.common.LCLog;
import lc.common.base.generation.scattered.LCScatteredFeatureGenerator;
import lc.common.base.generation.structure.LCFeatureGenerator;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import cpw.mods.fml.common.IWorldGenerator;

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
		scatteredGenerator.func_151539_a(chunkProvider, world, chunkX, chunkZ, null);
		featureGenerator.func_151539_a(chunkProvider, world, chunkX, chunkX, null);

		/*
		 * Ask the mater generators to actually place the structures it has
		 * declared in the world.
		 */
		boolean flag = scatteredGenerator.generateStructuresInChunk(world, random, chunkX, chunkZ);
		if (!flag)
			flag = featureGenerator.generateStructuresInChunk(world, random, chunkX, chunkZ);
	}

}
