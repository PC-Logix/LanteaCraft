package lc.client.render.fabs;

import lc.api.defs.IDefinitionReference;
import lc.api.rendering.IBlockRenderInfo;
import lc.api.rendering.IBlockSkinnable;
import lc.api.rendering.IRenderInfo;
import lc.common.base.LCBlock;
import lc.common.base.LCBlockRenderer;
import lc.common.base.LCTile;
import lc.common.configuration.xml.ComponentConfig;
import lc.common.impl.registry.DefinitionReference;
import lc.common.util.game.BlockContainerProxy;
import lc.common.util.game.WorldProxy;
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
	protected static int[] rotationMap = new int[] { 0, 0, 0, 2, 1, 3 };

	@Override
	public void configure(ComponentConfig c) {
		// TODO Auto-generated method stub

	}

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
			info = ((IRenderInfo) theBlock).renderInfoBlock();
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
			info = ((IRenderInfo) theBlock).renderInfoBlock();
		Trans3 trans = new Trans3(x, y, z);
		trans = preRenderInWorld(theBlock, info, world, renderer, trans, x, y, z);
		boolean flag = true;
		if (info == null || info.doWorldRender(world, world.getBlockMetadata(x, y, z), x, y, z)) {
			LCTile tile = (LCTile) world.getTileEntity(x, y, z);
			if (tile != null && tile instanceof IBlockSkinnable && ((IBlockSkinnable) tile).getSkinBlock() != null) {
				IBlockSkinnable skin = (IBlockSkinnable) tile;
				Block skinBlock = skin.getSkinBlock();
				int skinBlockMetadata = skin.getSkinBlockMetadata();
				RenderBlocks proxyRender = new RenderBlocks(new WorldProxy(world, skinBlockMetadata));
				proxyRender.setRenderBoundsFromBlock(block);
				if (skinBlockMetadata != 0) {
					BlockContainerProxy proxyContainer = null;
					for (int i = 0; i < 6; i++) {
						proxyRender.setOverrideBlockTexture(skinBlock.getIcon(i, skinBlockMetadata));
						proxyContainer = new BlockContainerProxy(skinBlock, i);
						flag = proxyRender.renderStandardBlock(proxyContainer, x, y, z);
					}
				} else
					flag = proxyRender.renderStandardBlock(skinBlock, x, y, z);
				proxyRender.clearOverrideBlockTexture();
			} else if (info == null
					|| info.doProperty("noRender", world, world.getBlockMetadata(x, y, z), x, y, z, true))
				flag = renderDefaultWorldBlock(world, x, y, z, block, trans, renderer);
		}
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
			trans = trans.side(0).translate(0.5d, 0.5d, 0.5d);
			trans = trans.turn(rotationMap[rotation.ordinal()]);
			trans = trans.translate(-0.5d, -0.5d, -0.5d);
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
