package pcl.lc.module.core.render;

import pcl.lc.base.GenericBlockRenderer;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;

public class BlockVoidRenderer extends GenericBlockRenderer {

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks rb) {
		return false;
	}

}
