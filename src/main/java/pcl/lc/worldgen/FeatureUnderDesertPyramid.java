package pcl.lc.worldgen;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import pcl.lc.LanteaCraft.Blocks;

public class FeatureUnderDesertPyramid extends StructureComponent {

	StructureComponent base;

	public FeatureUnderDesertPyramid() {
	}

	public FeatureUnderDesertPyramid(StructureComponent base) {
		super(0);
		this.base = base;
		StructureBoundingBox baseBox = base.getBoundingBox();
		int cx = baseBox.getCenterX();
		int cz = baseBox.getCenterZ();
		int bottom = baseBox.minY - 7;
		boundingBox = new StructureBoundingBox(cx - 5, bottom, cz - 5, cx + 5, bottom + 7, cz + 8);
		coordBaseMode = 0;
	}

	@Override
	public boolean addComponentParts(World world, Random rand, StructureBoundingBox clip) {
		StructureBoundingBox box = getBoundingBox();
		Block air = (Block) Block.blockRegistry.getObject("air");
		Block glowstone = (Block) Block.blockRegistry.getObject("glowStone");
		Block sandstone = (Block) Block.blockRegistry.getObject("sandStone");
		int smoothSandstone = 2;
		Block wool = (Block) Block.blockRegistry.getObject("cloth");
		int orange = 1;
		Block stairs = (Block) Block.blockRegistry.getObject("stairsSandStone");
		int stairsWest = 0;
		Block ladder = (Block) Block.blockRegistry.getObject("ladder");
		int ladderSouth = 2;
		Block dhd = Blocks.stargateControllerBlock;
		int dhdNorth = 4;
		Block sgBase = Blocks.stargateBaseBlock;
		int sgBaseNorth = 0;
		Block sgRing = Blocks.stargateRingBlock;
		// Main chamber
		fillWithBlocks(world, clip, 0, 0, 0, 10, 7, 10, sandstone, air, false);
		// Stairwell
		fillWithBlocks(world, clip, 4, 0, 11, 13, 7, 13, sandstone, air, false);
		// Stairwell entrance
		fillWithAir(world, clip, 12, 7, 12, 12, 9, 12);
		// Stairwell exit
		fillWithAir(world, clip, 5, 1, 10, 5, 2, 11);
		// Stairs
		placeBlockAtCurrentPosition(world, sandstone, 0, 12, 4, 12, clip);
		for (int i = 0; i < 4; i++)
			placeBlockAtCurrentPosition(world, stairs, stairsWest, 8 + i, 1 + i, 12, clip);
		for (int i = 0; i < 3; i++)
			placeBlockAtCurrentPosition(world, ladder, ladderSouth, 12, 5 + i, 12, clip);
		// Wall decorations
		fillWithMetadataBlocks(world, clip, 0, 3, 0, 10, 3, 10, wool, orange, air, 0, true);
		fillWithMetadataBlocks(world, clip, 5, 3, 0, 5, 3, 10, glowstone, 0, air, 0, true);
		fillWithMetadataBlocks(world, clip, 3, 4, 10, 7, 4, 10, wool, orange, air, 0, true);
		fillWithMetadataBlocks(world, clip, 8, 4, 10, 8, 4, 10, glowstone, 0, air, 0, true);
		fillWithMetadataBlocks(world, clip, 2, 4, 10, 2, 4, 10, glowstone, 0, air, 0, true);
		// Floor decorations
		fillWithMetadataBlocks(world, clip, 3, 0, 4, 3, 0, 6, wool, orange, air, 0, true);
		fillWithMetadataBlocks(world, clip, 7, 0, 4, 7, 0, 6, wool, orange, air, 0, true);
		fillWithMetadataBlocks(world, clip, 4, 0, 3, 6, 0, 3, wool, orange, air, 0, true);
		fillWithMetadataBlocks(world, clip, 4, 0, 7, 6, 0, 7, wool, orange, air, 0, true);
		placeBlockAtCurrentPosition(world, wool, orange, 5, 0, 5, clip);
		// Door frame
		fillWithMetadataBlocks(world, clip, 4, 1, 10, 6, 3, 10, sandstone, smoothSandstone, air, 0, true);
		// Stargate
		for (int i = -2; i <= 2; i++)
			for (int j = 0; j <= 4; j++) {
				Block id;
				int data;
				if (i == 0 && j == 0) {
					id = sgBase;
					data = sgBaseNorth;
				} else if (i == -2 || i == 2 || j == 0 || j == 4) {
					id = sgRing;
					data = i + j + 1 & 1;
				} else {
					id = air;
					data = 0;
				}
				placeBlockAtCurrentPosition(world, id, data, 5 + i, 1 + j, 2, clip);
			}
		// Blocks.sgBaseBlock.checkForMerge(world, box.minX + 5, box.minY + 1,
		// box.minZ + 2);
		// Controller
		placeBlockAtCurrentPosition(world, dhd, dhdNorth, 5, 1, 7, clip);
		return true;
	}

	@Override
	protected void func_143012_a(NBTTagCompound nbttagcompound) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void func_143011_b(NBTTagCompound nbttagcompound) {
		// TODO Auto-generated method stub

	}

}
