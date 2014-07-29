package pcl.lc.module.stargate.render;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import pcl.lc.api.EnumStargateType;
import pcl.lc.module.stargate.tile.TileStargateBase;

public class TileStargateBaseRenderer extends TileEntitySpecialRenderer {

	public static double sin[] = new double[StargateRenderConstants.numRingSegments + 1];
	public static double cos[] = new double[StargateRenderConstants.numRingSegments + 1];

	public final static StargateMechanicalRenderer mechanicalRenderer;
	public final static StargateDigitalRenderer digitalRenderer;
	public final static StargateEventHorizonRenderer horizonRenderer;

	static {
		mechanicalRenderer = new StargateMechanicalRenderer();
		digitalRenderer = new StargateDigitalRenderer();
		horizonRenderer = new StargateEventHorizonRenderer();

		for (int i = 0; i <= StargateRenderConstants.numRingSegments; i++) {
			double a = 2 * Math.PI * i / StargateRenderConstants.numRingSegments;
			sin[i] = Math.sin(a);
			cos[i] = Math.cos(a);
		}
	}

	protected static boolean before(int[] i, int j, int k) {
		if (k >= i.length)
			return true;
		for (int l = k; 0 <= l; l--)
			if (i[l] == j)
				return true;
		return false;
	}

	public TileStargateBaseRenderer() {

	}

	@Override
	public void renderTileEntityAt(TileEntity te, double x, double y, double z, float t) {
		TileStargateBase tesg = (TileStargateBase) te;
		if (tesg.getAsStructure().isValid()) {
			GL11.glPushMatrix();
			GL11.glEnable(GL12.GL_RESCALE_NORMAL);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			GL11.glTranslated(x + 0.5, y + 3.5, z + 0.5);
			EnumStargateType typeof = tesg.getType();
			if (typeof == EnumStargateType.STANDARD)
				mechanicalRenderer.renderStargateAt(this, tesg, x, y, z, t);
			if (typeof == EnumStargateType.ATLANTIS)
				digitalRenderer.renderStargateAt(this, tesg, x, y, z, t);
			GL11.glDisable(GL12.GL_RESCALE_NORMAL);
			GL11.glPopMatrix();
		}
	}

	protected void bind(ResourceLocation resource) {
		bindTexture(resource);
	}
}
