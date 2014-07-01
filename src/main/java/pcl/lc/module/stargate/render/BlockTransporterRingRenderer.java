package pcl.lc.module.stargate.render;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;
import pcl.lc.base.GenericBlockRenderer;
import pcl.lc.module.stargate.block.BlockTransporterRing;
import pcl.lc.module.stargate.tile.TileTransporterRing;

public class BlockTransporterRingRenderer extends GenericBlockRenderer {

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks rb) {
		BlockTransporterRing ringBlock = (BlockTransporterRing) block;
		TileTransporterRing te = (TileTransporterRing) world.getTileEntity(x, y, z);
		return super.renderWorldBlock(world, x, y, z, block, modelId, rb);

	}

}
