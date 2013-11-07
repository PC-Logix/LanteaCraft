//------------------------------------------------------------------------------------------------
//
//   SG Craft - Stargate base block renderer
//
//------------------------------------------------------------------------------------------------

package gcewing.sg.render.blocks;

import gcewing.sg.blocks.BlockStargateBase;
import gcewing.sg.render.RotationOrientedBlockRenderer;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;

public class BlockStargateBaseRenderer extends RotationOrientedBlockRenderer {

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks rb) {
		BlockStargateBase baseBlock = (BlockStargateBase) block;
		if (rb.overrideBlockTexture != null || !baseBlock.isMerged(world, x, y, z))
			return super.renderWorldBlock(world, x, y, z, block, modelId, rb);
		else
			return false;
	}

}
