package pcl.lc.base.render.font;

import java.util.HashMap;
import java.util.Map.Entry;

import net.minecraft.client.renderer.Tessellator;

import org.lwjgl.opengl.GL11;

/**
 * Display list buffering for font renderers.
 * 
 * @author AfterLifeLochie
 * 
 */
public class FontRenderBuffer {

	/**
	 * The buffer map
	 */
	private final HashMap<Integer, Integer> charmap = new HashMap<Integer, Integer>();
	/**
	 * glGenList origin result
	 */
	private final int listOrigin;
	/**
	 * glGenList size param
	 */
	private final int listSize;

	public FontRenderBuffer(FontMetric metric) {
		this.listSize = metric.glyphs.size();
		this.listOrigin = GL11.glGenLists(listSize);
		if (this.listOrigin == 0)
			throw new RuntimeException("Lists are not available, panic!");
		int cx = listOrigin;
		for (Entry<Integer, GlyphMetric> metricData : metric.glyphs.entrySet()) {
			GlyphMetric glyph = metricData.getValue();
			GL11.glNewList(cx, GL11.GL_COMPILE);
			GL11.glPushMatrix();
			// TODO: Move texture uvx calculations to the metric container.
			drawTexturedRectUV(0, 0, glyph.width, glyph.height, glyph.ux * (1f / 418f), glyph.vy * (1f / 242f),
					glyph.width * (1f / 418f), glyph.height * (1f / 242f), 1.0);
			GL11.glPopMatrix();
			GL11.glEndList();
			charmap.put(metricData.getKey(), cx++);
		}
	}

	public void callCharacter(int c) {
		GL11.glCallList(charmap.get(c));
	}

	public void free() {
		GL11.glDeleteLists(listOrigin, listSize);
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
