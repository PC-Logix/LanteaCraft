package pcl.lc.render.tileentity;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import pcl.lc.LanteaCraft;
import pcl.lc.tileentity.TileEntityLanteaDecorGlass;
import cpw.mods.fml.client.FMLClientHandler;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class TileEntityLanteaDecorGlassRenderer extends TileEntitySpecialRenderer {

	private ResourceLocation glassTexture;

	private double[][] rotationMatrix = { // Rotation view matrix
	{ 0.0D, -1.0D, 0.0D }, // Bottom
			{ 0.0D, 1.0D, 0.0D }, // Top
			{ 0.0D, 1.0D, 0.0D }, // North
			{ 0.0D, 1.0D, 0.0D }, // South
			{ 0.0D, 1.0D, 0.0D }, // East
			{ 0.0D, 1.0D, 0.0D }, // West
	};

	public TileEntityLanteaDecorGlassRenderer() {
		glassTexture = LanteaCraft.getResource("textures/tileentity/lantean_glass_128.png");
	}

	@Override
	public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float f) {
		TileEntityLanteaDecorGlass glass = (TileEntityLanteaDecorGlass) tileEntity;
		GL11.glPushMatrix();
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glScalef(1.0f, 1.0f, 1.0f);
		FMLClientHandler.instance().getClient().renderEngine.bindTexture(glassTexture);
		GL11.glTranslated(x, y, z);

		for (int i = 0; i < 6; i++) {
			if (glass.renderSide(i)) {
				GL11.glPushMatrix();
				GL11.glTranslated(0.5d, 0.5d, 0.5d);
				switch (i) {
				case 1:
					GL11.glRotated(180, 1.0d, 0.0d, 0.0d);
					break;
				case 2:
					GL11.glRotated(90, 1.0d, 0.0d, 0.0d);
					break;
				case 3:
					GL11.glRotated(270, 1.0d, 0.0d, 0.0d);
					GL11.glRotated(180, 0.0d, 1.0d, 0.0d);
					break;
				case 4:
					GL11.glRotated(270, 0.0d, 0.0d, 1.0d);
					GL11.glRotated(90, 0.0d, 1.0d, 0.0d);
					break;
				case 5:
					GL11.glRotated(90, 0.0d, 0.0d, 1.0d);
					GL11.glRotated(270, 0.0d, 1.0d, 0.0d);
					break;
				}
				applyRotationToSide(i, glass.sideRotation(i));
				GL11.glTranslated(-0.5d, -0.5d, -0.5d);
				double qx = frameCoordFromSide(glass.sideType(i));
				double qy = (glass.sideType(i) == 5) ? 0.2D : 0.0D;
				pushQuad(0.0d, 0.0d, 1.0d, 1.0d, qx, qy);
				GL11.glPopMatrix();
			}
		}

		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glPopMatrix();
	}

	private double frameCoordFromSide(int sideType) {
		switch (sideType) {
		case 4:
		case 5:
			return 0.0d;
		case 1:
			return 0.25d;
		case 2:
			return 0.5d;
		case 3:
			return 0.75d;
		}
		return 0.0d;
	}

	private void rotateSide(int side, double rotation) {
		GL11.glRotated(rotation, rotationMatrix[side][0], rotationMatrix[side][1], rotationMatrix[side][2]);
	}

	private void applyRotationToSide(int side, int rotationType) {
		double[][] rotationRule = { { 180, 270, 0, 90 }, { 0, 270, 180, 90 }, { 90, 180, 270 }, { 270, 180, 90 } };

		if (side == 0 || side == 1) {
			rotateSide(side, rotationRule[side][rotationType]);
		} else if (rotationType > 0)
			if (side == 2 || side == 3)
				rotateSide(side, rotationRule[2][rotationType - 1]);
			else
				rotateSide(side, rotationRule[3][rotationType - 1]);
	}

	private void pushQuad(double x0, double z0, double x1, double z1, double tx, double ty) {
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glTexCoord2d(tx, ty);
		GL11.glVertex3d(x0, 0.0d, z0);

		GL11.glTexCoord2d(tx + 0.25d, ty);
		GL11.glVertex3d(x1, 0.0d, z0);

		GL11.glTexCoord2d(tx + 0.25d, ty + 0.2d);
		GL11.glVertex3d(x1, 0.0d, z1);

		GL11.glTexCoord2d(tx, ty + 0.2d);
		GL11.glVertex3d(x0, 0.0d, z1);

		GL11.glEnd();
	}

}
