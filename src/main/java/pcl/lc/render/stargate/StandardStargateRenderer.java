package pcl.lc.render.stargate;

import static pcl.lc.render.tileentity.TileEntityStargateBaseRenderer.cos;
import static pcl.lc.render.tileentity.TileEntityStargateBaseRenderer.sin;

import org.lwjgl.opengl.GL11;

import pcl.lc.LanteaCraft;
import pcl.lc.render.tileentity.TileEntityStargateBaseRenderer;
import pcl.lc.tileentity.TileEntityStargateBase;

public class StandardStargateRenderer implements IStargateRenderer {

	private double u0, v0;
	private TileEntityStargateBaseRenderer caller;

	@Override
	public void renderStargateAt(TileEntityStargateBaseRenderer renderer, TileEntityStargateBase te, double x,
			double y, double z, float t) {
		caller = renderer;
		GL11.glRotatef(90 * te.getRotation(), 0, 1, 0);
		caller.bind(LanteaCraft.getResource("textures/tileentity/stargate_128.png"));
		GL11.glNormal3f(0, 1, 0);
		renderRing(StargateRenderConstants.ringInnerRadius, StargateRenderConstants.ringOuterRadius, false);
		renderInnerRing(te, t);
		renderChevrons(te);
		if (te.isConnected())
			TileEntityStargateBaseRenderer.horizonRenderer.renderStargateAt(renderer, te, x, y, z, t);
	}

	private void renderInnerRing(TileEntityStargateBase te, float t) {
		GL11.glPushMatrix();
		GL11.glRotatef((float) (95 + te.interpolatedRingAngle(t)), 0, 0, 1);
		double dvt = 1d / 8d;
		double r1 = StargateRenderConstants.ringInnerRadius + dvt, r2 = StargateRenderConstants.ringMidRadius + dvt;
		GL11.glNormal3f(0, 0, 1);
		GL11.glBegin(GL11.GL_QUADS);

		selectTile(StargateRenderConstants.ringSymbolTextureIndex);
		double u = 0, du = 0, dv = 0;
		for (int i = 0; i < StargateRenderConstants.numRingSegments; i++) {
			double z = StargateRenderConstants.ringDepth / 2 - (1d / 16d);
			u = StargateRenderConstants.ringSymbolTextureLength - (i + 1)
					* StargateRenderConstants.ringSymbolSegmentWidth;
			du = StargateRenderConstants.ringSymbolSegmentWidth;
			dv = StargateRenderConstants.ringSymbolTextureHeight;

			vertex(r1 * cos[i], r1 * sin[i], z, u + du, dv);
			vertex(r2 * cos[i], r2 * sin[i], z, u + du, 0);
			vertex(r2 * cos[i + 1], r2 * sin[i + 1], z, u, 0);
			vertex(r1 * cos[i + 1], r1 * sin[i + 1], z, u, dv);
		}

		GL11.glEnd();
		GL11.glPopMatrix();
	}

	private void renderRing(double r1, double r2, boolean isInnerRing) {
		double z = StargateRenderConstants.ringDepth / 2;
		double u = 0, du = 0, dv = 0;
		GL11.glBegin(GL11.GL_QUADS);
		for (int i = 0; i < StargateRenderConstants.numRingSegments; i++) {

			// Insides & Outsides
			selectTile(0x4);
			GL11.glNormal3d(-cos[i], -sin[i], 0);
			vertex(r2 * cos[i], r2 * sin[i], z, 0, 0);
			vertex(r2 * cos[i], r2 * sin[i], -z, 0, 16);
			vertex(r2 * cos[i + 1], r2 * sin[i + 1], -z, 16, 16);
			vertex(r2 * cos[i + 1], r2 * sin[i + 1], z, 16, 0);

			selectTile(0x17);
			GL11.glNormal3d(-cos[i], -sin[i], 0);
			vertex(r1 * cos[i], r1 * sin[i], z, 0, 0);
			vertex(r1 * cos[i + 1], r1 * sin[i + 1], z, 16, 0);
			vertex(r1 * cos[i + 1], r1 * sin[i + 1], -z, 16, 16);
			vertex(r1 * cos[i], r1 * sin[i], -z, 0, 16);

			// Back
			selectTile(StargateRenderConstants.ringFaceTextureIndex);
			GL11.glNormal3f(0, 0, -1);
			vertex(r1 * cos[i], r1 * sin[i], -z, 0, 16);
			vertex(r1 * cos[i + 1], r1 * sin[i + 1], -z, 16, 16);
			vertex(r2 * cos[i + 1], r2 * sin[i + 1], -z, 16, 0);
			vertex(r2 * cos[i], r2 * sin[i], -z, 0, 0);

			// Front
			GL11.glNormal3f(0, 0, 1);
			selectTile(StargateRenderConstants.ringFaceTextureIndex);
			u = 0;
			du = 16;
			dv = 16;
			double dxt = 1d / 8d;
			double r3 = r1 + dxt + 0.25, r4 = r1 + dxt;

			GL11.glNormal3d(-cos[i], -sin[i], 0);
			vertex(r4 * cos[i], r4 * sin[i], z, 0, 0);
			vertex(r4 * cos[i], r4 * sin[i], -z, 0, 16);
			vertex(r4 * cos[i + 1], r4 * sin[i + 1], -z, 16, 16);
			vertex(r4 * cos[i + 1], r4 * sin[i + 1], z, 16, 0);
			
			GL11.glNormal3f(0, 0, 1);
			vertex(r1 * cos[i], r1 * sin[i], z, u + du, dv);
			vertex(r4 * cos[i], r4 * sin[i], z, u + du, 0);
			vertex(r4 * cos[i + 1], r4 * sin[i + 1], z, u, 0);
			vertex(r1 * cos[i + 1], r1 * sin[i + 1], z, u, dv);
			
			GL11.glNormal3d(-cos[i], -sin[i], 0);
			vertex(r3 * cos[i], r3 * sin[i], z, 0, 0);
			vertex(r3 * cos[i + 1], r3 * sin[i + 1], z, 16, 0);
			vertex(r3 * cos[i + 1], r3 * sin[i + 1], -z, 16, 16);
			vertex(r3 * cos[i], r3 * sin[i], -z, 0, 16);
			
			GL11.glNormal3f(0, 0, 1);
			vertex(r3 * cos[i], r3 * sin[i], z, u + du, dv);
			vertex(r2 * cos[i], r2 * sin[i], z, u + du, 0);
			vertex(r2 * cos[i + 1], r2 * sin[i + 1], z, u, 0);
			vertex(r3 * cos[i + 1], r3 * sin[i + 1], z, u, dv);
		}
		GL11.glEnd();
	}

	private void renderChevrons(TileEntityStargateBase te) {
		GL11.glNormal3f(0, 0, 1);
		int sizeof = (te.getDialledAddres() != null) ? te.getDialledAddres().length() : -1;
		int[] renderQueue = (sizeof == 7) ? StargateRenderConstants.standardRenderQueue
				: StargateRenderConstants.extendedRenderQueue;
		for (int i = 0; i < 9; i++) {
			GL11.glPushMatrix();
			GL11.glRotatef(90 - (40 * i), 0, 0, 1);
			chevron((sizeof != -1) && before(renderQueue, i, te.getEncodedChevrons()));
			GL11.glPopMatrix();
		}
	}

	private boolean before(int[] i, int j, int k) {
		if (k >= i.length)
			return true;
		for (int l = k; 0 <= l; l--)
			if (i[l] == j)
				return true;
		return false;
	}

	private void chevron(boolean engaged) {
		double r1 = StargateRenderConstants.chevronInnerRadius;
		double r2 = StargateRenderConstants.chevronOuterRadius;
		double z2 = StargateRenderConstants.ringDepth / 2;
		double z1 = z2 + StargateRenderConstants.chevronDepth;
		double w1 = StargateRenderConstants.chevronBorderWidth;
		double w2 = w1 * 1.25;
		double x1 = r1, y1 = StargateRenderConstants.chevronWidth / 4;
		double x2 = r2, y2 = StargateRenderConstants.chevronWidth / 2;

		if (engaged)
			GL11.glTranslated(-StargateRenderConstants.chevronMotionDistance, 0, 0);
		GL11.glBegin(GL11.GL_QUADS);

		selectTile(StargateRenderConstants.chevronTextureIndex);

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

		selectTile(StargateRenderConstants.chevronLitTextureIndex);
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

	private void selectTile(int index) {
		u0 = (index & 0xf) / 16.0;
		v0 = (index >> 4) / 16.0;
	}

	private void vertex(double x, double y, double z, double u, double v) {
		GL11.glTexCoord2d(u0 + u * (1 / 256.0), v0 + v * (1 / 256.0));
		GL11.glVertex3d(x, y, z);
	}

}
