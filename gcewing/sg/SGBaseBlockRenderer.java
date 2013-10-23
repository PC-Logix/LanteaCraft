//------------------------------------------------------------------------------------------------
//
//   SG Craft - Stargate base block renderer
//
//------------------------------------------------------------------------------------------------

package gcewing.sg;

import net.minecraft.block.*;
import net.minecraft.client.renderer.*;
import net.minecraft.world.*;

import net.minecraftforge.common.*;

public class SGBaseBlockRenderer extends BaseOrientedCtrBlkRenderer {

	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block,
		int modelId, RenderBlocks rb)
	{
		SGBaseBlock baseBlock = (SGBaseBlock)block;
		if (rb.overrideBlockTexture != null || !baseBlock.isMerged(world, x, y, z))
			return super.renderWorldBlock(world, x, y, z, block, modelId, rb);
		else
			return false;
	}

}
