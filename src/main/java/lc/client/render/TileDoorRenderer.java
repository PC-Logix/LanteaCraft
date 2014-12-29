package lc.client.render;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import lc.ResourceAccess;
import lc.common.LCLog;
import lc.common.base.LCTile;
import lc.common.base.LCTileRenderer;
import lc.common.base.pipeline.LCTileRenderPipeline;
import lc.common.util.math.Orientations;
import lc.tiles.TileLanteaDoor;

public class TileDoorRenderer extends LCTileRenderer {

	@Override
	public LCTileRenderer getParent() {
		return null;
	}

	@Override
	public boolean renderTileEntityAt(LCTile tile, LCTileRenderPipeline renderer, double x, double y, double z,
			float partialTickTime) {

		TileLanteaDoor door = (TileLanteaDoor) tile;
		NBTTagCompound doorCompound = door.getBaseCompound();
		ResourceLocation whatTex = ResourceAccess.getNamedResource(ResourceAccess
				.formatResourceName("textures/blocks/lantean_door_bottom_128.png"));

		if (doorCompound != null && doorCompound.hasKey("hasBlockBelow") && doorCompound.getBoolean("hasBlockBelow"))
			whatTex = ResourceAccess.getNamedResource(ResourceAccess
					.formatResourceName("textures/blocks/lantean_door_top_128.png"));
		renderer.bind(whatTex);

		if (door.getDoorState()) {
			switch (door.getRotation()) {
			case NORTH:

				break;
			case SOUTH:

				break;
			case EAST:

				break;
			case WEST:

				break;
			}
		}

		GL11.glPushMatrix();
		GL11.glTranslated(x, y, z);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glColor3f(1.0f, 1.0f, 1.0f);
		GL11.glTranslatef(0.5f, 0.0f, 0.5f);
		GL11.glRotatef(Orientations.from(tile.getRotation()).angle(), 0, 1, 0);
		GL11.glTranslatef(-0.5f, 0.0f, -0.5f);
		if (door.getDoorState())
			GL11.glTranslatef(-0.80f, 0.0f, 0.0f);

		float w = 0.085f;
		float d0 = 0.5f - w, d1 = 0.5f + w;

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

		GL11.glEnd();
		GL11.glEnable(GL11.GL_LIGHTING);

		GL11.glPopMatrix();
		return true;
	}

}
