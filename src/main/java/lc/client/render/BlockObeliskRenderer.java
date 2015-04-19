package lc.client.render;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;
import lc.common.base.LCBlockRenderer;
import lc.common.configuration.xml.ComponentConfig;

/**
 * Obelisk block renderer implementation
 * 
 * @author AfterLifeLochie
 *
 */
public class BlockObeliskRenderer extends LCBlockRenderer {

	@Override
	public void configure(ComponentConfig c) {
		// TODO Auto-generated method stub

	}

	@Override
	public Class<? extends LCBlockRenderer> getParent() {
		return DefaultBlockRenderer.class;
	}

	@Override
	public boolean renderInventoryBlock(Block block, RenderBlocks renderer, int metadata) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean renderWorldBlock(Block block, RenderBlocks renderer, IBlockAccess world, int x, int y, int z) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean renderInventoryItemAs3d() {
		return false;
	}

}
