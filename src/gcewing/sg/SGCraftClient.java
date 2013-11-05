//------------------------------------------------------------------------------------------------
//
//   SG Craft - Client Proxy
//
//------------------------------------------------------------------------------------------------

package gcewing.sg;

import gcewing.sg.base.BaseMod;
import gcewing.sg.base.BaseModClient;
import gcewing.sg.core.EnumGuiList;
import gcewing.sg.gui.ScreenStargateBase;
import gcewing.sg.gui.ScreenStargateController;
import gcewing.sg.render.GenericBlockRenderer;
import gcewing.sg.render.BaseOrientedCtrBlkRenderer;
import gcewing.sg.render.blocks.BlockStargateBaseRenderer;
import gcewing.sg.render.blocks.BlockStargateRingRenderer;
import gcewing.sg.render.model.StargateControllerModel;
import gcewing.sg.render.tileentity.TileEntityPegasusStargateBaseRenderer;
import gcewing.sg.render.tileentity.TileEntityStargateBaseRenderer;
import gcewing.sg.render.tileentity.TileEntityStargateControllerRenderer;
import gcewing.sg.tileentity.TileEntityStargateBase;
import gcewing.sg.tileentity.TileEntityStargateController;
import gcewing.sg.tileentity.TileEntityPegasusStargateBase;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class SGCraftClient extends BaseModClient {

	public SGCraftClient(BaseMod mod) {
		super(mod);
	}

	@Override
	public void registerScreens() {
		addScreen(EnumGuiList.SGBase, ScreenStargateBase.class);
		addScreen(EnumGuiList.SGController, ScreenStargateController.class);
	}

	@Override
	public void registerRenderers() {
		SGCraft.Render.modelController = new StargateControllerModel("/assets/gcewing_sg/models/dhd.obj");

		SGCraft.Render.tileEntityBaseRenderer = new TileEntityStargateBaseRenderer();
		addTileEntityRenderer(TileEntityStargateBase.class, SGCraft.Render.tileEntityBaseRenderer);
		SGCraft.Render.tileEntityPegausBaseRenderer = new TileEntityPegasusStargateBaseRenderer();
		addTileEntityRenderer(TileEntityPegasusStargateBase.class, SGCraft.Render.tileEntityPegausBaseRenderer);
		SGCraft.Render.tileEntityControllerRenderer = new TileEntityStargateControllerRenderer();
		addTileEntityRenderer(TileEntityStargateController.class, SGCraft.Render.tileEntityControllerRenderer);

		SGCraft.Render.blockOrientedRenderer = new BaseOrientedCtrBlkRenderer();
		registerRenderer(SGCraft.Render.blockOrientedRenderer);
		SGCraft.Render.blockBaseRenderer = new BlockStargateBaseRenderer();
		registerRenderer(SGCraft.Render.blockBaseRenderer);
		SGCraft.Render.blockRingRenderer = new BlockStargateRingRenderer();
		registerRenderer(SGCraft.Render.blockRingRenderer);
	}

	void registerRenderer(GenericBlockRenderer renderer) {
		int id = RenderingRegistry.getNextAvailableRenderId();
		renderer.renderID = id;
		RenderingRegistry.registerBlockHandler(renderer);
	}
}
