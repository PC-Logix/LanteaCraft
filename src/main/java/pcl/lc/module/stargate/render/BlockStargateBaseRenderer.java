package pcl.lc.module.stargate.render;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;
import pcl.lc.module.core.render.BlockRotationOrientedRenderer;
import pcl.lc.module.stargate.block.BlockStargateBase;

public class BlockStargateBaseRenderer extends BlockRotationOrientedRenderer {

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks rb) {
		BlockStargateBase baseBlock = (BlockStargateBase) block;
		if (rb.overrideBlockTexture != null || !baseBlock.isMerged(world, x, y, z))
			return super.renderWorldBlock(world, x, y, z, block, modelId, rb);
		else
			return false;
	}

}
