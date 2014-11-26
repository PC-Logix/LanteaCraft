package lc.client;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;
import lc.common.base.LCBlockRenderer;
import lc.common.util.game.BlockContainerProxy;
import lc.common.util.game.WorldProxy;
import lc.tiles.TileStargateBase;

public class BlockStargateBaseRenderer extends LCBlockRenderer {

	@Override
	public Class<? extends LCBlockRenderer> getParent() {
		return null;
	}

	@Override
	public boolean renderInventoryBlock(Block block, RenderBlocks renderer, int metadata) {
		return false;
	}

	@Override
	public boolean renderWorldBlock(Block block, RenderBlocks renderer, IBlockAccess world, int x, int y, int z) {
		TileStargateBase tile = (TileStargateBase) world.getTileEntity(x, y, z);
		if (tile.disguiseBlock != null) {
			WorldProxy con = new WorldProxy(world, tile.disguiseMetadata);
			RenderBlocks ren2 = new RenderBlocks(con);
			ren2.setRenderBoundsFromBlock(block);
			if (tile.disguiseMetadata != 0) {
				BlockContainerProxy cont = null;
				for (int i = 0; i < 6; i++) {
					ren2.setOverrideBlockTexture(tile.disguiseBlock.getIcon(i, tile.disguiseMetadata));
					cont = new BlockContainerProxy(tile.disguiseBlock, i);
					ren2.renderStandardBlock(cont, x, y, z);
				}
				cont = null;
			} else {
				ren2.renderStandardBlock(tile.disguiseBlock, x, y, z);
			}
			ren2.clearOverrideBlockTexture();
			ren2 = null;
			con = null;
		}
		return true;
	}

	@Override
	public boolean renderInventoryItemAs3d() {
		return true;
	}

}
