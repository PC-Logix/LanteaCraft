//------------------------------------------------------------------------------------------------
//
//   SG Craft - Stargate ring block renderer
//
//------------------------------------------------------------------------------------------------

package gcewing.sg.render.blocks;

import gcewing.sg.blocks.BlockStargateRing;
import gcewing.sg.render.GenericBlockRenderer;
import gcewing.sg.tileentity.TileEntityStargateRing;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;

public class BlockStargateRingRenderer extends GenericBlockRenderer {

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks rb) {
		BlockStargateRing ringBlock = (BlockStargateRing) block;
		TileEntityStargateRing ringTE = (TileEntityStargateRing) world.getBlockTileEntity(x, y, z);

		if (ringTE != null && ringTE.getAsPart() != null && ringTE.getAsPart().isMerged())
			return false;
		else
			return super.renderWorldBlock(world, x, y, z, block, modelId, rb);
	}

}
