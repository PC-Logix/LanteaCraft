//------------------------------------------------------------------------------------------------
//
//   SG Craft - Stargate ring block renderer
//
//------------------------------------------------------------------------------------------------

package gcewing.sg.render;

import gcewing.sg.blocks.SGRingBlock;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;

public class SGRingBlockRenderer extends BaseBlockRenderer {

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks rb) {
		SGRingBlock ringBlock = (SGRingBlock) block;
		if (rb.overrideBlockTexture != null || !ringBlock.isMerged(world, x, y, z))
			return super.renderWorldBlock(world, x, y, z, block, modelId, rb);
		else
			return false;
	}

}
