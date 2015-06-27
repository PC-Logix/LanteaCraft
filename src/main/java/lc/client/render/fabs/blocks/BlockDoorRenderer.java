package lc.client.render.fabs.blocks;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;
import lc.common.base.LCBlockRenderer;
import lc.common.configuration.xml.ComponentConfig;

/**
 * Door block renderer implementation
 * 
 * @author AfterLifeLochie
 *
 */
public class BlockDoorRenderer extends LCBlockRenderer {

	@Override
	public void configure(ComponentConfig c) {
		// TODO Auto-generated method stub

	}

	@Override
	public Class<? extends LCBlockRenderer> getParent() {
		return null;
	}

	@Override
	public boolean renderInventoryBlock(Block block, RenderBlocks renderer, int metadata) {
		renderDefaultItem(new ItemStack(block, 1, metadata));
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
