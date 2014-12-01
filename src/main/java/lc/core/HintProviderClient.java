package lc.core;

import lc.api.defs.IContainerDefinition;
import lc.api.defs.IRecipeDefinition;
import lc.blocks.BlockStargateBase;
import lc.client.ItemDecoratorRenderer;
import lc.client.TileStargateBaseRenderer;
import lc.common.LCLog;
import lc.common.base.LCBlock;
import lc.common.base.LCBlockRenderHook;
import lc.common.base.LCItem;
import lc.common.base.LCItemRenderHook;
import lc.common.base.LCTile;
import lc.common.base.LCTileRenderHook;
import lc.common.impl.registry.DefinitionRegistry;
import lc.items.ItemDecorator;
import lc.tiles.TileStargateBase;
import net.minecraftforge.client.MinecraftForgeClient;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;

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

		DefinitionRegistry registry = (DefinitionRegistry) LCRuntime.runtime.registries().definitions();
		registry.registerTileRenderer(TileStargateBase.class, TileStargateBaseRenderer.class);
		registry.registerItemRenderer(ItemDecorator.class, ItemDecoratorRenderer.class);
	}

	@Override
	public void postInit() {
		super.postInit();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void provideHints(IContainerDefinition definition) {
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
