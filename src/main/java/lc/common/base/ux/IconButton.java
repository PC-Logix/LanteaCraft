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
	 * @param hover
	 *            If the button is hovered
	 * @param pressed
	 *            If the button is pressed
	 * @param x
	 *            The x-coordinate
	 * @param y
	 *            The y-coordinate
	 * @param scale
	 *            The scale factor
	 * @param zLevel
	 *            The z-depth
	 */
	public static void drawButton(Minecraft mc, String iconName, boolean hover, boolean pressed, int x, int y,
			double scale, float zLevel) {
		bindAndClamp(mc, "button_" + (pressed ? "down" : hover ? "hover" : "up") + ".png");
		drawTexturedRectUV(x, y, 24 * scale, 24 * scale, 0, 0, 1, 1, zLevel);
		bindAndClamp(mc, "icons/" + iconName + ".png");
		drawTexturedRectUV(x + 4 * scale, y + 4 * scale, 16 * scale, 16 * scale, 0, 0, 1, 1, zLevel);
	}

	private static void bindAndClamp(Minecraft mc, String name) {
		mc.getTextureManager().bindTexture(ResourceAccess.getNamedResource("textures/gui/" + name));
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