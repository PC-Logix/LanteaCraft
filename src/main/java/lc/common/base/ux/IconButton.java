package lc.common.base.ux;

import lc.ResourceAccess;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;

/**
 * IconButton element. TODO: Clean up, can extend GUIButton(?) instead.
 *
 * @author AfterLifeLochie
 *
 */
public class IconButton {
	/**
	 * Draw a button on the screen
	 *
	 * @param mc
	 *            The game
	 * @param iconName
	 *            The icon name
	 * @param x
	 *            The x-coordinate
	 * @param y
	 *            The y-coordinate
	 * @param scale
	 *            The scale factor
	 * @param zLevel
	 *            The z-depth
	 */
	public static void drawButton(Minecraft mc, String iconName, int x, int y, int mx, int my, boolean down,
			double scale, float zLevel) {
		boolean inBounds = buttonHovered(x, y, mx, my, scale);
		bindAndClamp(mc, "components/button_" + ((down && inBounds) ? "down" : (inBounds) ? "hover" : "up"));
		drawTexturedRectUV(x, y, 24 * scale, 24 * scale, 0, 0, 1, 1, zLevel);
		drawIcon(mc, iconName, x, y, scale, zLevel);
	}

	public static void drawIcon(Minecraft mc, String icon, int x, int y, double scale, float z) {
		bindAndClamp(mc, "icons/" + icon);
		drawTexturedRectUV(x + 4 * scale, y + 4 * scale, 16 * scale, 16 * scale, 0, 0, 1, 1, z);
	}

	public static boolean buttonHovered(int x, int y, int mx, int my, double scale) {
		return (mx >= x && mx <= x + (24 * scale)) && (my >= y && my <= y + (24 * scale));
	}

	public static boolean buttonDepressed(int x, int y, int mx, int my, boolean down, double scale) {
		return down && buttonHovered(x, y, mx, my, scale);
	}

	private static void bindAndClamp(Minecraft mc, String name) {
		mc.getTextureManager().bindTexture(
				ResourceAccess.getNamedResource(ResourceAccess.formatResourceName("textures/gui/%s_${TEX_QUALITY}.png",
						name)));
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