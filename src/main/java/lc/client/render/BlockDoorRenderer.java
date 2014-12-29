package lc.client.render;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;
import lc.common.base.LCBlockRenderer;

public class BlockDoorRenderer extends LCBlockRenderer {

	@Override
	public Class<? extends LCBlockRenderer> getParent() {
		return null;
	}

	@Override
	public boolean renderInventoryBlock(Block block, RenderBlocks renderer, int metadata) {
		return true;
	}

	@Override
	public boolean renderWorldBlock(Block block, RenderBlocks renderer, IBlockAccess world, int x, int y, int z) {
		return true;
	}

	@Override
	public boolean renderInventoryItemAs3d() {
		return false;
	}

}
