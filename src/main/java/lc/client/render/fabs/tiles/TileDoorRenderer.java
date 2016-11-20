package lc.client.render.fabs.tiles;

import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import lc.common.base.LCTile;
import lc.common.base.LCTileRenderer;
import lc.common.base.pipeline.LCTileRenderPipeline;
import lc.common.configuration.xml.ComponentConfig;
import lc.common.resource.ResourceAccess;
import lc.common.util.math.Orientations;
import lc.tiles.TileLanteaDoor;

public class TileDoorRenderer extends LCTileRenderer {

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

		TileLanteaDoor door = (TileLanteaDoor) tile;
		String doorType = (door.getBlockMetadata() != 0) ? "goauld" : "lantean";

		ResourceLocation whatTex = ResourceAccess.getNamedResource(ResourceAccess.formatResourceName(
				"textures/blocks/%s_door_bottom_${TEX_QUALITY}.png", doorType));
		if (!door.hasBlockAbove())
			whatTex = ResourceAccess.getNamedResource(ResourceAccess.formatResourceName(
					"textures/blocks/%s_door_top_${TEX_QUALITY}.png", doorType));
		renderer.bind(whatTex);

		GL11.glPushMatrix();
		GL11.glTranslated(x, y, z);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glColor3f(1.0f, 1.0f, 1.0f);

		float frame = door.clientAnimation;

		float w = 0.085f;
		float d0 = 0.5f - w, d1 = 0.5f + w;

		GL11.glTranslatef(0.5f, 0.0f, 0.5f);
		GL11.glRotatef(Orientations.from(tile.getRotation()).angle(), 0, 1, 0);
		GL11.glTranslatef(-0.5f, 0.0f, -0.5f);

		float dt = (20 - frame);
		float angle = (90.0f / 20.0f) * dt;
		GL11.glTranslatef(0.2f, 0.5f, 0.5f);
		if (!door.getDoorState())
			angle = 90.0f - angle;
		GL11.glRotatef(angle, 0, 1, 0);
		GL11.glTranslatef(-0.2f, -0.5f, -0.5f);

		GL11.glBegin(GL11.GL_QUADS);
		GL11.glTexCoord2f(1, 1);
		GL11.glVertex3f(0, 0, d0);
		GL11.glTexCoord2f(1, 0);
		GL11.glVertex3f(0, 1, d0);
		GL11.glTexCoord2f(0, 0);
		GL11.glVertex3f(1, 1, d0);
		GL11.glTexCoord2f(0, 1);
		GL11.glVertex3f(1, 0, d0);

		GL11.glTexCoord2f(0, 1);
		GL11.glVertex3f(0, 0, d1);
		GL11.glTexCoord2f(1, 1);
		GL11.glVertex3f(1, 0, d1);
		GL11.glTexCoord2f(1, 0);
		GL11.glVertex3f(1, 1, d1);
		GL11.glTexCoord2f(0, 0);
		GL11.glVertex3f(0, 1, d1);

		GL11.glVertex3f(1, 0, d0);
		GL11.glVertex3f(1, 1, d0);
		GL11.glVertex3f(1, 1, d1);
		GL11.glVertex3f(1, 0, d1);

		GL11.glVertex3f(0, 0, d0);
		GL11.glVertex3f(0, 0, d1);
		GL11.glVertex3f(0, 1, d1);
		GL11.glVertex3f(0, 1, d0);

		if (!door.hasBlockAbove()) { // face cull
			GL11.glVertex3f(0, 1, d0);
			GL11.glVertex3f(0, 1, d1);
			GL11.glVertex3f(1, 1, d1);
			GL11.glVertex3f(1, 1, d0);
		}
		
		if (!door.hasBlockBelow()) { // face cull
			GL11.glVertex3f(0, 0, d0);
			GL11.glVertex3f(1, 0, d0);
			GL11.glVertex3f(1, 0, d1);
			GL11.glVertex3f(0, 0, d1);
		}

		GL11.glEnd();
		GL11.glEnable(GL11.GL_LIGHTING);

		GL11.glPopMatrix();
		return true;
	}

}
