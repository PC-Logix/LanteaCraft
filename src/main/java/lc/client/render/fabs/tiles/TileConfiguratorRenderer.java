package lc.client.render.fabs.tiles;

import org.lwjgl.opengl.GL11;

import lc.client.models.ModelLaptop;
import lc.common.base.LCTile;
import lc.common.base.LCTileRenderer;
import lc.common.base.pipeline.LCTileRenderPipeline;
import lc.common.configuration.xml.ComponentConfig;
import lc.common.resource.ResourceAccess;
import lc.common.util.math.Orientations;

public class TileConfiguratorRenderer extends LCTileRenderer {

	public TileConfiguratorRenderer() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void configure(ComponentConfig c) {
		// TODO Auto-generated method stub

	}

	@Override
	public LCTileRenderer getParent() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean renderTileEntityAt(LCTile tile, LCTileRenderPipeline renderer, double x, double y, double z,
			float partialTickTime) {
		if (ModelLaptop.$ == null)
			return false;
		GL11.glPushMatrix();
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glTranslated(x, y, z);
		renderer.bind(ResourceAccess.getNamedResource("textures/models/laptop.png"));
		GL11.glTranslatef(0.5f, 0.0f, 0.5f);
		GL11.glRotatef(Orientations.from(tile.getRotation()).angle(), 0.0f, 1.0f, 0.0f);
		GL11.glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
		GL11.glTranslatef(-0.5f, 0.0f, -0.5f);
		GL11.glTranslatef(0.0f, 1.0f, 0.0f);
		GL11.glRotatef(180.0f, 0.0f, 0.0f, 1.0f);
		GL11.glTranslatef(-0.5f, -0.5f, 0.5f);
		ModelLaptop.$.renderAll(-1.75f);
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glPopMatrix();
		return true;
	}

}
