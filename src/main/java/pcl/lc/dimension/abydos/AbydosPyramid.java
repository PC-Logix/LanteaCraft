package pcl.lc.dimension.abydos;

import java.util.Random;

import pcl.lc.dimension.MapGenFeatureStructureComponent;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;

public class AbydosPyramid extends MapGenFeatureStructureComponent {
	private boolean[] field_74940_h = new boolean[4];
	/** List of items to generate in chests of Temples. */
	public static final WeightedRandomChestContent[] itemsToGenerateInTemple = new WeightedRandomChestContent[] {
			new WeightedRandomChestContent(Items.diamond, 0, 1, 3, 3),
			new WeightedRandomChestContent(Items.iron_ingot, 0, 1, 5, 10),
			new WeightedRandomChestContent(Items.gold_ingot, 0, 2, 7, 15),
			new WeightedRandomChestContent(Items.emerald, 0, 1, 3, 2),
			new WeightedRandomChestContent(Items.bone, 0, 4, 6, 20),
			new WeightedRandomChestContent(Items.rotten_flesh, 0, 3, 7, 16),
			new WeightedRandomChestContent(Items.saddle, 0, 1, 1, 3),
			new WeightedRandomChestContent(Items.iron_horse_armor, 0, 1, 1, 1),
			new WeightedRandomChestContent(Items.golden_horse_armor, 0, 1, 1, 1),
			new WeightedRandomChestContent(Items.diamond_horse_armor, 0, 1, 1, 1) };

	public AbydosPyramid() {
	}

	public AbydosPyramid(Random par1Random, int par2, int par3) {
		super(par1Random, par2, 64, par3, 42, 30, 42);
	}

	protected void func_143012_a(NBTTagCompound par1NBTTagCompound) {
		super.func_143012_a(par1NBTTagCompound);
		par1NBTTagCompound.setBoolean("hasPlacedChest0", this.field_74940_h[0]);
		par1NBTTagCompound.setBoolean("hasPlacedChest1", this.field_74940_h[1]);
		par1NBTTagCompound.setBoolean("hasPlacedChest2", this.field_74940_h[2]);
		par1NBTTagCompound.setBoolean("hasPlacedChest3", this.field_74940_h[3]);
	}

	protected void func_143011_b(NBTTagCompound par1NBTTagCompound) {
		super.func_143011_b(par1NBTTagCompound);
		this.field_74940_h[0] = par1NBTTagCompound.getBoolean("hasPlacedChest0");
		this.field_74940_h[1] = par1NBTTagCompound.getBoolean("hasPlacedChest1");
		this.field_74940_h[2] = par1NBTTagCompound.getBoolean("hasPlacedChest2");
		this.field_74940_h[3] = par1NBTTagCompound.getBoolean("hasPlacedChest3");
	}

	public boolean addComponentParts(World par1World, Random par2Random, StructureBoundingBox par3StructureBoundingBox) {
		this.fillWithBlocks(par1World, par3StructureBoundingBox, 0, -4, 0, this.scatteredFeatureSizeX - 1, 0,
				this.scatteredFeatureSizeZ - 1, Blocks.sandstone, Blocks.sandstone, false);
		int i;

		for (i = 1; i <= 18; ++i) {
			this.fillWithBlocks(par1World, par3StructureBoundingBox, i, i, i, this.scatteredFeatureSizeX - 1 - i, i,
					this.scatteredFeatureSizeZ - 1 - i, Blocks.sandstone, Blocks.sandstone, false);
			this.fillWithBlocks(par1World, par3StructureBoundingBox, i + 1, i, i + 1, this.scatteredFeatureSizeX - 2
					- i, i, this.scatteredFeatureSizeZ - 2 - i, Blocks.air, Blocks.air, false);
		}

		int j;

		for (i = 0; i < this.scatteredFeatureSizeX; ++i) {
			for (j = 0; j < this.scatteredFeatureSizeZ; ++j) {
				byte b0 = -5;
				this.func_151554_b(par1World, Blocks.sandstone, 0, i, b0, j, par3StructureBoundingBox);
			}
		}
		return true;
	}
}