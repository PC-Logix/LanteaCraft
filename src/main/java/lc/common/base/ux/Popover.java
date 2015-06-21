package lc.common.base.ux;

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.commons.lang3.text.WordUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.google.common.collect.Lists;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.RenderHelper;

/**
 * Popover GUI element
 * 
 * @author AfterLifeLochie
 *
 */
public class Popover {

	private String message;
	private int age;
	private int expiry;

	/**
	 * Create a popover
	 * 
	 * @param message
	 *            The message text
	 * @param expiry
	 *            The expiry time
	 */
	public Popover(String message, int expiry) {
		this.message = message;
		this.expiry = expiry;
	}

	/**
	 * Render the popover
	 * 
	 * @param mc
	 *            The game
	 * @param gui
	 *            The GUI
	 * @param x
	 *            The x-coordinate of the popover
	 * @param y
	 *            The y-coordinate of the popover
	 * @param width
	 *            The width
	 * @param zLevel
	 *            The z-depth
	 * @return The height of the popover
	 */
	public int render(Minecraft mc, LCContainerGUI gui, int x, int y, int width, float zLevel) {
		float remain = 1.0f;
		if (age > 0.5f * expiry)
			remain = Math.max(0.001f, (expiry - age) / (0.5f * (expiry + 1)));
		return Popover.drawPopover(mc, gui, message, x, y, remain, width, zLevel);
	}

	/** Advance the popover */
	public void tick() {
		age++;
	}

	/**
	 * Check if the popover has expired
	 * 
	 * @return If the popover is dead
	 */
	public boolean dead() {
		return age > expiry;
	}

	/**
	 * Draws a popover
	 * 
	 * @param mc
	 *            The game
	 * @param gui
	 *            The GUI
	 * @param text
	 *            The text
	 * @param x
	 *            The x-coordinate
	 * @param y
	 *            The y-coordinate
	 * @param remain
	 *            The remaining time
	 * @param width
	 *            The width of the box
	 * @param zLevel
	 *            The z-depth
	 * @return The height used
	 */
	public static int drawPopover(Minecraft mc, LCContainerGUI gui, String text, int x, int y, float remain, int width,
			float zLevel) {
		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		RenderHelper.disableStandardItemLighting();
		GL11.glDisable(GL11.GL_LIGHTING);
		int boxWidth = 0;
		FontRenderer font = mc.fontRenderer;

		GL11.glColor4f(1.0f, 1.0f, 1.0f, remain);

		ArrayList<String> lines = Lists.newArrayList(WordUtils.wrap(text, width).split("\n"));
		Iterator<String> iterator = lines.iterator();
		while (iterator.hasNext()) {
			String s = (String) iterator.next();
			int l = font.getStringWidth(s);
			if (l > boxWidth)
				boxWidth = l;
		}

		int x0 = x - (boxWidth / 2);
		int y0 = y;
		int height = 8;

		if (lines.size() > 1)
			height += 2 + (lines.size() - 1) * 10;
		int j1 = 0x00100010 | ((int) Math.ceil(remain * 0xF0) << 24);

		gui.drawGradientRectUV(x0 - 3, y0 - 4, x0 + boxWidth + 3, y0 - 3, j1, j1);
		gui.drawGradientRectUV(x0 - 3, y0 + height + 3, x0 + boxWidth + 3, y0 + height + 4, j1, j1);
		gui.drawGradientRectUV(x0 - 3, y0 - 3, x0 + boxWidth + 3, y0 + height + 3, j1, j1);
		gui.drawGradientRectUV(x0 - 4, y0 - 3, x0 - 3, y0 + height + 3, j1, j1);
		gui.drawGradientRectUV(x0 + boxWidth + 3, y0 - 3, x0 + boxWidth + 4, y0 + height + 3, j1, j1);

		int k1 = 0x005000FF | ((int) Math.ceil(remain * 0x50) << 24);
		int l1 = (k1 & 0xFEFEFE) >> 1 | k1 & 0xFF000000;

		gui.drawGradientRectUV(x0 - 3, y0 - 3 + 1, x0 - 3 + 1, y0 + height + 3 - 1, k1, l1);
		gui.drawGradientRectUV(x0 + boxWidth + 2, y0 - 3 + 1, x0 + boxWidth + 3, y0 + height + 3 - 1, k1, l1);
		gui.drawGradientRectUV(x0 - 3, y0 - 3, x0 + boxWidth + 3, y0 - 3 + 1, k1, k1);
		gui.drawGradientRectUV(x0 - 3, y0 + height + 2, x0 + boxWidth + 3, y0 + height + 3, l1, l1);

		for (int i2 = 0; i2 < lines.size(); ++i2) {
			String s1 = lines.get(i2);
			int col = 0xFFFFFF | ((int) (remain * 0x50) << 24);
			font.drawStringWithShadow(s1, x0, y0, col);
			if (i2 == 0)
				y0 += 2;
			y0 += 10;
		}
		GL11.glEnable(GL11.GL_LIGHTING);
		RenderHelper.enableStandardItemLighting();
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		return height + 8;
	}

}
