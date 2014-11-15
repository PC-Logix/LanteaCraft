package lc.client;

import lc.api.defs.IDefinitionReference;
import lc.api.rendering.IBlockRenderInfo;
import lc.api.rendering.IRenderInfo;
import lc.common.base.LCBlock;
import lc.common.base.LCBlockRenderer;
import lc.common.impl.registry.DefinitionReference;
import lc.common.util.math.Trans3;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * Default block renderer implementation
 *
 * @author AfterLifeLochie
 *
 */
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
		IBlockRenderInfo info = null;
		if (theBlock instanceof IRenderInfo)
			info = ((IRenderInfo) theBlock).block();
		Trans3 trans = new Trans3(0.0, 0.0, 0.0);
		trans = preRenderInInventory(theBlock, info, metadata, renderer, trans);
		if (info == null || info.doInventoryRender(metadata))
			renderDefaultInventoryBlock(block, metadata, trans, renderer);
		boolean flag = postRenderInInventory(theBlock, info, metadata, renderer);
		return flag;
	}

	@Override
	public boolean renderWorldBlock(Block block, RenderBlocks renderer, IBlockAccess world, int x, int y, int z) {
		if (!(block instanceof LCBlock))
			return false;
		LCBlock theBlock = (LCBlock) block;
		IBlockRenderInfo info = null;
		if (theBlock instanceof IRenderInfo)
			info = ((IRenderInfo) theBlock).block();
		Trans3 trans = new Trans3(x + 0.5, y + 0.5, z + 0.5);
		trans = preRenderInWorld(theBlock, info, world, renderer, trans, x, y, z);
		boolean flag = true;
		if (info == null || info.doWorldRender(world, world.getBlockMetadata(x, y, z), x, y, z))
			flag = renderDefaultWorldBlock(world, x, y, z, block, trans, renderer);
		flag = postRenderInWorld(theBlock, info, world, renderer, flag, x, y, z);
		return flag;
	}

	@Override
	public boolean renderInventoryItemAs3d() {
		// TODO Auto-generated method stub
		return true;
	}

	private Trans3 preRenderInWorld(LCBlock block, IBlockRenderInfo info, IBlockAccess world, RenderBlocks renderer,
			Trans3 trans, int x, int y, int z) {
		if (block.canRotate()) {
			ForgeDirection rotation = block.getRotation(world, x, y, z);
			trans = trans.side(0).turn(rotationMap[rotation.ordinal()]);
		}
		return trans;
	}

	private boolean postRenderInWorld(LCBlock block, IBlockRenderInfo info, IBlockAccess world, RenderBlocks renderer,
			boolean flag, int x, int y, int z) {
		renderer.uvRotateBottom = renderer.uvRotateEast = renderer.uvRotateNorth = renderer.uvRotateSouth = renderer.uvRotateTop = renderer.uvRotateWest = 0;
		return flag;
	}

	private Trans3 preRenderInInventory(LCBlock theBlock, IBlockRenderInfo info, int metadata, RenderBlocks renderer,
			Trans3 trans) {
		return trans;
	}

	private boolean postRenderInInventory(LCBlock theBlock, IBlockRenderInfo info, int metadata, RenderBlocks renderer) {
		return true;
	}

	@Override
	public IDefinitionReference ref() {
		return new DefinitionReference(this);
	}
}
