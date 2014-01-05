package pcl.lc.render.tileentity;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import pcl.lc.LanteaCraft;
import pcl.lc.render.stargate.AtlantisStargateRenderer;
import pcl.lc.render.stargate.EventHorizonRenderer;
import pcl.lc.render.stargate.StandardStargateRenderer;
import pcl.lc.tileentity.TileEntityStargateBase;

public class TileEntityStargateBaseRenderer extends TileEntitySpecialRenderer {

	public final static int numRingSegments = 38 * 2;

	public final static int ringFaceTextureIndex = 0x14;
	public final static int ringTextureIndex = 0x15;
	public final static int ringSymbolTextureIndex = 0x20;
	public final static int chevronTextureIndex = 0x05;
	public final static int chevronLitTextureIndex = 0x16;

	public final static double ringInnerRadius = 3.0;
	public final static double ringMidRadius = 3.25;
	public final static double ringOuterRadius = 3.5;
	public final static double ringDepth = 0.5;

	public final static double ringSymbolTextureLength = 38 * 8;
	public final static double ringSymbolTextureHeight = 12;
	public final static double ringSymbolSegmentWidth = ringSymbolTextureLength / numRingSegments;

	public static double sin[] = new double[numRingSegments + 1];
	public static double cos[] = new double[numRingSegments + 1];

	protected final static StandardStargateRenderer standardRenderer;
	protected final static AtlantisStargateRenderer atlantisRenderer;
	
	protected final static EventHorizonRenderer horizonRenderer;

	static {
		standardRenderer = new StandardStargateRenderer();
		atlantisRenderer = new AtlantisStargateRenderer();
		
		horizonRenderer = new EventHorizonRenderer();

		for (int i = 0; i <= numRingSegments; i++) {
			double a = 2 * Math.PI * i / numRingSegments;
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
			atlantisRenderer.renderStargateAt(this, tesg, x, y, z, t);
			GL11.glDisable(GL12.GL_RESCALE_NORMAL);
			GL11.glPopMatrix();
		}
	}

	public void bind(ResourceLocation resource) {
		bindTexture(resource);
	}
}
