package lc.dimensions.abydos;

import java.util.List;
import java.util.Random;

import lc.common.base.generation.structure.LCFeatureGenerator;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.NoiseGeneratorOctaves;
import net.minecraft.world.gen.NoiseGeneratorPerlin;

/**
 * Abydos chunk provider implementation
 *
 * @author AfterLifeLochie
 *
 */
public class AbydosChunkProvider implements IChunkProvider {

	private Random rng;
	private NoiseGeneratorOctaves ng1, ng2, ng3, ng6;
	private NoiseGeneratorPerlin ngp4;
	private BiomeGenBase biomeForGeneration;
	/**
	 * FIXME: We should be relying on the LCMasterWorldGen instead of
	 * referencing the feature generator directly. This will result in an
	 * inconsistent state.
	 */
	private LCFeatureGenerator structureController;
	private World worldObj;

	private final float[] parabolicField;
	private final double[] field_147434_q;
	private double[] stoneNoise = new double[256];
	private double[] d_ng3, d_ng1, d_ng2, d_ng6;

	/**
	 * Default constructor
	 *
	 * @param world
	 *            The world to write to
	 * @param biome
	 *            The default biome
	 */
	public AbydosChunkProvider(World world, BiomeGenBase biome) {
		worldObj = world;
		rng = new Random(world.getSeed());
		ng1 = new NoiseGeneratorOctaves(rng, 16);
		ng2 = new NoiseGeneratorOctaves(rng, 16);
		ng3 = new NoiseGeneratorOctaves(rng, 8);
		ngp4 = new NoiseGeneratorPerlin(rng, 4);
		ng6 = new NoiseGeneratorOctaves(rng, 16);
		field_147434_q = new double[825];
		parabolicField = new float[25];
		biomeForGeneration = biome;
		structureController = new LCFeatureGenerator();

		for (int j = -2; j <= 2; ++j)
			for (int k = -2; k <= 2; ++k) {
				float f = 10.0F / MathHelper.sqrt_float(j * j + k * k + 0.2F);
				parabolicField[j + 2 + (k + 2) * 5] = f;
			}
	}

	private void generateChunk(int cx, int cz, Block[] map) {
		byte heightWater = 42;
		initializeNoise(cx * 4, 0, cz * 4);
		for (int k = 0; k < 4; ++k) {
			int l = k * 5;
			int i1 = (k + 1) * 5;
			for (int j1 = 0; j1 < 4; ++j1) {
				int k1 = (l + j1) * 33;
				int l1 = (l + j1 + 1) * 33;
				int i2 = (i1 + j1) * 33;
				int j2 = (i1 + j1 + 1) * 33;
				for (int k2 = 0; k2 < 32; ++k2) {
					double d0 = 0.125D;
					double d1 = field_147434_q[k1 + k2];
					double d2 = field_147434_q[l1 + k2];
					double d3 = field_147434_q[i2 + k2];
					double d4 = field_147434_q[j2 + k2];
					double d5 = (field_147434_q[k1 + k2 + 1] - d1) * d0;
					double d6 = (field_147434_q[l1 + k2 + 1] - d2) * d0;
					double d7 = (field_147434_q[i2 + k2 + 1] - d3) * d0;
					double d8 = (field_147434_q[j2 + k2 + 1] - d4) * d0;

					for (int l2 = 0; l2 < 8; ++l2) {
						double d9 = 0.25D;
						double d10 = d1;
						double d11 = d2;
						double d12 = (d3 - d1) * d9;
						double d13 = (d4 - d2) * d9;

						for (int i3 = 0; i3 < 4; ++i3) {
							int j3 = i3 + k * 4 << 12 | 0 + j1 * 4 << 8 | k2 * 8 + l2;
							short short1 = 256;
							j3 -= short1;
							double d14 = 0.25D;
							double d16 = (d11 - d10) * d14;
							double d15 = d10 - d16;

							for (int k3 = 0; k3 < 4; ++k3)
								if ((d15 += d16) > 0.0D)
									map[j3 += short1] = Blocks.stone;
								else if (k2 * 8 + l2 < heightWater)
									map[j3 += short1] = Blocks.water;
								else
									map[j3 += short1] = null;

							d10 += d12;
							d11 += d13;
						}

						d1 += d5;
						d2 += d6;
						d3 += d7;
						d4 += d8;
					}
				}
			}
		}
	}

	private void replaceBlocksForBiome(int x, int z, Block[] block, byte[] abyte, BiomeGenBase bgb) {
		double d0 = 0.03125D;
		stoneNoise = ngp4.func_151599_a(stoneNoise, x * 16, z * 16, 16, 16, d0 * 2.0D, d0 * 2.0D, 1.0D);
		for (int k = 0; k < 16; ++k)
			for (int l = 0; l < 16; ++l)
				bgb.genTerrainBlocks(worldObj, rng, block, abyte, x * 16 + k, z * 16 + l, stoneNoise[l + k * 16]);
	}

	/**
	 * loads or generates the chunk at the chunk location specified
	 */
	@Override
	public Chunk loadChunk(int par1, int par2) {
		return provideChunk(par1, par2);
	}

	/**
	 * Will return back a chunk, if it doesn't exist and its not a MP client it
	 * will generates all the blocks for the specified chunk from the map seed
	 * and chunk seed
	 */
	@Override
	public Chunk provideChunk(int chunkX, int chunkZ) {
		rng.setSeed(chunkX * 341873128712L + chunkZ * 132897987541L);
		Block[] ablock = new Block[65536];
		byte[] abyte = new byte[65536];
		generateChunk(chunkX, chunkZ, ablock);
		replaceBlocksForBiome(chunkX, chunkZ, ablock, abyte, biomeForGeneration);

		structureController.func_151539_a(this, worldObj, chunkX, chunkZ, null);

		Chunk chunk = new Chunk(worldObj, ablock, abyte, chunkX, chunkZ);
		byte[] abyte1 = chunk.getBiomeArray();

		for (int k = 0; k < abyte1.length; ++k)
			abyte1[k] = (byte) biomeForGeneration.biomeID;

		chunk.generateSkylightMap();
		return chunk;
	}

	private void initializeNoise(int x, int y, int z) {
		d_ng6 = ng6.generateNoiseOctaves(d_ng6, x, z, 5, 5, 200.0D, 200.0D, 0.5D);
		d_ng3 = ng3.generateNoiseOctaves(d_ng3, x, y, z, 5, 33, 5, 8.555150000000001D, 4.277575000000001D,
				8.555150000000001D);
		d_ng1 = ng1.generateNoiseOctaves(d_ng1, x, y, z, 5, 33, 5, 684.412D, 684.412D, 684.412D);
		d_ng2 = ng2.generateNoiseOctaves(d_ng2, x, y, z, 5, 33, 5, 684.412D, 684.412D, 684.412D);
		int l = 0;
		int i1 = 0;
		for (int j1 = 0; j1 < 5; ++j1)
			for (int k1 = 0; k1 < 5; ++k1) {
				float f = 0.0F, f1 = 0.0F, f2 = 0.0F;
				byte b0 = 2;
				BiomeGenBase biomegenbase = biomeForGeneration;

				for (int l1 = -b0; l1 <= b0; ++l1)
					for (int i2 = -b0; i2 <= b0; ++i2) {
						BiomeGenBase biomegenbase1 = biomeForGeneration;
						float f3 = biomegenbase1.rootHeight;
						float f4 = biomegenbase1.heightVariation;
						float f5 = parabolicField[l1 + 2 + (i2 + 2) * 5] / (f3 + 2.0F);
						if (biomegenbase1.rootHeight > biomegenbase.rootHeight)
							f5 /= 2.0F;
						f += f4 * f5;
						f1 += f3 * f5;
						f2 += f5;
					}

				f /= f2;
				f1 /= f2;
				f = f * 0.9F + 0.1F;
				f1 = (f1 * 4.0F - 1.0F) / 8.0F;
				double d12 = d_ng6[i1] / 8000.0D;

				if (d12 < 0.0D)
					d12 = -d12 * 0.3D;

				d12 = d12 * 3.0D - 2.0D;

				if (d12 < 0.0D) {
					d12 /= 2.0D;
					if (d12 < -1.0D)
						d12 = -1.0D;
					d12 /= 1.4D;
					d12 /= 2.0D;
				} else {
					if (d12 > 1.0D)
						d12 = 1.0D;
					d12 /= 8.0D;
				}

				++i1;
				double d13 = f1, d14 = f;
				d13 += d12 * 0.2D;
				d13 = d13 * 8.5D / 8.0D;
				double d5 = 8.5D + d13 * 4.0D;

				for (int j2 = 0; j2 < 33; ++j2) {
					double d6 = (j2 - d5) * 12.0D * 128.0D / 256.0D / d14;

					if (d6 < 0.0D)
						d6 *= 4.0D;

					double d7 = d_ng1[l] / 512.0D, d8 = d_ng2[l] / 512.0D;
					double d9 = (d_ng3[l] / 10.0D + 1.0D) / 2.0D;
					double d10 = MathHelper.denormalizeClamp(d7, d8, d9) - d6;

					if (j2 > 29) {
						double d11 = (j2 - 29) / 3.0F;
						d10 = d10 * (1.0D - d11) + -10.0D * d11;
					}

					field_147434_q[l] = d10;
					++l;
				}
			}
	}

	/**
	 * Checks to see if a chunk exists at x, y
	 */
	@Override
	public boolean chunkExists(int x, int y) {
		return true;
	}

	/**
	 * Populates chunk with ores etc etc
	 */
	@Override
	public void populate(IChunkProvider provider, int cx, int cz) {
		BlockFalling.fallInstantly = true;
		int x = cx * 16;
		int z = cz * 16;
		BiomeGenBase biomegenbase = worldObj.getBiomeGenForCoords(x + 16, z + 16);
		rng.setSeed(worldObj.getSeed());
		long i1 = rng.nextLong() / 2L * 2L + 1L;
		long j1 = rng.nextLong() / 2L * 2L + 1L;
		rng.setSeed(cx * i1 + cz * j1 ^ worldObj.getSeed());
		biomegenbase.decorate(worldObj, rng, x, z);
		structureController.generateStructuresInChunk(worldObj, rng, cx, cz);
		BlockFalling.fallInstantly = false;
	}

	/**
	 * Two modes of operation: if passed true, save all Chunks in one go. If
	 * passed false, save up to two chunks. Return true if all chunks have been
	 * saved.
	 */
	@Override
	public boolean saveChunks(boolean saveAll, IProgressUpdate p) {
		return true;
	}

	/**
	 * Save extra data not associated with any Chunk. Not saved during autosave,
	 * only during world unload. Currently unimplemented.
	 */
	@Override
	public void saveExtraData() {
	}

	/**
	 * Unloads chunks that are marked to be unloaded. This is not guaranteed to
	 * unload every such chunk.
	 */
	@Override
	public boolean unloadQueuedChunks() {
		return false;
	}

	/**
	 * Returns if the IChunkProvider supports saving.
	 */
	@Override
	public boolean canSave() {
		return true;
	}

	/**
	 * Converts the instance data to a readable string.
	 */
	@Override
	public String makeString() {
		return "AbydosDimensionSource";
	}

	/**
	 * Returns a list of creatures of the specified type that can spawn at the
	 * given location.
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public List getPossibleCreatures(EnumCreatureType type, int x, int y, int z) {
		BiomeGenBase biomegenbase = worldObj.getBiomeGenForCoords(x, z);
		return biomegenbase.getSpawnableList(type);
	}

	@Override
	public ChunkPosition func_147416_a(World world, String strucClazz, int x, int y, int z) {
		return null;
	}

	@Override
	public int getLoadedChunkCount() {
		return 0;
	}

	@Override
	public void recreateStructures(int par1, int par2) {
		structureController.func_151539_a(this, worldObj, par1, par2, null);
	}
}