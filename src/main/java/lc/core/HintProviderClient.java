package lc.core;

import net.minecraftforge.client.MinecraftForgeClient;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import lc.api.defs.ILanteaCraftDefinition;
import lc.api.defs.IRecipeDefinition;
import lc.common.IHintProvider;
import lc.common.base.LCBlock;
import lc.common.base.LCBlockRenderHook;
import lc.common.base.LCItem;
import lc.common.base.LCItemRenderHook;
import lc.common.base.LCTile;
import lc.common.base.LCTileRenderHook;

public class HintProviderClient implements IHintProvider {

	private LCBlockRenderHook blockRenderingHook;
	private LCTileRenderHook tileRenderingHook;
	private LCItemRenderHook itemRenderingHook;

	public HintProviderClient() {
		// TODO Auto-generated method stub
	}

	@Override
	public void preInit() {
		// TODO Auto-generated method stub

	}

	@Override
	public void init() {
		blockRenderingHook = new LCBlockRenderHook(RenderingRegistry.getNextAvailableRenderId());
		tileRenderingHook = new LCTileRenderHook();
		itemRenderingHook = new LCItemRenderHook();
		RenderingRegistry.registerBlockHandler(blockRenderingHook.getRenderId(), blockRenderingHook);
	}

	@Override
	public void postInit() {
		// TODO Auto-generated method stub

	}

	@Override
	public void provideHints(ILanteaCraftDefinition definition) {
		if (definition.getBlock() != null) {
			LCBlock theBlock = (LCBlock) definition.getBlock();
			theBlock.setRenderer(blockRenderingHook.getRenderId());
		}

		if (definition.getTileType() != null) {
			Class<? extends LCTile> theTile = (Class<? extends LCTile>) definition.getTileType();
			ClientRegistry.bindTileEntitySpecialRenderer(theTile, tileRenderingHook);
		}

		if (definition.getItem() != null) {
			LCItem theItem = (LCItem) definition.getItem();
			MinecraftForgeClient.registerItemRenderer(theItem, itemRenderingHook);
		}

	}

	@Override
	public void provideHints(IRecipeDefinition definition) {
		// TODO Auto-generated method stub

	}

}
