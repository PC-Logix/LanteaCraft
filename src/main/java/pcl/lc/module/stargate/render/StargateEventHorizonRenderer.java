package pcl.lc.module.stargate.render;

import static pcl.lc.module.stargate.render.TileStargateBaseRenderer.cos;
import static pcl.lc.module.stargate.render.TileStargateBaseRenderer.sin;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import pcl.lc.api.EnumStargateType;
import pcl.lc.core.ResourceAccess;
import pcl.lc.module.stargate.tile.TileStargateBase;

public class StargateEventHorizonRenderer implements IStargateRenderer {

	public final static int ehGridRadialSize = 10;
	public final static int ehGridPolarSize = StargateRenderConstants.numRingSegments;
	public final static double ehBandWidth = StargateRenderConstants.ringInnerRadius / ehGridRadialSize;
	private static ResourceLocation eventHorizonTexture = ResourceAccess.getNamedResource(ResourceAccess
			.formatResourceName("textures/fx/eventhorizon_${TEX_QUALITY}.png"));
	private static ResourceLocation irisTexture = ResourceAccess.getNamedResource("textures/fx/energy_iris_128.png");

	private TileStargateBaseRenderer caller;

	@Override
	public void renderStargateAt(TileStargateBaseRenderer renderer, EnumStargateType type, TileStargateBase te,
			double x, double y, double z, float t) {
		caller = renderer;
		renderEventHorizon(te);
	}

	private void renderEventHorizon(TileStargateBase te) {
		caller.bind(eventHorizonTexture);
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glNormal3d(0, 0, 1);
		double grid[][] = te.getEventHorizonGrid()[0];
		for (int i = 1; i < ehGridRadialSize; i++) {
			GL11.glBegin(GL11.GL_QUAD_STRIP);
			for (int j = 0; j <= ehGridPolarSize; j++) {
				eventHVertex(grid, i, j);
				eventHVertex(grid, i + 1, j);
			}
			GL11.glEnd();
		}

		GL11.glBegin(GL11.GL_TRIANGLE_FAN);
		GL11.glTexCoord2d(0.5, 0.5);
		GL11.glVertex3d(0, 0, grid[1][0]);
		for (int j = 0; j <= ehGridPolarSize; j++)
			eventHVertex(grid, 1, j);
		GL11.glEnd();
		GL11.glEnable(GL11.GL_CULL_FACE);
	}

	private void eventHVertex(double[][] grid, int i, int j) {
		double r = i * ehBandWidth;
		double x = r * cos[j];
		double y = r * sin[j];

		double u = (x + 3) / 6;
		double v = (y + 3) / 6;
		GL11.glTexCoord2d(u, v);
		GL11.glVertex3d(x, y, grid[j + 1][i]);
	}

	private void irisIntVertex(double k, int i, int j) {
		double r = i * ehBandWidth;
		double x = r * cos[j];
		double y = r * sin[j];

		double u = (x + 3) / 6;
		double v = (y + 3) / 6;
		GL11.glTexCoord2d(u, v);
		GL11.glVertex3d(x, y, k);
	}

}
