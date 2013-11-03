package gcewing.sg;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import net.minecraft.client.renderer.tileentity.*;
import net.minecraft.tileentity.*;
import net.minecraft.util.*;


public class SGPegasusBaseTERenderer extends SGBaseTERenderer {
	@Override
	void renderEventHorizon(SGBaseTE te) {
		eventHorizonTexture = new ResourceLocation("gcewing_sg", "textures/eventhorizon_ATL_" + SGCraft.RenderHD + ".png");
		
		//bindTextureByName("/misc/water.png");
		bindTexture(eventHorizonTexture);
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glNormal3d(0, 0, 1);
		double grid[][] = te.getEventHorizonGrid()[0];
		for (int i = 1; i < ehGridRadialSize; i++) {
			GL11.glBegin(GL11.GL_QUAD_STRIP);
			for (int j = 0; j <= ehGridPolarSize; j++) {
				ehVertex(grid, i, j);
				ehVertex(grid, i+1, j);
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
