package lc.client.render;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.FMLClientHandler;
import lc.ResourceAccess;
import lc.api.stargate.StargateType;
import lc.blocks.BlockDHD;
import lc.client.models.ModelDHD;
import lc.client.models.loader.WavefrontModel.WavefrontModelException;
import lc.common.LCLog;
import lc.common.base.LCTile;
import lc.common.base.LCTileRenderer;
import lc.common.base.pipeline.LCTileRenderPipeline;
import lc.common.util.math.Orientations;
import lc.tiles.TileDHD;

public class TileDHDRenderer extends LCTileRenderer {

	/** The DHD model */
	public ModelDHD model;

	/** Default constructor */
	public TileDHDRenderer() {
		try {
			model = new ModelDHD();
		} catch (WavefrontModelException e) {
			LCLog.warn("Failed to load ModelDHD.", e);
			model = null;
		}
	}

	@Override
	public LCTileRenderer getParent() {
		return null;
	}

	@Override
	public boolean renderTileEntityAt(LCTile tile, LCTileRenderPipeline renderer, double x, double y, double z,
			float partialTickTime) {
		if (model == null)
			return false;
		StargateType typeof = StargateType.fromOrdinal(tile.getBlockMetadata());
		GL11.glPushMatrix();
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glTranslated(x, y, z);
		GL11.glTranslatef(0.5f, 0.0f, 0.5f);
		GL11.glRotatef(Orientations.from(tile.getRotation()).angle(), 0, 1, 0);
		GL11.glTranslatef(-0.5f, 0.0f, -0.5f);
		String typename = (typeof.getSuffix().length() != 0) ? "dhd_%s_" + typeof.getSuffix() : "dhd_%s";
		FMLClientHandler.instance().getClient().renderEngine.bindTexture(ResourceAccess.getNamedResource(ResourceAccess
				.formatResourceName("textures/models/%s_${TEX_QUALITY}.png", String.format(typename, "on"))));
		model.renderAll();
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glPopMatrix();
		return true;
	}

}
