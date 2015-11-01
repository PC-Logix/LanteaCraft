package lc.client;

import lc.LCRuntime;
import lc.api.audio.ISoundController;
import lc.api.components.ComponentType;
import lc.api.components.IConfigurationProvider;
import lc.api.defs.IContainerDefinition;
import lc.api.defs.IRecipeDefinition;
import lc.api.rendering.IParticleMachine;
import lc.blocks.BlockBrazier;
import lc.blocks.BlockConfigurator;
import lc.blocks.BlockDHD;
import lc.blocks.BlockLanteaDoor;
import lc.blocks.BlockObelisk;
import lc.client.openal.ClientSoundController;
import lc.client.opengl.ParticleMachine;
import lc.client.render.fabs.blocks.BlockBrazierRenderer;
import lc.client.render.fabs.blocks.BlockConfiguratorRenderer;
import lc.client.render.fabs.blocks.BlockDHDRenderer;
import lc.client.render.fabs.blocks.BlockDoorRenderer;
import lc.client.render.fabs.blocks.BlockObeliskRenderer;
import lc.client.render.fabs.entities.EntityStaffProjectileRenderer;
import lc.client.render.fabs.items.ItemDecoratorRenderer;
import lc.client.render.fabs.tiles.TileConfiguratorRenderer;
import lc.client.render.fabs.tiles.TileDHDRenderer;
import lc.client.render.fabs.tiles.TileDoorRenderer;
import lc.client.render.fabs.tiles.TileStargateBaseRenderer;
import lc.client.render.fabs.tiles.TileTransportRingRenderer;
import lc.common.LCLog;
import lc.common.base.LCBlock;
import lc.common.base.LCEntityRenderer;
import lc.common.base.LCItem;
import lc.common.base.LCTile;
import lc.common.base.pipeline.LCBlockRenderPipeline;
import lc.common.base.pipeline.LCEntityRenderPipeline;
import lc.common.base.pipeline.LCItemRenderPipeline;
import lc.common.base.pipeline.LCTileRenderPipeline;
import lc.common.configuration.xml.ComponentConfig;
import lc.common.impl.registry.DefinitionRegistry;
import lc.entity.EntityStaffProjectile;
import lc.items.ItemDecorator;
import lc.server.HintProviderServer;
import lc.tiles.TileConfigurator;
import lc.tiles.TileDHD;
import lc.tiles.TileLanteaDoor;
import lc.tiles.TileStargateBase;
import lc.tiles.TileTransportRing;
import net.minecraft.entity.Entity;
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

	private LCBlockRenderPipeline blockRenderingHook;
	private LCTileRenderPipeline tileRenderingHook;
	private LCItemRenderPipeline itemRenderingHook;
	private LCEntityRenderPipeline entityRenderingHook;

	private ClientSoundController soundController;
	private ParticleMachine particleMachine;

	/** Default constructor */
	public HintProviderClient() {
		super();
		LCLog.debug("HintProviderClient providing client-side hints");
	}

	@Override
	public void preInit() {
		super.preInit();
		blockRenderingHook = new LCBlockRenderPipeline(RenderingRegistry.getNextAvailableRenderId());
		tileRenderingHook = new LCTileRenderPipeline();
		itemRenderingHook = new LCItemRenderPipeline();
		entityRenderingHook = new LCEntityRenderPipeline();
		soundController = new ClientSoundController();
		particleMachine = new ParticleMachine();
		RenderingRegistry.registerBlockHandler(blockRenderingHook.getRenderId(), blockRenderingHook);
	}

	@Override
	public void init() {
		super.init();

		DefinitionRegistry registry = (DefinitionRegistry) LCRuntime.runtime.registries().definitions();
		registry.registerTileRenderer(TileStargateBase.class, TileStargateBaseRenderer.class);
		registry.registerTileRenderer(TileTransportRing.class, TileTransportRingRenderer.class);
		registry.registerItemRenderer(ItemDecorator.class, ItemDecoratorRenderer.class);

		registry.registerBlockRenderer(BlockLanteaDoor.class, BlockDoorRenderer.class);
		registry.registerBlockRenderer(BlockObelisk.class, BlockObeliskRenderer.class);
		registry.registerBlockRenderer(BlockBrazier.class, BlockBrazierRenderer.class);
		registry.registerBlockRenderer(BlockConfigurator.class, BlockConfiguratorRenderer.class);
		registry.registerBlockRenderer(BlockDHD.class, BlockDHDRenderer.class);
		registry.registerTileRenderer(TileLanteaDoor.class, TileDoorRenderer.class);
		registry.registerTileRenderer(TileDHD.class, TileDHDRenderer.class);
		registry.registerTileRenderer(TileConfigurator.class, TileConfiguratorRenderer.class);

		registry.registerEntityRenderer(EntityStaffProjectile.class, EntityStaffProjectileRenderer.class);
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

		if (definition.getEntityType() != null) {
			Class<? extends Entity> theEntity = definition.getEntityType();
			RenderingRegistry.registerEntityRenderingHandler(theEntity, entityRenderingHook);
		}

	}

	@Override
	public void provideHints(IRecipeDefinition definition) {
		super.provideHints(definition);
		// TODO Auto-generated method stub

	}

	@Override
	public ISoundController audio() {
		return soundController;
	}

	@Override
	public IParticleMachine particles() {
		return particleMachine;
	}

}
