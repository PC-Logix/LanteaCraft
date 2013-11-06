//------------------------------------------------------------------------------------------------
//
//   SG Craft - Stargate base tile entity renderer
//
//------------------------------------------------------------------------------------------------

package gcewing.sg.render.tileentity;

import gcewing.sg.SGCraft;
import gcewing.sg.base.BaseTileEntityRenderer;
import gcewing.sg.tileentity.TileEntityStargateBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class TileEntityStargateBaseRenderer extends BaseTileEntityRenderer {

	public final static int numRingSegments = 32;
	public final static double ringInnerRadius = 2.0;
	public final static double ringMidRadius = 2.25;
	public final static double ringOuterRadius = 2.5;
	public final static double ringDepth = 0.5;
	public final static double chevronInnerRadius = 2.25;
	public final static double chevronOuterRadius = ringOuterRadius + 1 / 16.0;
	public final static double chevronWidth = (chevronOuterRadius - chevronInnerRadius) * 1.5;
	public final static double chevronDepth = 0.125;
	public final static double chevronBorderWidth = chevronWidth / 6;
	public final static double chevronMotionDistance = 1 / 8.0;

	public final static int ringFaceTextureIndex = 0x14;
	public final static int ringTextureIndex = 0x15;
	public final static int ringSymbolTextureIndex = 0x20;
	public final static int chevronTextureIndex = 0x05;
	public final static int chevronLitTextureIndex = 0x16;

	public final static double ringSymbolTextureLength = 27 * 8;
	public final static double ringSymbolTextureHeight = 12;
	public final static double ringSymbolSegmentWidth = ringSymbolTextureLength / numRingSegments;

	public final static int ehGridRadialSize = 5;
	public final static int ehGridPolarSize = numRingSegments;
	public final static double ehBandWidth = ringInnerRadius / ehGridRadialSize;

	public static double s[] = new double[numRingSegments + 1];
	public static double c[] = new double[numRingSegments + 1];

	static {
		for (int i = 0; i <= numRingSegments; i++) {
			double a = 2 * Math.PI * i / numRingSegments;
			s[i] = Math.sin(a);
			c[i] = Math.cos(a);
		}
	}

	double u0, v0;

	@Override
	public void renderTileEntityAt(TileEntity te, double x, double y, double z, float t) {
		// System.out.printf("SGBaseTERenderer.renderTileEntityAt (%g,%g,%g)\n",
		// x, y, z);
		TileEntityStargateBase tesg = (TileEntityStargateBase) te;
		if (tesg.isMerged) {
			GL11.glPushMatrix();
			GL11.glEnable(GL12.GL_RESCALE_NORMAL);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			GL11.glTranslated(x + 0.5, y + 2.5, z + 0.5);
			renderStargate(tesg, t);
			GL11.glDisable(GL12.GL_RESCALE_NORMAL);
			GL11.glPopMatrix();
		}
	}

	void renderStargate(TileEntityStargateBase te, float t) {
		GL11.glRotatef(90 * te.getRotation(), 0, 1, 0);
		bindTexture(SGCraft.getInstance().getResource(
				"textures/tileentity/stargate_" + SGCraft.getProxy().getRenderMode() + ".png"));
		GL11.glNormal3f(0, 1, 0);
		renderRing(ringMidRadius, ringOuterRadius, RingType.Outer);
		renderInnerRing(te, t);
		renderChevrons(te);
		if (te.isConnected())
			renderEventHorizon(te);
	}

	void renderInnerRing(TileEntityStargateBase te, float t) {
		GL11.glPushMatrix();
		GL11.glRotatef((float) (te.interpolatedRingAngle(t) - (135 - TileEntityStargateBase.ringSymbolAngle / 2)), 0,
				0, 1);
		renderRing(ringInnerRadius, ringMidRadius, RingType.Inner);
		GL11.glPopMatrix();
	}

	void renderRing(double r1, double r2, RingType type) {
		double z = ringDepth / 2;
		double u = 0, du = 0, dv = 0;
		GL11.glBegin(GL11.GL_QUADS);
		for (int i = 0; i < numRingSegments; i++) {
			selectTile(ringTextureIndex);
			// Outer
			if (type == RingType.Outer) {
				GL11.glNormal3d(c[i], s[i], 0);
				vertex(r2 * c[i], r2 * s[i], z, 0, 0);
				vertex(r2 * c[i], r2 * s[i], -z, 0, 16);
				vertex(r2 * c[i + 1], r2 * s[i + 1], -z, 16, 16);
				vertex(r2 * c[i + 1], r2 * s[i + 1], z, 16, 0);
			}
			// Inner
			if (type == RingType.Inner) {
				GL11.glNormal3d(-c[i], -s[i], 0);
				vertex(r1 * c[i], r1 * s[i], -z, 0, 0);
				vertex(r1 * c[i], r1 * s[i], z, 0, 16);
				vertex(r1 * c[i + 1], r1 * s[i + 1], z, 16, 16);
				vertex(r1 * c[i + 1], r1 * s[i + 1], -z, 16, 0);
			}
			// Back
			GL11.glNormal3f(0, 0, -1);
			vertex(r1 * c[i], r1 * s[i], -z, 0, 16);
			vertex(r1 * c[i + 1], r1 * s[i + 1], -z, 16, 16);
			vertex(r2 * c[i + 1], r2 * s[i + 1], -z, 16, 0);
			vertex(r2 * c[i], r2 * s[i], -z, 0, 0);
			// Front
			GL11.glNormal3f(0, 0, 1);
			switch (type) {
			case Outer:
				selectTile(ringFaceTextureIndex);
				u = 0;
				du = 16;
				dv = 16;
				break;
			case Inner:
				selectTile(ringSymbolTextureIndex);
				u = ringSymbolTextureLength - (i + 1) * ringSymbolSegmentWidth;
				du = ringSymbolSegmentWidth;
				dv = ringSymbolTextureHeight;
				break;
			}
			vertex(r1 * c[i], r1 * s[i], z, u + du, dv);
			vertex(r2 * c[i], r2 * s[i], z, u + du, 0);
			vertex(r2 * c[i + 1], r2 * s[i + 1], z, u, 0);
			vertex(r1 * c[i + 1], r1 * s[i + 1], z, u, dv);
		}
		GL11.glEnd();
	}

	void renderChevrons(TileEntityStargateBase te) {
		GL11.glNormal3f(0, 0, 1);
		for (int i = 0; i < 7; i++) {
			GL11.glPushMatrix();
			GL11.glRotatef(45 * (i - 1), 0, 0, 1);
			chevron(6 - i < te.numEngagedChevrons);
			GL11.glPopMatrix();
		}
	}

	void chevron(boolean engaged) {
		double r1 = chevronInnerRadius;
		double r2 = chevronOuterRadius;
		double z2 = ringDepth / 2;
		double z1 = z2 + chevronDepth;
		double w1 = chevronBorderWidth;
		double w2 = w1 * 1.25;
		double x1 = r1, y1 = chevronWidth / 4;
		double x2 = r2, y2 = chevronWidth / 2;

		if (engaged)
			GL11.glTranslated(-chevronMotionDistance, 0, 0);
		GL11.glBegin(GL11.GL_QUADS);

		selectTile(chevronTextureIndex);

		// Face 1
		vertex(x2, y2, z1, 0, 2);
		vertex(x1, y1, z1, 0, 16);
		vertex(x1 + w1, y1 - w1, z1, 4, 12);
		vertex(x2, y2 - w2, z1, 4, 2);

		// Side 1
		vertex(x2, y2, z1, 0, 0);
		vertex(x2, y2, z2, 0, 4);
		vertex(x1, y1, z2, 16, 4);
		vertex(x1, y1, z1, 16, 0);

		// End 1
		vertex(x2, y2, z1, 16, 0);
		vertex(x2, y2 - w2, z1, 12, 0);
		vertex(x2, y2 - w2, z2, 12, 4);
		vertex(x2, y2, z2, 16, 4);

		// Face 2
		vertex(x1 + w1, y1 - w1, z1, 4, 12);
		vertex(x1, y1, z1, 0, 16);
		vertex(x1, -y1, z1, 16, 16);
		vertex(x1 + w1, -y1 + w1, z1, 12, 12);

		// Side 2
		vertex(x1, y1, z1, 0, 0);
		vertex(x1, y1, z2, 0, 4);
		vertex(x1, -y1, z2, 16, 4);
		vertex(x1, -y1, z1, 16, 0);

		// Face 3
		vertex(x2, -y2 + w2, z1, 12, 0);
		vertex(x1 + w1, -y1 + w1, z1, 12, 12);
		vertex(x1, -y1, z1, 16, 16);
		vertex(x2, -y2, z1, 16, 0);

		// Side 3
		vertex(x1, -y1, z1, 0, 0);
		vertex(x1, -y1, z2, 0, 4);
		vertex(x2, -y2, z2, 16, 4);
		vertex(x2, -y2, z1, 16, 0);

		// End 3
		vertex(x2, -y2, z1, 0, 0);
		vertex(x2, -y2, z2, 0, 4);
		vertex(x2, -y2 + w2, z2, 4, 4);
		vertex(x2, -y2 + w2, z1, 4, 0);

		// Back
		vertex(x2, -y2, z2, 0, 0);
		vertex(x1, -y1, z2, 0, 16);
		vertex(x1, y1, z2, 16, 16);
		vertex(x2, y2, z2, 16, 0);

		GL11.glEnd();

		selectTile(chevronLitTextureIndex);
		if (!engaged)
			GL11.glColor3d(0.5, 0.5, 0.5);
		else
			GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glBegin(GL11.GL_QUADS);

		// Face 4
		vertex(x2, y2 - w2, z1, 0, 4);
		vertex(x1 + w1, y1 - w1, z1, 4, 16);
		vertex(x1 + w1, 0, z1, 8, 16);
		vertex(x2, 0, z1, 8, 4);

		vertex(x2, 0, z1, 8, 4);
		vertex(x1 + w1, 0, z1, 8, 16);
		vertex(x1 + w1, -y1 + w1, z1, 12, 16);
		vertex(x2, -y2 + w2, z1, 16, 4);

		// End 4
		vertex(x2, y2 - w2, z2, 0, 0);
		vertex(x2, y2 - w2, z1, 0, 4);
		vertex(x2, -y2 + w2, z1, 16, 4);
		vertex(x2, -y2 + w2, z2, 16, 0);

		GL11.glColor3f(1, 1, 1);
		GL11.glEnd();
		GL11.glEnable(GL11.GL_LIGHTING);
	}

	static ResourceLocation eventHorizonTexture = new ResourceLocation("gcewing_sg", "textures/eventhorizon_"
			+ SGCraft.getProxy().getRenderMode() + ".png");

	void renderEventHorizon(TileEntityStargateBase te) {

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
		GL11.glTexCoord2d(0.5, 0.5);
		GL11.glVertex3d(0, 0, grid[1][0]);
		for (int j = 0; j <= ehGridPolarSize; j++)
			ghVertex(grid, 1, j);
		GL11.glEnd();
		GL11.glEnable(GL11.GL_CULL_FACE);
	}

	void ehVertex(double[][] grid, int i, int j) {
		double r = i * ehBandWidth;
		double x = r * c[j];
		double y = r * s[j];

		double u = (x + 2) / 4;
		double v = (y + 2) / 4;
		GL11.glTexCoord2d(u, v);
		GL11.glVertex3d(x, y, grid[j + 1][i]);
	}

	void ghVertex(double[][] grid, int i, int j) {
		double r = i * ehBandWidth;
		double x = r * c[j];
		double y = r * s[j];

		double u = (x + 2) / 4;
		double v = (y + 2) / 4;
		GL11.glTexCoord2d(u, v);
		GL11.glVertex3d(x, y, grid[j + 1][i]);
	}

	void selectTile(int index) {
		u0 = (index & 0xf) / 16.0;
		v0 = (index >> 4) / 16.0;
	}

	void vertex(double x, double y, double z, double u, double v) {
		GL11.glTexCoord2d(u0 + u * (1 / 256.0), v0 + v * (1 / 256.0));
		GL11.glVertex3d(x, y, z);
	}

}

enum RingType {
	Inner, Outer
}
