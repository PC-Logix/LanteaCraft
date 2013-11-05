//------------------------------------------------------------------------------------------------
//
//   SG Craft - Stargate ring block renderer
//
//------------------------------------------------------------------------------------------------

package gcewing.sg.render.blocks;

import gcewing.sg.blocks.BlockStargateRing;
import gcewing.sg.render.GenericBlockRenderer;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;

public class BlockStargateRingRenderer extends GenericBlockRenderer {

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks rb) {
		BlockStargateRing ringBlock = (BlockStargateRing) block;
		if (rb.overrideBlockTexture != null || !ringBlock.isMerged(world, x, y, z))
			return super.renderWorldBlock(world, x, y, z, block, modelId, rb);
		else
			return false;
	}

}
