package lc.common.base.ux;

import lc.common.stargate.StargateCharsetHelper;
import lc.core.ResourceAccess;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;

public class StargateGlyphRenderer {

	private static double uscale, vscale;

	public static void drawAddress(Minecraft mc, String address, int x, int y, int length, int scale, float zLevel) {
		bindSGTexture(mc, "symbols.png", 512 / scale, 320 / scale);
		int paddingTop = 0, paddingLeft = 0;
		int borderSize = 12 / scale;
		int cellSize = 64 / scale;
		if (address.length() > 7) {
			cellSize = (int) Math.floor(448 / address.length()) / scale;
			if (448 / scale > (address.length() * cellSize)) {
				double freeLeftRight = 448 / scale - (address.length() * cellSize);
				double freeTopBottom = 64 / scale - cellSize;
				paddingLeft += (int) Math.floor(freeLeftRight / address.length());
				paddingTop += (int) Math.floor(freeTopBottom / 2);
			}
		}

		for (int i = 0; i < address.length(); i++) {
			int s = StargateCharsetHelper.singleton().index(address.charAt(i));
			double u = uscale * ((s / (length - 1)) * 32), v = vscale * ((s % (length - 1)) * 32);
			double u2 = uscale * 32, v2 = vscale * 32;
			drawTexturedRectUV(x + borderSize + i * cellSize + paddingLeft, y + borderSize + paddingTop, cellSize,
					cellSize, u, v, u2, v2, zLevel);
		}
	}

	private static void bindSGTexture(Minecraft mc, String name, int usize, int vsize) {
		mc.getTextureManager().bindTexture(ResourceAccess.getNamedResource("textures/gui/" + name));
		uscale = 1.0 / usize;
		vscale = 1.0 / vsize;
	}

	private static void drawTexturedRectUV(double x, double y, double w, double h, double u, double v, double us,
			double vs, float zLevel) {
		Tessellator tess = Tessellator.instance;
		tess.startDrawingQuads();
		tess.setColorOpaque_F(1.0f, 1.0f, 1.0f);
		tess.addVertexWithUV(x, y + h, zLevel, u, v + vs);
		tess.addVertexWithUV(x + w, y + h, zLevel, u + us, v + vs);
		tess.addVertexWithUV(x + w, y, zLevel, u + us, v);
		tess.addVertexWithUV(x, y, zLevel, u, v);
		tess.draw();
	}

}