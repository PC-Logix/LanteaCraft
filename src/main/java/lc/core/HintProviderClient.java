package lc.core;

import net.minecraftforge.client.MinecraftForgeClient;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import lc.api.defs.ILanteaCraftDefinition;
import lc.api.defs.IRecipeDefinition;
import lc.common.LCLog;
import lc.common.base.LCBlock;
import lc.common.base.LCBlockRenderHook;
import lc.common.base.LCItem;
import lc.common.base.LCItemRenderHook;
import lc.common.base.LCTile;
import lc.common.base.LCTileRenderHook;

/**
 * Client-side hint provider implementation
 * 
 * @author AfterLifeLochie
 * 
 */
public class HintProviderClient extends HintProviderServer {

	private LCBlockRenderHook blockRenderingHook;
	private LCTileRenderHook tileRenderingHook;
	private LCItemRenderHook itemRenderingHook;

	/** Default constructor */
	public HintProviderClient() {
		super();
		LCLog.debug("HintProviderClient providing client-side hints");
	}

	@Override
	public void preInit() {
		super.preInit();
		blockRenderingHook = new LCBlockRenderHook(RenderingRegistry.getNextAvailableRenderId());
		tileRenderingHook = new LCTileRenderHook();
		itemRenderingHook = new LCItemRenderHook();
		RenderingRegistry.registerBlockHandler(blockRenderingHook.getRenderId(), blockRenderingHook);
	}

	@Override
	public void init() {
		super.init();
		// TODO Auto-generated method stub
	}

	@Override
	public void postInit() {
		super.postInit();

	}

	@SuppressWarnings("unchecked")
	@Override
	public void provideHints(ILanteaCraftDefinition definition) {
		super.provideHints(definition);
		if (definition.getBlock() != null) {
			LCBlock theBlock = (LCBlock) definition.getBlock();
			theBlock.setRenderer(blockRenderingHook.getRenderId());
		}

		if (definition.getTileType() != null) {
			Class<? extends LCTile> theTile = (Class<? extends LCTile>) definition.getTileType();
			ClientRegistry.bindTileEntitySpecialRenderer(theTile, tileRenderingHook);
		}

		if (definition.getItem() != null && definition.getItem() instanceof LCItem) {
			LCItem theItem = (LCItem) definition.getItem();
			MinecraftForgeClient.registerItemRenderer(theItem, itemRenderingHook);
		}

	}

	@Override
	public void provideHints(IRecipeDefinition definition) {
		super.provideHints(definition);
		// TODO Auto-generated method stub

	}

}
