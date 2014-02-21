package pcl.lc.render.stargate;

import static pcl.lc.render.tileentity.TileEntityStargateBaseRenderer.cos;
import static pcl.lc.render.tileentity.TileEntityStargateBaseRenderer.sin;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import pcl.lc.LanteaCraft;
import pcl.lc.render.tileentity.TileEntityStargateBaseRenderer;
import pcl.lc.tileentity.TileEntityStargateBase;

public class EventHorizonRenderer implements IStargateRenderer {

	public final static int ehGridRadialSize = 10;
	public final static int ehGridPolarSize = StargateRenderConstants.numRingSegments;
	public final static double ehBandWidth = StargateRenderConstants.ringInnerRadius / ehGridRadialSize;
	private static ResourceLocation eventHorizonTexture = LanteaCraft.getResource("textures/fx/eventhorizon_"
			+ LanteaCraft.getProxy().getRenderMode() + ".png");
	private static ResourceLocation irisTexture = LanteaCraft.getResource("textures/fx/energy_iris_128.png");

	private TileEntityStargateBaseRenderer caller;

	@Override
	public void renderStargateAt(TileEntityStargateBaseRenderer renderer, TileEntityStargateBase te, double x,
			double y, double z, float t) {
		caller = renderer;
		renderEventHorizon(te);
	}

	private void renderEventHorizon(TileEntityStargateBase te) {
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
