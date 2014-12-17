package lc.generation;

import java.util.Random;

import lc.api.init.Blocks;
import lc.blocks.BlockStargateBase;
import lc.common.base.generation.scattered.LCScatteredFeature;
import lc.common.base.generation.scattered.LCScatteredFeatureStart;
import lc.common.base.generation.structure.LCFeature;
import lc.common.base.generation.structure.LCFeatureStart;
import lc.core.LCRuntime;
import lc.generation.AbydosPyramid.AbydosPyramidFeature;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;

public class LanteaSurfaceStargate extends LCScatteredFeatureStart {

	/** Default constructor */
	public LanteaSurfaceStargate() {
	}

	public LanteaSurfaceStargate(World world, Random rand, int x, int y) {
		super(world, rand, x, y);

	}

	@Override
	protected void addComponents(World world, Random rng, int cx, int cz) {
		components.add(new SurfaceStargateFeature(rng, cx, cz));
	}

	public static class SurfaceStargateFeature extends LCScatteredFeature {

		/**
		 * @param rng
		 *            The random number generator
		 * @param x
		 *            The x-coordinate
		 * @param z
		 *            The z-coordinate
		 */
		public SurfaceStargateFeature(Random rng, int x, int z) {
			super(rng, x, 64, z, 42, 30, 42);
		}

		/**
		 * func_143012_a: saveToNBT(NBTTagCompound compound);
		 */
		@Override
		protected void func_143012_a(NBTTagCompound tag) {
			super.func_143012_a(tag);
		}

		/**
		 * func_143011_b: loadFromNBT(NBTTagCompound compound);
		 */
		@Override
		protected void func_143011_b(NBTTagCompound tag) {
			super.func_143011_b(tag);
		}

		@Override
		public boolean addComponentParts(World par1World, Random par2Random,
				StructureBoundingBox par3StructureBoundingBox) {
			fillWithBlocks(par1World, par3StructureBoundingBox, 0, -4, 0, scatteredFeatureSizeX - 1, 0,
					scatteredFeatureSizeZ - 1, net.minecraft.init.Blocks.sandstone,
					net.minecraft.init.Blocks.sandstone, false);

			Blocks modBlocks = LCRuntime.runtime.blocks();
			Block base = modBlocks.stargateBaseBlock.getBlock();
			Block ring = modBlocks.stargateRingBlock.getBlock();

			return true;
		}
	}
}