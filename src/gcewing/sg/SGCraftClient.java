//------------------------------------------------------------------------------------------------
//
//   SG Craft - Client Proxy
//
//------------------------------------------------------------------------------------------------

package gcewing.sg;

import gcewing.sg.base.BaseMod;
import gcewing.sg.base.BaseModClient;
import gcewing.sg.core.SGGui;
import gcewing.sg.gui.SGBaseScreen;
import gcewing.sg.gui.SGControllerScreen;
import gcewing.sg.render.BaseBlockRenderer;
import gcewing.sg.render.BaseOrientedCtrBlkRenderer;
import gcewing.sg.render.SGBaseBlockRenderer;
import gcewing.sg.render.SGBaseTERenderer;
import gcewing.sg.render.SGControllerModel;
import gcewing.sg.render.SGControllerTERenderer;
import gcewing.sg.render.SGPegasusBaseTERenderer;
import gcewing.sg.render.SGRingBlockRenderer;
import gcewing.sg.tileentity.SGBaseTE;
import gcewing.sg.tileentity.SGControllerTE;
import gcewing.sg.tileentity.SGPegasusBaseTE;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class SGCraftClient extends BaseModClient {

	public SGCraftClient(BaseMod mod) {
		super(mod);
	}

	@Override
	public void registerScreens() {
		addScreen(SGGui.SGBase, SGBaseScreen.class);
		addScreen(SGGui.SGController, SGControllerScreen.class);
	}

	@Override
	public void registerRenderers() {
		SGCraft.Render.modelController = new SGControllerModel("/assets/gcewing_sg/models/dhd.obj");

		SGCraft.Render.tileEntityBaseRenderer = new SGBaseTERenderer();
		addTileEntityRenderer(SGBaseTE.class, SGCraft.Render.tileEntityBaseRenderer);
		SGCraft.Render.tileEntityPegausBaseRenderer = new SGPegasusBaseTERenderer();
		addTileEntityRenderer(SGPegasusBaseTE.class, SGCraft.Render.tileEntityPegausBaseRenderer);
		SGCraft.Render.tileEntityControllerRenderer = new SGControllerTERenderer();
		addTileEntityRenderer(SGControllerTE.class, SGCraft.Render.tileEntityControllerRenderer);

		SGCraft.Render.blockOrientedRenderer = new BaseOrientedCtrBlkRenderer();
		registerRenderer(SGCraft.Render.blockOrientedRenderer);
		SGCraft.Render.blockBaseRenderer = new SGBaseBlockRenderer();
		registerRenderer(SGCraft.Render.blockBaseRenderer);
		SGCraft.Render.blockRingRenderer = new SGRingBlockRenderer();
		registerRenderer(SGCraft.Render.blockRingRenderer);
	}

	void registerRenderer(BaseBlockRenderer renderer) {
		int id = RenderingRegistry.getNextAvailableRenderId();
		renderer.renderID = id;
		RenderingRegistry.registerBlockHandler(renderer);
	}
}
