package lc.client.render.fabs.tiles;

import org.lwjgl.opengl.GL11;

import lc.api.stargate.StargateType;
import lc.client.models.ModelDHD;
import lc.common.base.LCTile;
import lc.common.base.LCTileRenderer;
import lc.common.base.pipeline.LCTileRenderPipeline;
import lc.common.configuration.xml.ComponentConfig;
import lc.common.util.math.Orientations;
import lc.tiles.TileDHD;

public class TileDHDRenderer extends LCTileRenderer {


	@Override
	public void configure(ComponentConfig c) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public LCTileRenderer getParent() {
		return null;
	}

	@Override
	public boolean renderTileEntityAt(LCTile tile, LCTileRenderPipeline renderer, double x, double y, double z,
			float partialTickTime) {
		if (ModelDHD.$ == null)
			return false;
		GL11.glPushMatrix();
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glTranslated(x, y, z);
		GL11.glTranslatef(0.5f, 0.0f, 0.5f);
		GL11.glRotatef(Orientations.from(tile.getRotation()).angle(), 0, 1, 0);
		GL11.glTranslatef(-0.5f, 0.0f, -0.5f);
		TileDHD dhd = (TileDHD) tile;
		ModelDHD.$.prepareAndRender(StargateType.fromOrdinal(tile.getBlockMetadata()), dhd.clientAskConnectionOpen());
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glPopMatrix();
		return true;
	}

}
