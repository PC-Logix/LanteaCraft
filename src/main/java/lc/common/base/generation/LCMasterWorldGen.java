package lc.common.base.generation;

import java.util.Random;

import lc.common.LCLog;
import lc.common.base.generation.decoration.LCChunkDecorator;
import lc.common.base.generation.scattered.LCScatteredFeatureGenerator;
import lc.common.base.generation.structure.LCFeatureGenerator;
import lc.common.util.Tracer;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import cpw.mods.fml.common.IWorldGenerator;

/**
 * LanteaCraft world decorator hook.
 * 
 * @author AfterLifeLochie
 *
 */
public class LCMasterWorldGen implements IWorldGenerator {

	/** Scattered feature generator */
	protected final LCScatteredFeatureGenerator scatteredGenerator = new LCScatteredFeatureGenerator();
	/** Special feature generator */
	protected final LCFeatureGenerator featureGenerator = new LCFeatureGenerator();
	/** Chunk decorator generator */
	protected final LCChunkDecorator decorator = new LCChunkDecorator();

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator,
			IChunkProvider chunkProvider) {
		try {
			Tracer.begin(this, "chunk decorator pass");
			Chunk achunk = world.getChunkFromChunkCoords(chunkX, chunkZ);
			decorator.paint(random, world, achunk, LCChunkData.forChunk(achunk));
		} catch (Throwable t) {
			LCLog.warn("Problem decoration chunk.", t);
		} finally {
			Tracer.end();
		}
		/*
		 * Ask the master generators to generate all known structures. It
		 * doesn't matter if the Block[] array is null at this stage.
		 */
		try {
			Tracer.begin(this, "scattered feature generator allocation");
			scatteredGenerator.func_151539_a(chunkProvider, world, chunkX, chunkZ, null);
		} catch (Throwable t) {
			LCLog.warn("Problem populating scattered features for chunk.", t);
		} finally {
			Tracer.end();
		}
		try {
			Tracer.begin(this, "ordered feature generator allocation");
			featureGenerator.func_151539_a(chunkProvider, world, chunkX, chunkX, null);
		} catch (Throwable t) {
			LCLog.warn("Problem populating features for chunk.", t);
		} finally {
			Tracer.end();
		}

		/*
		 * Ask the mater generators to actually place the structures it has
		 * declared in the world.
		 */
		boolean flag = false;
		try {
			Tracer.begin(this, "scattered feature population");
			scatteredGenerator.generateStructuresInChunk(world, random, chunkX, chunkZ);
		} catch (Throwable t) {
			LCLog.warn("Failed to generate scattered structures.", t);
		} finally {
			Tracer.end();
		}
		if (!flag)
			try {
				Tracer.begin(this, "ordered feature population");
				flag = featureGenerator.generateStructuresInChunk(world, random, chunkX, chunkZ);
			} catch (Throwable t) {
				LCLog.warn("Failed to generate structures.", t);
			} finally {
				Tracer.end();
			}
	}
}
