package lc.client.render.fabs.tiles;

import lc.api.stargate.StargateType;
import lc.client.animation.Animation;
import lc.client.models.ModelStargate;
import lc.common.base.LCTile;
import lc.common.base.LCTileRenderer;
import lc.common.base.multiblock.MultiblockState;
import lc.common.base.pipeline.LCTileRenderPipeline;
import lc.common.configuration.xml.ComponentConfig;
import lc.common.resource.ResourceAccess;
import lc.common.resource.ResourceMap;
import lc.common.util.data.StateMap;
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

	/** The texture resource map */
	public final ResourceMap resources = new ResourceMap();

	public final ResourceLocation fxHorizon = ResourceAccess.getNamedResource(ResourceAccess
			.formatResourceName("textures/fx/eventhorizon_${TEX_QUALITY}.png"));

	public final static int ehGridRadialSize = 10;
	public final static int irBladeSize = 32;
	public final static int ehGridPolarSize = ModelStargate.numRingSegments;
	public final static double ehBandWidth = ModelStargate.ringInnerRadius / ehGridRadialSize;

	/** The Stargate model */
	public final ModelStargate model;

	/** Default constructor */
	public TileStargateBaseRenderer() {
		for (StargateType type : StargateType.values()) {
			ResourceLocation fr, gl;
			String suffix = (type.getSuffix() != null && type.getSuffix().length() > 0) ? "_" + type.getSuffix()
					+ "_${TEX_QUALITY}.png" : "_${TEX_QUALITY}.png";
			fr = ResourceAccess.getNamedResource(ResourceAccess.formatResourceName("textures/tileentity/stargate"
					+ suffix));
			gl = ResourceAccess.getNamedResource(ResourceAccess
					.formatResourceName("textures/tileentity/stargate_glyphs" + suffix));
			ResourceMap map = new ResourceMap();
			map.add("frame", fr).add("glyphs", gl);
			resources.add(type.getName(), map);
		}

		model = new ModelStargate();
		model.init();
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
		if (tile instanceof TileStargateBase) {
			TileStargateBase base = (TileStargateBase) tile;
			if (base.getState() == MultiblockState.FORMED) {
				GL11.glPushMatrix();
				GL11.glTranslated(x + 0.5d, y + 3.5d, z + 0.5d);
				StateMap state = base.renderInfoTile().tileRenderState();
				Animation animation = (Animation) base.renderInfoTile().tileAnimation();
				if (animation != null) {
					Double frame = base.renderInfoTile().tileAnimationProgress() + (double) partialTickTime;
					if (!animation.finished(frame))
						animation.sampleProperties(state, frame);
				}
				model.render(this, renderer, base, state);
				
				if (state.get("iris", false)) {
					if (state.get("iris-type", "mechanical").equals("mechanical")) {
						renderMechIrisImmediate(base, renderer, state, x, y, z, partialTickTime);
					} else {
						renderGhostIrisImmediate(base, renderer, state, x, y, z, partialTickTime);
					}
				}

				if (state.get("event-horizon", false))
					renderGfxHorizonImmediate(base, renderer, state, x, y, z);

				GL11.glPopMatrix();
			}
		}
		return true;
	}

	private void renderMechIrisImmediate(TileStargateBase tile, LCTileRenderPipeline pipeline, StateMap state,
			double x, double y, double z, float t) {
		double progress = 1.0d - state.get("iris-progress", 0.0d);
		for (int i = 0; i < irBladeSize; i++) {
			GL11.glPushMatrix();
			GL11.glRotated(360.0 * i / irBladeSize, 0.0, 0.0, 1.0);
			double tiltAngle = progress * 60;
			double radius = ModelStargate.ringMidRadius;
			double longW = ModelStargate.ringMidRadius, shortW = ModelStargate.ringInnerRadius;
			double heightZ = 0.2, heightQ = 1.00;
			double length = heightQ - (heightQ - heightZ) * progress * progress;
			double u0 = shortW / longW, v0 = length / heightQ;
			double v1 = heightZ / heightQ;
			double depth0 = 0.05, depth1 = 0.01;

			GL11.glTranslated(radius, 0, 0);
			GL11.glRotated(-tiltAngle, 0, 0, 1);
			GL11.glBegin(GL11.GL_TRIANGLE_FAN);
			GL11.glTexCoord2d(0, 0);
			GL11.glVertex3d(-longW, 0, depth0);
			GL11.glTexCoord2d(1, 0);
			GL11.glVertex3d(0, 0, depth0 + depth1);
			GL11.glTexCoord2d(1, v1);
			GL11.glVertex3d(0, heightZ, depth0 + depth1);
			GL11.glTexCoord2d(u0, v0);
			GL11.glVertex3d(-longW + shortW, length, depth0);
			GL11.glTexCoord2d(0, v0);
			GL11.glVertex3d(-longW, length, depth0);
			GL11.glEnd();

			GL11.glBegin(GL11.GL_TRIANGLE_FAN);
			GL11.glTexCoord2d(0, 0);
			GL11.glVertex3d(-longW, 0, depth0);
			GL11.glTexCoord2d(0, v0);
			GL11.glVertex3d(-longW, length, depth0);
			GL11.glTexCoord2d(u0, v0);
			GL11.glVertex3d(-longW + shortW, length, depth0);
			GL11.glTexCoord2d(1, v1);
			GL11.glVertex3d(0, heightZ, depth0 - depth1);
			GL11.glTexCoord2d(1, 0);
			GL11.glVertex3d(0, 0, depth0 - depth1);
			GL11.glEnd();
			GL11.glPopMatrix();
		}

	}

	private void renderGhostIrisImmediate(TileStargateBase tile, LCTileRenderPipeline pipeline, StateMap state,
			double x, double y, double z, float t) {

	}

	private void renderGfxHorizonImmediate(TileStargateBase tile, LCTileRenderPipeline pipeline, StateMap state,
			double x, double y, double z) {
		pipeline.bind(fxHorizon);
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glNormal3d(0, 0, 1);
		double grid[][] = tile.getGfxGrid()[0];
		for (int i = 1; i < ehGridRadialSize; i++) {
			GL11.glBegin(GL11.GL_QUAD_STRIP);
			for (int j = 0; j <= ehGridPolarSize; j++) {
				eventHVertex(state, grid, i, j);
				eventHVertex(state, grid, i + 1, j);
			}
			GL11.glEnd();
		}

		GL11.glBegin(GL11.GL_TRIANGLE_FAN);
		GL11.glTexCoord2d(0.5, 0.5);
		GL11.glVertex3d(0, 0, grid[1][0]);
		for (int j = 0; j <= ehGridPolarSize; j++)
			eventHVertex(state, grid, 1, j);
		GL11.glEnd();
		GL11.glEnable(GL11.GL_CULL_FACE);

	}

	private void eventHVertex(StateMap state, double[][] grid, int i, int j) {
		double r = i * 2.75d / 10.0d;
		double x = r * ModelStargate.cos[j];
		double y = r * ModelStargate.sin[j];

		double u = (x + 3) / 6;
		double v = (y + 3) / 6;
		GL11.glTexCoord2d(u, v);
		if (state.get("iris", false)) {
			GL11.glVertex3d(x, y, Math.min(grid[j + 1][i], 0.035));
		} else
			GL11.glVertex3d(x, y, grid[j + 1][i]);
	}
}
