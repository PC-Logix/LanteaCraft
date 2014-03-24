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

	ResourceLocation glassTexture;

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

		for (int side = 0; side < 4; side++) {
			if (glass.renderSide(side + 2)) {
				GL11.glPushMatrix();
				GL11.glTranslated(0.5d, 0.5d, 0.5d);
				GL11.glRotated(90, 1.0d, 0.0d, 0.0d);
				switch (side) {
				case 1:
					GL11.glRotated(90, 0.0d, 0.0d, 1.0d);
					break;
				case 2:
					GL11.glRotated(180, 0.0d, 0.0d, 1.0d);
					break;
				case 3:
					GL11.glRotated(-90, 0.0d, 0.0d, 1.0d);
					break;
				}
				GL11.glTranslated(-0.5d, -0.5d, -0.5d);
				pushQuad(0.0d, 0.0d, 1.0d, 1.0d, 0.0d, 0.0d);
				pushQuad(0.0d, 0.0d, 1.0d, 1.0d, 0.0d, 0.4d);
				GL11.glPopMatrix();
			} else 
				System.out.println("Skip side " + side);
		}

		for (int flip = 0; flip < 2; flip++) {
			if (glass.renderSide(flip)) {
				GL11.glPushMatrix();
				GL11.glTranslated(0.5d, 0.5d, 0.5d);
				if (flip == 1)
					GL11.glRotated(180, 1.0d, 0.0d, 0.0d);
				GL11.glTranslated(-0.5d, -0.5d, -0.5d);
				pushQuad(0.0d, 0.0d, 1.0d, 1.0d, 0.0d, 0.0d);
				GL11.glPopMatrix();
			} else
				System.out.println("Skip side " + flip);
		}

		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glPopMatrix();

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
