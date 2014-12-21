package lc.client.render;

import lc.ResourceAccess;
import lc.client.models.ModelStargate;
import lc.common.base.LCTile;
import lc.common.base.LCTileRenderer;
import lc.common.base.pipeline.LCTileRenderPipeline;
import lc.tiles.TileStargateBase;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

/**
 * Stargate base tile renderer.
 *
 * @author AfterLifeLochie
 *
 */
public class TileStargateBaseRenderer extends LCTileRenderer {

	/** The frame texture resource */
	public final ResourceLocation texFrame;
	/** The glyph texture resource */
	public final ResourceLocation texGlyphs;

	/** The Stargate model */
	public final ModelStargate model;

	/** Default constructor */
	public TileStargateBaseRenderer() {
		texFrame = ResourceAccess.getNamedResource(ResourceAccess
				.formatResourceName("textures/tileentity/stargate_${TEX_QUALITY}.png"));
		texGlyphs = ResourceAccess.getNamedResource(ResourceAccess
				.formatResourceName("textures/tileentity/stargate_glyphs_${TEX_QUALITY}.png"));
		model = new ModelStargate();
		model.init();
	}

	@Override
	public LCTileRenderer getParent() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean renderTileEntityAt(LCTile tile, LCTileRenderPipeline renderer, double x, double y, double z,
			float partialTickTime) {
		if (tile instanceof TileStargateBase) {
			GL11.glPushMatrix();
			GL11.glTranslated(x + 0.5d, y + 3.5d, z + 0.5d);
			model.render(this, renderer, (TileStargateBase) tile);
			GL11.glPopMatrix();
		}
		return true;
	}
}
