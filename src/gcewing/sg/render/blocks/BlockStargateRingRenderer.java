package gcewing.sg.render.blocks;

import gcewing.sg.blocks.BlockStargateRing;
import gcewing.sg.multiblock.StargateMultiblock;
import gcewing.sg.multiblock.StargatePart;
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

		StargatePart partOf = ringTE.getAsPart();
		if (partOf == null)
			return super.renderWorldBlock(world, x, y, z, block, modelId, rb);

		StargateMultiblock structureOf = (StargateMultiblock) partOf.findHostMultiblock(false);
		if (structureOf == null)
			return super.renderWorldBlock(world, x, y, z, block, modelId, rb);

		if (!structureOf.isValid())
			return super.renderWorldBlock(world, x, y, z, block, modelId, rb);

		return false;

	}

}
