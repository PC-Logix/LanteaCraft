package pcl.lc.base.render.font;

import org.lwjgl.opengl.GL11;

/**
 * Renders a written-like typeface in game using natural writing styles.
 * 
 * @author AfterLifeLochie
 */
public class WrittenFontRenderer {

	public void renderPages(FontMetric metric, FontRenderBuffer buffer, PageBox page, float ox, float oy, float z) {
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
				buffer.callCharacter((int) c);
				x += mx.width; // shunt by glpyh size
			}
			y += line.line_height; // shunt by line's height prop
		}
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glPopMatrix();
	}
}
