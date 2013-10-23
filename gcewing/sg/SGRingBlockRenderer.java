//------------------------------------------------------------------------------------------------
//
//   SG Craft - Stargate ring block renderer
//
//------------------------------------------------------------------------------------------------

package gcewing.sg;

import net.minecraft.block.*;
import net.minecraft.client.renderer.*;
import net.minecraft.world.*;

public class SGRingBlockRenderer extends BaseBlockRenderer {

	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block,
		int modelId, RenderBlocks rb)
	{
		SGRingBlock ringBlock = (SGRingBlock)block;
		if (rb.overrideBlockTexture != null || !ringBlock.isMerged(world, x, y, z))
			return super.renderWorldBlock(world, x, y, z, block, modelId, rb);
		else
			return false;
	}

}
