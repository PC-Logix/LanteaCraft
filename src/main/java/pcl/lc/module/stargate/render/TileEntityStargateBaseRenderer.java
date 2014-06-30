package pcl.lc.module.stargate.render;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import pcl.lc.module.stargate.tile.TileEntityStargateBase;

public class TileEntityStargateBaseRenderer extends TileEntitySpecialRenderer {

	public static double sin[] = new double[StargateRenderConstants.numRingSegments + 1];
	public static double cos[] = new double[StargateRenderConstants.numRingSegments + 1];

	public final static StandardStargateRenderer standardRenderer;
	public final static AtlantisStargateRenderer atlantisRenderer;

	public final static EventHorizonRenderer horizonRenderer;

	static {
		standardRenderer = new StandardStargateRenderer();
		atlantisRenderer = new AtlantisStargateRenderer();

		horizonRenderer = new EventHorizonRenderer();

		for (int i = 0; i <= StargateRenderConstants.numRingSegments; i++) {
			double a = 2 * Math.PI * i / StargateRenderConstants.numRingSegments;
			sin[i] = Math.sin(a);
			cos[i] = Math.cos(a);
		}
	}

	public TileEntityStargateBaseRenderer() {

	}

	@Override
	public void renderTileEntityAt(TileEntity te, double x, double y, double z, float t) {
		TileEntityStargateBase tesg = (TileEntityStargateBase) te;
		if (tesg.getAsStructure().isValid()) {
			GL11.glPushMatrix();
			GL11.glEnable(GL12.GL_RESCALE_NORMAL);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			GL11.glTranslated(x + 0.5, y + 3.5, z + 0.5);
			standardRenderer.renderStargateAt(this, tesg, x, y, z, t);
			GL11.glDisable(GL12.GL_RESCALE_NORMAL);
			GL11.glPopMatrix();
		}
	}

	public void bind(ResourceLocation resource) {
		bindTexture(resource);
	}
}
