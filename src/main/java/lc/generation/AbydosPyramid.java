package lc.generation;

import java.util.Random;

import lc.common.base.generation.structure.LCFeatureComponent;
import lc.core.LCRuntime;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;

/**
 * Abydos pyramid scattered feature generator implementation.
 * 
 * @author AfterLifeLochie
 *
 */
public class AbydosPyramid extends LCFeatureComponent {

	/** Default constructor */
	public AbydosPyramid() {
	}

	/**
	 * @param rng
	 *            The random number generator
	 * @param x
	 *            The x-coordinate
	 * @param z
	 *            The z-coordinate
	 */
	public AbydosPyramid(Random rng, int x, int z) {
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
	public boolean addComponentParts(World par1World, Random par2Random, StructureBoundingBox par3StructureBoundingBox) {
		fillWithBlocks(par1World, par3StructureBoundingBox, 0, -4, 0, scatteredFeatureSizeX - 1, 0,
				scatteredFeatureSizeZ - 1, Blocks.sandstone, Blocks.sandstone, false);
		int i;

		lc.api.init.Blocks modBlocks = LCRuntime.runtime.blocks();

		for (i = 1; i <= 18; ++i) {
			fillWithMetadataBlocks(par1World, par3StructureBoundingBox, i, i, i, scatteredFeatureSizeX - 1 - i, i,
					scatteredFeatureSizeZ - 1 - i, modBlocks.lanteaDecorBlock.getBlock(), 4,
					modBlocks.lanteaDecorBlock.getBlock(), 4, false);
			fillWithBlocks(par1World, par3StructureBoundingBox, i + 1, i, i + 1, scatteredFeatureSizeX - 2 - i, i,
					scatteredFeatureSizeZ - 2 - i, Blocks.air, Blocks.air, false);
		}

		int j;

		for (i = 0; i < scatteredFeatureSizeX; ++i)
			for (j = 0; j < scatteredFeatureSizeZ; ++j) {
				byte b0 = -5;
				func_151554_b(par1World, Blocks.sandstone, 0, i, b0, j, par3StructureBoundingBox);
			}
		return true;
	}
}