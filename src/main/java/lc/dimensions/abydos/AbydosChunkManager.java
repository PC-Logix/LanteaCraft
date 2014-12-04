package lc.dimensions.abydos;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import net.minecraft.world.ChunkPosition;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManager;

/**
 * Abydos chunk manager implementation.
 *
 * @author AfterLifeLochie
 *
 */
public class AbydosChunkManager extends WorldChunkManager {

	private BiomeGenBase biomeGenerator;
	private float rainfall;

	/**
	 * Default constructor
	 *
	 * @param biomeGenerator
	 *            The biome generator
	 * @param rainfall
	 *            The quantity of rainfall
	 */
	public AbydosChunkManager(BiomeGenBase biomeGenerator, float rainfall) {
		this.biomeGenerator = biomeGenerator;
		this.rainfall = rainfall;
	}

	/**
	 * Returns the BiomeGenBase related to the x, z position on the world.
	 */
	@Override
	public BiomeGenBase getBiomeGenAt(int par1, int par2) {
		return biomeGenerator;
	}

	/**
	 * Returns an array of biomes for the location input.
	 */
	@Override
	public BiomeGenBase[] getBiomesForGeneration(BiomeGenBase[] map, int par2, int par3, int par4, int par5) {
		if (map == null || map.length < par4 * par5)
			map = new BiomeGenBase[par4 * par5];

		Arrays.fill(map, 0, par4 * par5, biomeGenerator);
		return map;
	}

	/**
	 * Returns a list of rainfall values for the specified blocks.
	 */
	@Override
	public float[] getRainfall(float[] map, int x, int y, int width, int length) {
		if (map == null || map.length < width * length)
			map = new float[width * length];
		Arrays.fill(map, 0, width * length, rainfall);
		return map;
	}

	/**
	 * Returns biomes to use for the blocks and loads the other data like
	 * temperature and humidity onto the WorldChunkManager
	 */
	@Override
	public BiomeGenBase[] loadBlockGeneratorData(BiomeGenBase[] map, int x, int y, int width, int depth) {
		if (map == null || map.length < width * depth)
			map = new BiomeGenBase[width * depth];
		Arrays.fill(map, 0, width * depth, biomeGenerator);
		return map;
	}

	/**
	 * Return a list of biomes for the specified blocks. Args: listToReuse, x,
	 * y, width, length, cacheFlag (if false, don't check biomeCache to avoid
	 * infinite loop in BiomeCacheBlock)
	 */
	@Override
	public BiomeGenBase[] getBiomeGenAt(BiomeGenBase[] par1ArrayOfBiomeGenBase, int par2, int par3, int par4, int par5,
			boolean par6) {
		return loadBlockGeneratorData(par1ArrayOfBiomeGenBase, par2, par3, par4, par5);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public ChunkPosition findBiomePosition(int p_150795_1_, int p_150795_2_, int p_150795_3_, List p_150795_4_,
			Random p_150795_5_) {
		return p_150795_4_.contains(biomeGenerator) ? new ChunkPosition(p_150795_1_ - p_150795_3_
				+ p_150795_5_.nextInt(p_150795_3_ * 2 + 1), 0, p_150795_2_ - p_150795_3_
				+ p_150795_5_.nextInt(p_150795_3_ * 2 + 1)) : null;
	}

	/**
	 * checks given Chunk's Biomes against List of allowed ones
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public boolean areBiomesViable(int par1, int par2, int par3, List par4List) {
		return par4List.contains(biomeGenerator);
	}
}
