//------------------------------------------------------------------------------------------------
//
//   SG Craft - Stargate portal block
//
//------------------------------------------------------------------------------------------------

package gcewing.sg.blocks;

import gcewing.sg.tileentity.SGBaseTE;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

public class SGPortalBlock extends Block {

	public SGPortalBlock(int id) {
		super(id, Material.rock);
		setBlockBounds(0, 0, 0, 0, 0, 0);
	}

	@Override
	public int getRenderType() {
		return -1;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int par2, int par3, int par4) {
		return null;
	}

	@Override
	public int quantityDropped(Random par1Random) {
		return 0;
	}

	// @Override
	// public void onEntityCollidedWithBlock(World world, int x, int y, int z,
	// Entity entity) {
	// if (!world.isRemote) {
	// //System.out.printf("SGPortalBlock.onEntityCollidedWithBlock (%d,%d,%d) in %s\n",
	// // x, y, z, world);
	// SGBaseTE te = getStargateTE(world, x, y, z);
	// if (te != null)
	// te.entityInPortal(entity);
	// }
	// }

	SGBaseTE getStargateTE(World world, int x, int y, int z) {
		for (int i = -1; i <= 1; i++)
			for (int j = -3; j <= -1; j++)
				for (int k = -1; k <= 1; k++) {
					TileEntity te = world.getBlockTileEntity(x + i, y + j, z + k);
					if (te instanceof SGBaseTE)
						return (SGBaseTE) te;
				}
		return null;
	}

}
