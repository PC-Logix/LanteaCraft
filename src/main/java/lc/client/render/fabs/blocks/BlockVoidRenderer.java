package lc.client.render.fabs.blocks;

import lc.api.rendering.IBlockRenderInfo;
import lc.common.base.LCBlock;
import lc.common.base.LCBlockRenderer;
import lc.common.configuration.xml.ComponentConfig;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;

/**
 * Voidable block renderer implementation.
 *
 * @author AfterLifeLochie
 *
 */
public abstract class BlockVoidRenderer extends LCBlockRenderer {

	@Override
	public void configure(ComponentConfig c) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean renderInventoryBlock(Block block, RenderBlocks renderer, int metadata) {
		return false;
	}

	@Override
	public boolean renderWorldBlock(Block block, RenderBlocks renderer, IBlockAccess world, int x, int y, int z) {
		if (!(block instanceof LCBlock))
			return false;
		LCBlock lcb = (LCBlock) block;
		IBlockRenderInfo info = lcb.renderInfoBlock();
		if (info == null)
			return false;
		return info.doProperty("noRender", world, world.getBlockMetadata(x, y, z), x, y, z, false);
	}
}
