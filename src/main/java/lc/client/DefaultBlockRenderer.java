package lc.client;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;
import lc.common.base.LCBlock;
import lc.common.base.LCBlockRenderer;
import lc.common.util.math.Trans3;

public class DefaultBlockRenderer extends LCBlockRenderer {

	/** Map of ForgeDirection to rotations on axis 0 */
	private static int[] rotationMap = new int[] { 0, 0, 0, 2, 1, 3 };

	@Override
	public Class<? extends LCBlockRenderer> getParent() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean renderInventoryBlock(Block block, RenderBlocks renderer, int metadata) {
		if (!(block instanceof LCBlock))
			return false;
		LCBlock theBlock = (LCBlock) block;
		Trans3 trans = new Trans3(0.0, 0.0, 0.0);
		trans = preRenderInInventory(theBlock, metadata, renderer, trans);
		renderDefaultInventoryBlock(block, metadata, trans, renderer);
		boolean flag = postRenderInInventory(theBlock, metadata, renderer);
		return flag;
	}

	@Override
	public boolean renderWorldBlock(Block block, RenderBlocks renderer, IBlockAccess world, int x, int y, int z) {
		if (!(block instanceof LCBlock))
			return false;
		LCBlock theBlock = (LCBlock) block;
		Trans3 trans = new Trans3(x + 0.5, y + 0.5, z + 0.5);
		trans = preRenderInWorld(theBlock, world, renderer, trans, x, y, z);
		boolean flag = renderDefaultWorldBlock(world, x, y, z, block, trans, renderer);
		flag = postRenderInWorld(theBlock, world, renderer, flag, x, y, z);
		return flag;
	}

	@Override
	public boolean renderInventoryItemAs3d() {
		// TODO Auto-generated method stub
		return true;
	}

	private Trans3 preRenderInWorld(LCBlock block, IBlockAccess world, RenderBlocks renderer, Trans3 trans, int x,
			int y, int z) {
		if (block.canRotate()) {
			ForgeDirection rotation = block.getRotation(world, x, y, z);
			trans = trans.side(0).turn(rotationMap[rotation.ordinal()]);
		}
		return trans;
	}

	private boolean postRenderInWorld(LCBlock block, IBlockAccess world, RenderBlocks renderer, boolean flag, int x,
			int y, int z) {
		renderer.uvRotateBottom = renderer.uvRotateEast = renderer.uvRotateNorth = renderer.uvRotateSouth = renderer.uvRotateTop = renderer.uvRotateWest = 0;
		return flag;
	}

	private Trans3 preRenderInInventory(LCBlock theBlock, int metadata, RenderBlocks renderer, Trans3 trans) {
		return trans;
	}

	private boolean postRenderInInventory(LCBlock theBlock, int metadata, RenderBlocks renderer) {
		return true;
	}
}
