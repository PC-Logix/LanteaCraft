package gcewing.sg.render.tileentity;

import gcewing.sg.SGCraft;
import gcewing.sg.tileentity.TileEntityStargateBase;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

public class TileEntityPegasusStargateBaseRenderer extends TileEntityStargateBaseRenderer {
	@Override
	void renderEventHorizon(TileEntityStargateBase te) {
		eventHorizonTexture = new ResourceLocation("gcewing_sg", "textures/fx/eventhorizon" + SGCraft.getProxy().getRenderMode() + ".png");

		// bindTextureByName("/misc/water.png");
		bindTexture(eventHorizonTexture);
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glNormal3d(0, 0, 1);
		double grid[][] = te.getEventHorizonGrid()[0];
		for (int i = 1; i < ehGridRadialSize; i++) {
			GL11.glBegin(GL11.GL_QUAD_STRIP);
			for (int j = 0; j <= ehGridPolarSize; j++) {
				ehVertex(grid, i, j);
				ehVertex(grid, i + 1, j);
			}
			GL11.glEnd();
		}
		GL11.glBegin(GL11.GL_TRIANGLE_FAN);
		GL11.glTexCoord2d(0, 0);
		GL11.glVertex3d(0, 0, grid[1][0]);
		for (int j = 0; j <= ehGridPolarSize; j++)
			ehVertex(grid, 1, j);
		GL11.glEnd();
		GL11.glEnable(GL11.GL_CULL_FACE);
	}
}
