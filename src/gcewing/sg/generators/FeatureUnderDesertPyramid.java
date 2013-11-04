//------------------------------------------------------------------------------------------------
//
//   SG Craft - Generate stargate under desert pyramid
//
//------------------------------------------------------------------------------------------------

package gcewing.sg.generators;

import gcewing.sg.SGCraft.Blocks;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;

public class FeatureUnderDesertPyramid extends StructureComponent {

	StructureComponent base;

	// added cause forge went WAT?
	public FeatureUnderDesertPyramid() {
	}

	public FeatureUnderDesertPyramid(StructureComponent base) {
		super(0);
		// System.out.printf("SGCraft: FeatureUnderDesertPyramid\n");
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
		int sandstone = Block.sandStone.blockID;
		int smoothSandstone = 2;
		int wool = Block.cloth.blockID;
		int orange = 1;
		int stairs = Block.stairsSandStone.blockID;
		int stairsWest = 0;
		int ladder = Block.ladder.blockID;
		int ladderSouth = 2;
		int dhd = Blocks.sgControllerBlock.blockID;
		int dhdNorth = 0;
		int sgBase = Blocks.sgBaseBlock.blockID;
		int sgBaseNorth = 0;
		int sgRing = Blocks.sgRingBlock.blockID;
		// System.out.printf("SGCraft: FeatureUnderDesertPyramid.addComponentParts: "
		// + "Filling (%d,%d,%d)-(%d,%d,%d)\n", box.minX, box.minY, box.minZ,
		// box.maxX, box.maxY, box.maxZ);
		// Main chamber
		fillWithBlocks(world, clip, 0, 0, 0, 10, 7, 10, sandstone, 0, false);
		// Stairwell
		fillWithBlocks(world, clip, 4, 0, 11, 13, 7, 13, sandstone, 0, false);
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
		fillWithMetadataBlocks(world, clip, 0, 3, 0, 10, 3, 10, wool, orange, 0, 0, true);
		fillWithMetadataBlocks(world, clip, 3, 4, 10, 7, 4, 10, wool, orange, 0, 0, true);
		// Floor decorations
		fillWithMetadataBlocks(world, clip, 3, 0, 4, 3, 0, 6, wool, orange, 0, 0, true);
		fillWithMetadataBlocks(world, clip, 7, 0, 4, 7, 0, 6, wool, orange, 0, 0, true);
		fillWithMetadataBlocks(world, clip, 4, 0, 3, 6, 0, 3, wool, orange, 0, 0, true);
		fillWithMetadataBlocks(world, clip, 4, 0, 7, 6, 0, 7, wool, orange, 0, 0, true);
		placeBlockAtCurrentPosition(world, wool, orange, 5, 0, 5, clip);
		// Door frame
		fillWithMetadataBlocks(world, clip, 4, 1, 10, 6, 3, 10, sandstone, smoothSandstone, 0, 0, true);
		// Stargate
		for (int i = -2; i <= 2; i++)
			for (int j = 0; j <= 4; j++) {
				int id, data;
				if (i == 0 && j == 0) {
					id = sgBase;
					data = sgBaseNorth;
				} else if (i == -2 || i == 2 || j == 0 || j == 4) {
					id = sgRing;
					data = i + j + 1 & 1;
				} else {
					id = 0;
					data = 0;
				}
				placeBlockAtCurrentPosition(world, id, data, 5 + i, 1 + j, 2, clip);
			}
		Blocks.sgBaseBlock.checkForMerge(world, box.minX + 5, box.minY + 1, box.minZ + 2);
		// Controller
		placeBlockAtCurrentPosition(world, dhd, dhdNorth, 5, 1, 7, clip);
		// SGCraft.sgControllerBlock.checkForLink(world, box.minX + 5, box.minY
		// + 1, box.minZ + 7);
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
