package lc.common.base;

import lc.api.defs.ILanteaCraftRenderer;
import lc.common.impl.DefinitionRegistry;
import lc.common.impl.DefinitionRegistry.RendererType;
import lc.core.LCRuntime;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

public class LCBlockRenderHook implements ISimpleBlockRenderingHandler {

	private final int renderIdx;
	private final DefinitionRegistry registry;

	public LCBlockRenderHook(int renderIdx) {
		this.renderIdx = renderIdx;
		this.registry = (DefinitionRegistry) LCRuntime.runtime.registries().definitions();
	}

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
		ILanteaCraftRenderer worker = registry.getRendererFor(RendererType.BLOCK, block.getClass());
		if (worker == null || !(worker instanceof LCBlockRenderer))
			return;
		LCBlockRenderer blockRenderer = (LCBlockRenderer) worker;
		while (blockRenderer != null && !blockRenderer.renderInventoryBlock(block, renderer, metadata)) {
			worker = registry.getRenderer(RendererType.BLOCK, blockRenderer.getParent());
			if (worker == null || !(worker instanceof LCBlockRenderer))
				return;
			blockRenderer = (LCBlockRenderer) worker;
		}
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId,
			RenderBlocks renderer) {
		ILanteaCraftRenderer worker = registry.getRendererFor(RendererType.BLOCK, block.getClass());
		if (worker == null || !(worker instanceof LCBlockRenderer))
			return false;
		LCBlockRenderer blockRenderer = (LCBlockRenderer) worker;
		while (blockRenderer != null && !blockRenderer.renderWorldBlock(block, renderer, world, x, y, z)) {
			worker = registry.getRenderer(RendererType.BLOCK, blockRenderer.getParent());
			if (worker == null || !(worker instanceof LCBlockRenderer))
				return false;
			blockRenderer = (LCBlockRenderer) worker;
		}
		return true;
	}

	@Override
	public boolean shouldRender3DInInventory(int modelId) {
		return true;
	}

	@Override
	public int getRenderId() {
		return renderIdx;
	}

}
