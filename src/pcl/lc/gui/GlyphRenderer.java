package pcl.lc.gui;

import java.awt.Container;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import pcl.lc.LanteaCraft;
import pcl.lc.base.GenericContainer;
import pcl.lc.base.GenericContainerGUI;
import pcl.lc.core.GateAddressHelper;

public class GlyphRenderer {

	private static double uscale, vscale;

	public static void drawAddress(Minecraft mc, String address, int x, int y, int length, int scale, float zLevel) {
		bindSGTexture(mc, "symbols.png", 512 / scale, 256 / scale);
		int borderSize = 12 / scale;
		int cellSize = 64 / scale;
		int n = address.length();
		for (int i = 0; i < n; i++) {
			int s = GateAddressHelper.charToSymbol(address.charAt(i));
			int row = s / length;
			int col = s % length;
			drawTexturedRect(x + borderSize + i * cellSize, y + borderSize, cellSize, cellSize, col * cellSize, row
					* cellSize, zLevel);
		}
	}

	private static void bindSGTexture(Minecraft mc, String name, int usize, int vsize) {
		mc.getTextureManager().bindTexture(LanteaCraft.getInstance().getResource("textures/gui/" + name));
		uscale = 1.0 / usize;
		vscale = 1.0 / vsize;
	}

	private static void drawTexturedRect(double x, double y, double w, double h, double u, double v, float zLevel) {
		drawTexturedRect(x, y, w, h, u, v, w, h, zLevel);
	}

	private static void drawTexturedRect(double x, double y, double w, double h, double u, double v, double us,
			double vs, float zLevel) {
		drawTexturedRectUV(x, y, w, h, u * uscale, v * vscale, us * uscale, vs * vscale, zLevel);
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
