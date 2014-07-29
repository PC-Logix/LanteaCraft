package pcl.lc.base.render.font;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import org.lwjgl.opengl.GL11;

/**
 * Renders a written-like typeface in game using natural writing styles.
 * 
 * @author AfterLifeLochie
 */
public class WrittenFontRenderer {

	/**
	 * Render a page box to the screen
	 * 
	 * @param pages
	 *            The pages
	 */
	public void renderPages(FontMetric metric, PageBox page, float ox, float oy, float z) {
		Minecraft.getMinecraft().getTextureManager().bindTexture(metric.fontImageName);
		float x = 0, y = 0;
		GL11.glPushMatrix();
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA);
		// Translate to the draw dest
		GL11.glTranslatef(ox, oy, 0.0f);
		for (LineBox line : page.lines) {
			x = 0; // carriage return
			for (int i = 0; i < line.line.length(); i++) {
				char c = line.line.charAt(i);
				if (c == ' ') // is a space?
					x += line.space_size; // shunt by a space
				GlyphMetric mx = metric.glyphs.get((int) c);
				if (mx == null) // blank glyph?
					continue;
				drawTexturedRectUV(x, y, mx.width, mx.height, mx.ux * (1f / 418f), mx.vy * (1f / 242f), mx.width
						* (1f / 418f), mx.height * (1f / 242f), z);
				x += mx.width; // shunt by glpyh size
			}
			y += line.line_height; // shunt by line's height prop
		}
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glPopMatrix();
	}

	public void drawTexturedRectUV(double x, double y, double w, double h, double u, double v, double us, double vs,
			double zLevel) {
		Tessellator tess = Tessellator.instance;
		GL11.glPushMatrix();
		GL11.glScalef(0.45f, 0.45f, 1.0f);
		tess.startDrawingQuads();
		tess.setColorOpaque_F(1.0f, 1.0f, 1.0f);
		tess.addVertexWithUV(x, y + h, zLevel, u, v + vs);
		tess.addVertexWithUV(x + w, y + h, zLevel, u + us, v + vs);
		tess.addVertexWithUV(x + w, y, zLevel, u + us, v);
		tess.addVertexWithUV(x, y, zLevel, u, v);
		tess.draw();
		GL11.glPopMatrix();
	}
}
