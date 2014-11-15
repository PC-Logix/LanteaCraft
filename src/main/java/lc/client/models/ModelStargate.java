package lc.client.models;

import static lc.client.models.ModelStargate.ModelStargateConstants.cos;
import static lc.client.models.ModelStargate.ModelStargateConstants.sin;
import static lc.client.opengl.GLHelper.pushTexVertex;
import lc.client.TileStargateBaseRenderer;
import lc.client.opengl.BufferDisplayList;
import lc.common.base.LCTileRenderHook;
import lc.common.util.math.Orientations;
import lc.tiles.TileStargateBase;

import org.lwjgl.opengl.GL11;

/**
 * Stargate model factory
 *
 * @author AfterLifeLochie
 *
 */
public class ModelStargate {

	public static class ModelStargateConstants {
		public final static int numRingSegments = 38;
		public final static double ringSymbolAngle = 360.0 / 38;
		public final static double ringSymbolTextureLength = 38 * 8;
		public final static double ringSymbolTextureHeight = 12;
		public final static double ringSymbolSegmentWidth = ringSymbolTextureLength / numRingSegments;

		public final static double ringInnerRadius = 3.0;
		public final static double ringInnerMovingRadius = ringInnerRadius + 0.1;
		public final static double ringOuterRadius = 3.5;
		public final static double ringMidRadius = ringInnerMovingRadius + (ringOuterRadius - ringInnerMovingRadius)
				/ 2;

		public final static double ringDepth = 0.15;

		public final static int numChevrons = 9;
		public final static double chevronAngle = 360.0 / numChevrons;
		public final static double chevronAngleOffset = -90.0;
		public final static double chevronInnerRadius = ringMidRadius;
		public final static double chevronOuterRadius = ringOuterRadius + 1 / 16.0;
		public final static double chevronWidth = 0.5;
		public final static double chevronDepth = 0.0625d;
		public final static double chevronBorderWidth = chevronWidth / 6;
		public final static double chevronMotionDistance = 1 / 16.0d;

		public final static int ringFaceTextureIndex = 0x14;
		public final static int ringTextureIndex = 0x15;
		public final static int ringSymbolTextureIndex = 0x20;
		public final static int chevronTextureIndex = 0x05;
		public final static int chevronLitTextureIndex = 0x16;

		public static double sin[] = new double[numRingSegments + 1];
		public static double cos[] = new double[numRingSegments + 1];

		static {
			for (int i = 0; i <= numRingSegments; i++) {
				double a = 2 * Math.PI * i / numRingSegments;
				sin[i] = Math.sin(a);
				cos[i] = Math.cos(a);
			}
		}

	}

	/** Display buffer for outer shell */
	private final BufferDisplayList listShell = new BufferDisplayList();
	/** Display buffer for chevron */
	private final BufferDisplayList listChevron = new BufferDisplayList();
	/** Display buffer for lit chevron */
	private final BufferDisplayList listLitChevron = new BufferDisplayList();
	/** Display buffer for inner ring */
	private final BufferDisplayList listRing = new BufferDisplayList();

	private double u0, v0;

	public ModelStargate() {

	}

	public void init() {
		listShell.init();
		listChevron.init();
		listLitChevron.init();
		listRing.init();

		listShell.enter();
		renderShellImmediate();
		listShell.exit();

		listChevron.enter();
		renderChevronImmediate(false);
		listChevron.exit();

		listLitChevron.enter();
		renderChevronImmediate(true);
		listLitChevron.exit();

		listRing.enter();
		renderRingImmediate();
		listRing.exit();
	}

	public void render(TileStargateBaseRenderer renderer, LCTileRenderHook tesr, TileStargateBase tile) {
		GL11.glRotatef(Orientations.from(tile.getRotation()).angle(), 0, 1, 0);
		tesr.bind(renderer.texture);
		listShell.bind();
		listShell.release();
	}

	private void renderShellImmediate() {
		double radiusInner = ModelStargateConstants.ringInnerRadius;
		double radiusOuter = ModelStargateConstants.ringOuterRadius;
		double radiusMidInner = ModelStargateConstants.ringInnerMovingRadius;
		double radiusMidOuter = ModelStargateConstants.ringMidRadius;
		double ringDepth = ModelStargateConstants.ringDepth;
		double bevelDepth = ModelStargateConstants.ringDepth - 1d / 16d;
		GL11.glNormal3f(0, 1, 0);
		GL11.glBegin(GL11.GL_QUADS);
		for (int i = 0; i < ModelStargateConstants.numRingSegments; i++) {

			// Outside surface
			selectTile(0x4);
			GL11.glNormal3d(-cos[i], -sin[i], 0);
			vertex(radiusOuter * cos[i], radiusOuter * sin[i], ringDepth, 0, 0);
			vertex(radiusOuter * cos[i], radiusOuter * sin[i], -ringDepth, 0, 16);
			vertex(radiusOuter * cos[i + 1], radiusOuter * sin[i + 1], -ringDepth, 16, 16);
			vertex(radiusOuter * cos[i + 1], radiusOuter * sin[i + 1], ringDepth, 16, 0);

			// Outside ring inside filler (shear prevention chevron layer)
			selectTile(ModelStargateConstants.ringFaceTextureIndex);
			vertex(radiusMidOuter * cos[i], radiusMidOuter * sin[i], ringDepth, 0, 0);
			vertex(radiusMidOuter * cos[i + 1], radiusMidOuter * sin[i + 1], ringDepth, 16, 0);
			vertex(radiusMidOuter * cos[i + 1], radiusMidOuter * sin[i + 1], -ringDepth, 16, 16);
			vertex(radiusMidOuter * cos[i], radiusMidOuter * sin[i], -ringDepth, 0, 16);

			// Inside surface
			selectTile(0x17);
			GL11.glNormal3d(-cos[i], -sin[i], 0);
			vertex(radiusInner * cos[i], radiusInner * sin[i], bevelDepth, 0, 0);
			vertex(radiusInner * cos[i + 1], radiusInner * sin[i + 1], bevelDepth, 16, 0);
			vertex(radiusInner * cos[i + 1], radiusInner * sin[i + 1], -bevelDepth, 16, 16);
			vertex(radiusInner * cos[i], radiusInner * sin[i], -bevelDepth, 0, 16);

			// Inside ring inside filler (shear prevention chevron layer)
			selectTile(ModelStargateConstants.ringFaceTextureIndex);
			vertex(radiusMidInner * cos[i], radiusMidInner * sin[i], ringDepth, 0, 0);
			vertex(radiusMidInner * cos[i], radiusMidInner * sin[i], -ringDepth, 0, 16);
			vertex(radiusMidInner * cos[i + 1], radiusMidInner * sin[i + 1], -ringDepth, 16, 16);
			vertex(radiusMidInner * cos[i + 1], radiusMidInner * sin[i + 1], ringDepth, 16, 0);

			// Back
			GL11.glNormal3f(0, 0, -1);
			selectTile(ModelStargateConstants.ringFaceTextureIndex);

			// Inner back flat outer ring
			vertex(radiusInner * cos[i], radiusInner * sin[i], -bevelDepth, 0, 16);
			vertex(radiusInner * cos[i + 1], radiusInner * sin[i + 1], -bevelDepth, 16, 16);
			vertex(radiusMidInner * cos[i + 1], radiusMidInner * sin[i + 1], -ringDepth, 16, 0);
			vertex(radiusMidInner * cos[i], radiusMidInner * sin[i], -ringDepth, 0, 0);

			// Outer back bevel inner ring
			vertex(radiusMidInner * cos[i], radiusMidInner * sin[i], -ringDepth, 0, 16);
			vertex(radiusMidInner * cos[i + 1], radiusMidInner * sin[i + 1], -ringDepth, 16, 16);
			vertex(radiusOuter * cos[i + 1], radiusOuter * sin[i + 1], -ringDepth, 16, 0);
			vertex(radiusOuter * cos[i], radiusOuter * sin[i], -ringDepth, 0, 0);

			// Front
			GL11.glNormal3f(0, 0, 1);
			selectTile(ModelStargateConstants.ringFaceTextureIndex);

			// Inner front flat inner ring
			GL11.glNormal3f(0, 0, 1);
			vertex(radiusInner * cos[i], radiusInner * sin[i], bevelDepth, 16, 16);
			vertex(radiusMidInner * cos[i], radiusMidInner * sin[i], ringDepth, 16, 0);
			vertex(radiusMidInner * cos[i + 1], radiusMidInner * sin[i + 1], ringDepth, 0, 0);
			vertex(radiusInner * cos[i + 1], radiusInner * sin[i + 1], bevelDepth, 0, 16);

			// Outer front flat outer ring
			vertex(radiusMidOuter * cos[i], radiusMidOuter * sin[i], ringDepth, 16, 16);
			vertex(radiusOuter * cos[i], radiusOuter * sin[i], ringDepth, 16, 0);
			vertex(radiusOuter * cos[i + 1], radiusOuter * sin[i + 1], ringDepth, 0, 0);
			vertex(radiusMidOuter * cos[i + 1], radiusMidOuter * sin[i + 1], ringDepth, 0, 16);
		}
		GL11.glEnd();
	}

	public void renderChevronImmediate(boolean lit) {
		double r1 = ModelStargateConstants.chevronInnerRadius - 1d / 18d;
		double r2 = ModelStargateConstants.chevronOuterRadius;
		double z2 = ModelStargateConstants.ringDepth - 1d / 32d;

		double z1 = z2 + ModelStargateConstants.chevronDepth;
		double w1 = ModelStargateConstants.chevronBorderWidth;
		double w2 = w1 * 1.25;

		double x1 = r1, y1 = ModelStargateConstants.chevronWidth / 4;
		double x2 = r2, y2 = ModelStargateConstants.chevronWidth / 2;

		GL11.glBegin(GL11.GL_QUADS);

		selectTile(ModelStargateConstants.chevronTextureIndex);

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

		selectTile(ModelStargateConstants.chevronLitTextureIndex);
		if (!lit)
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

	private void renderRingImmediate() {
		double radiusMidInner = ModelStargateConstants.ringInnerMovingRadius - 1 / 128d;
		double radiusMidOuter = ModelStargateConstants.ringMidRadius + 1 / 128d;
		double z = ModelStargateConstants.ringDepth - 1d / 128d;
		GL11.glNormal3f(0, 0, 1);
		GL11.glBegin(GL11.GL_QUADS);
		selectTile(ModelStargateConstants.ringSymbolTextureIndex);
		double u = 0, du = 0, dv = 0;
		for (int i = 0; i < ModelStargateConstants.numRingSegments; i++) {
			u = i * (1.0d / ModelStargateConstants.numRingSegments);
			du = 1.0d / ModelStargateConstants.numRingSegments;
			dv = 0.66d;
			pushTexVertex(radiusMidInner * cos[i], radiusMidInner * sin[i], z, u + du, dv);
			pushTexVertex(radiusMidOuter * cos[i], radiusMidOuter * sin[i], z, u + du, 0);
			pushTexVertex(radiusMidOuter * cos[i + 1], radiusMidOuter * sin[i + 1], z, u, 0);
			pushTexVertex(radiusMidInner * cos[i + 1], radiusMidInner * sin[i + 1], z, u, dv);
		}
		GL11.glEnd();
	}

	private void selectTile(int index) {
		u0 = (index & 0xf) / 16.0;
		v0 = (index >> 4) / 16.0;
	}

	private void vertex(double x, double y, double z, double u, double v) {
		pushTexVertex(x, y, z, u0 + u * (1 / 256.0), v0 + v * (1 / 256.0));
	}
}
