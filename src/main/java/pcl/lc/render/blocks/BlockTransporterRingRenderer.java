package pcl.lc.render.blocks;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;
import pcl.lc.blocks.BlockTransporterRing;
import pcl.lc.tileentity.TileEntityTransporterRing;

public class BlockTransporterRingRenderer extends GenericBlockRenderer {

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks rb) {
		BlockTransporterRing ringBlock = (BlockTransporterRing) block;
		TileEntityTransporterRing te = (TileEntityTransporterRing) world.getTileEntity(x, y, z);
		return super.renderWorldBlock(world, x, y, z, block, modelId, rb);

	}

}
