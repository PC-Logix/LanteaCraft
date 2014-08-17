package pcl.lc.module.stargate.render;

import static pcl.lc.module.stargate.render.TileStargateBaseRenderer.cos;
import static pcl.lc.module.stargate.render.TileStargateBaseRenderer.sin;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import pcl.lc.api.EnumStargateState;
import pcl.lc.api.EnumStargateType;
import pcl.lc.core.ResourceAccess;
import pcl.lc.module.stargate.tile.TileStargateBase;

public class StargateMechanicalRenderer implements IStargateRenderer {

	private double u0, v0;
	private TileStargateBaseRenderer caller;
	private ResourceLocation stargateTex[] = new ResourceLocation[2];
	private ResourceLocation chevronTex[] = new ResourceLocation[2];

	public StargateMechanicalRenderer() {
		stargateTex[0] = ResourceAccess.getNamedResource(ResourceAccess
				.formatResourceName("textures/tileentity/stargate_${TEX_QUALITY}.png"));
		stargateTex[1] = ResourceAccess.getNamedResource(ResourceAccess
				.formatResourceName("textures/tileentity/stargate_nox_${TEX_QUALITY}.png"));
		chevronTex[0] = ResourceAccess.getNamedResource(ResourceAccess
				.formatResourceName("textures/tileentity/stargate_glyphs_${TEX_QUALITY}.png"));
		chevronTex[1] = ResourceAccess.getNamedResource(ResourceAccess
				.formatResourceName("textures/tileentity/stargate_glyphs_nox_${TEX_QUALITY}.png"));
	}

	@Override
	public void renderStargateAt(TileStargateBaseRenderer renderer, EnumStargateType type, TileStargateBase te,
			double x, double y, double z, float t) {
		caller = renderer;
		GL11.glRotatef(90 * te.getRotation(), 0, 1, 0);
		caller.bind(stargateTexFor(type));
		GL11.glNormal3f(0, 1, 0);
		renderRing(te, type);
		renderInnerRing(te, type, t);
		renderChevrons(te, type);
		if (te.isConnected())
			TileStargateBaseRenderer.horizonRenderer.renderStargateAt(renderer, type, te, x, y, z, t);
	}

	private ResourceLocation stargateTexFor(EnumStargateType type) {
		if (type == EnumStargateType.NOX)
			return stargateTex[1];
		return stargateTex[0];
	}

	private ResourceLocation chevronTexFor(EnumStargateType type) {
		if (type == EnumStargateType.NOX)
			return chevronTex[1];
		return chevronTex[0];
	}

	private void renderInnerRing(TileStargateBase te, EnumStargateType type, float t) {
		double radiusMidInner = StargateRenderConstants.ringInnerMovingRadius - (1 / 128d);
		double radiusMidOuter = StargateRenderConstants.ringMidRadius + (1 / 128d);
		double z = StargateRenderConstants.ringDepth - (1d / 128d);
		GL11.glPushMatrix();
		caller.bind(chevronTexFor(type));
		GL11.glRotatef((float) (85 + te.interpolatedRingAngle(t)), 0, 0, 1);
		GL11.glNormal3f(0, 0, 1);
		GL11.glBegin(GL11.GL_QUADS);

		selectTile(StargateRenderConstants.ringSymbolTextureIndex);
		double u = 0, du = 0, dv = 0;
		for (int i = 0; i < StargateRenderConstants.numRingSegments; i++) {
			u = i * (1.0d / StargateRenderConstants.numRingSegments);
			du = 1.0d / StargateRenderConstants.numRingSegments;
			dv = 0.66d;
			raw_vertex(radiusMidInner * cos[i], radiusMidInner * sin[i], z, u + du, dv);
			raw_vertex(radiusMidOuter * cos[i], radiusMidOuter * sin[i], z, u + du, 0);
			raw_vertex(radiusMidOuter * cos[i + 1], radiusMidOuter * sin[i + 1], z, u, 0);
			raw_vertex(radiusMidInner * cos[i + 1], radiusMidInner * sin[i + 1], z, u, dv);
		}

		GL11.glEnd();
		GL11.glPopMatrix();
		caller.bind(stargateTexFor(type));
	}

	private void renderRing(TileStargateBase te, EnumStargateType type) {
		double radiusInner = StargateRenderConstants.ringInnerRadius;
		double radiusOuter = StargateRenderConstants.ringOuterRadius;
		double radiusMidInner = StargateRenderConstants.ringInnerMovingRadius;
		double radiusMidOuter = StargateRenderConstants.ringMidRadius;

		double ringDepth = StargateRenderConstants.ringDepth;
		double bevelDepth = StargateRenderConstants.ringDepth - (1d / 16d);
		GL11.glBegin(GL11.GL_QUADS);
		for (int i = 0; i < StargateRenderConstants.numRingSegments; i++) {

			// Outside surface
			selectTile(0x4);
			GL11.glNormal3d(-cos[i], -sin[i], 0);
			vertex(radiusOuter * cos[i], radiusOuter * sin[i], ringDepth, 0, 0);
			vertex(radiusOuter * cos[i], radiusOuter * sin[i], -ringDepth, 0, 16);
			vertex(radiusOuter * cos[i + 1], radiusOuter * sin[i + 1], -ringDepth, 16, 16);
			vertex(radiusOuter * cos[i + 1], radiusOuter * sin[i + 1], ringDepth, 16, 0);

			// Outside ring inside filler (shear prevention chevron layer)
			selectTile(StargateRenderConstants.ringFaceTextureIndex);
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
			selectTile(StargateRenderConstants.ringFaceTextureIndex);
			vertex(radiusMidInner * cos[i], radiusMidInner * sin[i], ringDepth, 0, 0);
			vertex(radiusMidInner * cos[i], radiusMidInner * sin[i], -ringDepth, 0, 16);
			vertex(radiusMidInner * cos[i + 1], radiusMidInner * sin[i + 1], -ringDepth, 16, 16);
			vertex(radiusMidInner * cos[i + 1], radiusMidInner * sin[i + 1], ringDepth, 16, 0);

			// Back
			GL11.glNormal3f(0, 0, -1);
			selectTile(StargateRenderConstants.ringFaceTextureIndex);

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
			selectTile(StargateRenderConstants.ringFaceTextureIndex);

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

	private void renderChevrons(TileStargateBase te, EnumStargateType type) {
		GL11.glNormal3f(0, 0, 1);
		int sizeof = (te.getDialledAddress() != null) ? te.getDialledAddress().length() : -1;
		int[] renderQueue = (sizeof == 7) ? StargateRenderConstants.standardRenderQueue
				: StargateRenderConstants.extendedRenderQueue;
		for (int i = 0; i < 9; i++) {
			GL11.glPushMatrix();
			GL11.glRotatef(
					(float) ((StargateRenderConstants.chevronAngle * i) - StargateRenderConstants.chevronAngleOffset),
					0, 0, 1);
			chevron((sizeof != -1) && TileStargateBaseRenderer.before(renderQueue, i, te.getEncodedChevrons()), i == 0
					&& te.getState() == EnumStargateState.InterDialling);
			GL11.glPopMatrix();
		}
	}

	private void chevron(boolean engaged, boolean moved) {
		double r1 = StargateRenderConstants.chevronInnerRadius - (1d / 18d);
		double r2 = StargateRenderConstants.chevronOuterRadius;
		double z2 = StargateRenderConstants.ringDepth - (1d / 32d);

		double z1 = z2 + StargateRenderConstants.chevronDepth;
		double w1 = StargateRenderConstants.chevronBorderWidth;
		double w2 = w1 * 1.25;

		double x1 = r1, y1 = StargateRenderConstants.chevronWidth / 4;
		double x2 = r2, y2 = StargateRenderConstants.chevronWidth / 2;

		if (moved)
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

	private void raw_vertex(double x, double y, double z, double u, double v) {
		GL11.glTexCoord2d(u, v);
		GL11.glVertex3d(x, y, z);
	}

	private void vertex(double x, double y, double z, double u, double v) {
		GL11.glTexCoord2d(u0 + u * (1 / 256.0), v0 + v * (1 / 256.0));
		GL11.glVertex3d(x, y, z);
	}

}
