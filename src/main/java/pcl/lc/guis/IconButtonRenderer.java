package pcl.lc.guis;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import pcl.lc.LanteaCraft;

public class IconButtonRenderer {
	public static void drawButton(Minecraft mc, String iconName, boolean hover, boolean pressed, int x, int y, double scale,
			float zLevel) {
		bindAndClamp(mc, "button-" + ((pressed) ? "down" : (hover) ? "hover" : "up") + ".png");
		drawTexturedRectUV(x, y, 24 * scale, 24 * scale, 0, 0, 1, 1, zLevel);
		bindAndClamp(mc, "icons/" + iconName + ".png");
		drawTexturedRectUV(x + (4 * scale), y + (4 * scale), 16 * scale, 16 * scale, 0, 0, 1, 1, zLevel);
	}

	private static void bindAndClamp(Minecraft mc, String name) {
		mc.getTextureManager().bindTexture(LanteaCraft.getResource("textures/gui/" + name));
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
