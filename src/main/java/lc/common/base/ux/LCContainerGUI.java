package lc.common.base.ux;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import com.google.common.collect.Lists;

import lc.api.stargate.StargateType;
import lc.client.opengl.BufferTexture;
import lc.common.LCLog;
import lc.common.resource.ResourceAccess;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

/**
 * Internal base container GUI class.
 *
 * @author AfterLifeLochie
 *
 */
public abstract class LCContainerGUI extends GuiContainer {

	private final static int defaultTextColor = 0x404040;
	private LCContainerTab activeTab;
	private ArrayList<Popover> popovers;
	private TileEntity tile;
	private BufferTexture tabVboBuffer;

	double uscale, vscale;
	float red = 1.0F, green = 1.0F, blue = 1.0F;
	int textColor = defaultTextColor;
	boolean textShadow = false;
	boolean mouseDown = false;

	/**
	 * Create a new container GUI
	 * 
	 * @param tile
	 *            The parent tile
	 * @param container
	 *            The parent container
	 */
	public LCContainerGUI(TileEntity tile, Container container) {
		super(container);
		this.tile = tile;
		tabVboBuffer = new BufferTexture(Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
		popovers = new ArrayList<Popover>();
		tabVboBuffer.init();
	}

	/**
	 * Get a HashMap of all tabs in the interface.
	 *
	 * @return A list of all tabs in the interface.
	 */
	protected abstract HashMap<Integer, LCContainerTab> getTabs();

	/**
	 * Switch to a tab
	 *
	 * @param to
	 *            The tab number.
	 */
	public void switchTab(int to) {
		LCContainerTab nextTab = getTabs().get(to);
		if (nextTab == activeTab)
			return;
		if (nextTab == null)
			nextTab = getTabs().get(0);
		if (activeTab != null)
			activeTab.onTabClosed(this);
		activeTab = nextTab;
		mouseDown = false;
		rebuildLayout();
		activeTab.onTabOpened(this);
	}

	/**
	 * Rebuild the interface's layout.
	 */
	public void rebuildLayout() {
		Dimension size = activeTab.getTabDimensions();
		if (size == null) {
			size = new Dimension(800, 600);
			LCLog.warn("Problem rendering tab %s: no tab size!", activeTab);
		}
		xSize = size.width;
		ySize = size.height;
		guiLeft = (width - xSize) / 2;
		guiTop = (height - ySize) / 2;
	}

	/**
	 * Get the layout's x-dimension.
	 * 
	 * @return The x-size
	 */
	public int getXSize() {
		return xSize;
	}

	/**
	 * Get the layout's y-dimension.
	 * 
	 * @return The y-size
	 */
	public int getYSize() {
		return ySize;
	}

	public int offsetLeft() {
		return guiLeft;
	}

	public int offsetTop() {
		return guiTop;
	}

	@Override
	public void setWorldAndResolution(Minecraft mc, int w, int h) {
		super.setWorldAndResolution(mc, w, h);
		rebuildLayout();
	}

	@Override
	public void updateScreen() {
		super.updateScreen();
		if (activeTab != null)
			activeTab.update(this);
		Iterator<Popover> i = popovers.iterator();
		while (i.hasNext()) {
			Popover what = i.next();
			what.tick();
			if (what.dead())
				i.remove();
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTickCount, int mouseX, int mouseY) {
		GL11.glEnable(GL11.GL_BLEND);
		if (activeTab != null) {
			GL11.glPushMatrix();
			GL11.glTranslatef((float) guiLeft, (float) guiTop, 0.0F);
			activeTab.drawBackgroundLayer(this, partialTickCount, mouseX, mouseY);
			GL11.glPopMatrix();
		}

		GL11.glPushMatrix();

		float stop = guiLeft + xSize;
		boolean tips = (mouseX > stop && mouseX < stop + 16);
		GL11.glTranslatef(stop, (float) guiTop, 0.0F);
		GL11.glDisable(GL11.GL_LIGHTING);
		for (int i = 0; i < getTabs().size(); i++) {
			int y = (i * 16);
			LCContainerTab tab = getTabs().get(i);
			ResourceLocation ico = tab.getTabIcon();
			if (ico != null) {
				if (tab == activeTab)
					bindTexture(ResourceAccess.getNamedResource(ResourceAccess
							.formatResourceName("textures/gui/components/button_down_${TEX_QUALITY}.png")));
				else
					bindTexture(ResourceAccess.getNamedResource(ResourceAccess
							.formatResourceName("textures/gui/components/button_up_${TEX_QUALITY}.png")));
				drawTexturedRect(0, y, 16, 16);
				bindTexture(ico);
				drawTexturedRect(2, y + 2, 12, 12);
				if (tips && mouseY > y + guiTop && mouseY < y + guiTop + 16) {
					drawTooltip(tab.getTabName(), (int) (mouseX - stop - 2), mouseY - guiTop);
				}
			} else
				LCLog.warn("Problem rendering tab %s: tab icon null!", tab.getClass().getName());
		}
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glPopMatrix();

		GL11.glPushMatrix();
		Iterator<Popover> popovers = this.popovers.iterator();
		int qx = 16;
		while (popovers.hasNext()) {
			Popover over = popovers.next();
			qx += over.render(Minecraft.getMinecraft(), this, width / 2, height - qx, 80, 300.0f);
		}
		GL11.glPopMatrix();
		GL11.glDisable(GL11.GL_BLEND);
	}

	@SuppressWarnings("rawtypes")
	public void drawTooltip(String what, int x, int y) {
		List l0 = Lists.newArrayList(what);
		drawHoveringText(l0, x, y, fontRendererObj);
		RenderHelper.disableStandardItemLighting();
		GL11.glEnable(GL11.GL_BLEND);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		GL11.glEnable(GL11.GL_BLEND);
		if (activeTab != null)
			activeTab.drawForegroundLayer(this, mouseX, mouseY);
		GL11.glDisable(GL11.GL_BLEND);
	}

	@Override
	protected void keyTyped(char c, int key) {
		//super.keyTyped(c, key);
		if (activeTab != null)
			activeTab.keyTyped(this, c, key);
		if (key == Keyboard.KEY_ESCAPE)
			close();
	}

	@Override
	protected void mouseClicked(int x, int y, int mouseButton) {
		super.mouseClicked(x, y, mouseButton);
		if (mouseButton == 0)
			mouseDown = true;
		if (activeTab != null)
			activeTab.mouseClicked(this, x, y, mouseButton);
		if (x > guiLeft + xSize && x < guiLeft + xSize + 16) {
			int where = y - guiTop;
			int which = (int) Math.floor(where / 16);
			if (which >= 0 && which < getTabs().size()) {
				switchTab(which);
			}
		}
	}

	@Override
	protected void mouseMovedOrUp(int x, int y, int mouseButton) {
		super.mouseMovedOrUp(x, y, mouseButton);
		if (activeTab != null)
			activeTab.mouseMovedOrUp(this, x, y, mouseButton);
		if (mouseButton == 0 || mouseButton == 1)
			mouseDown = false;
	}

	@Override
	public void drawScreen(int par1, int par2, float par3) {
		resetColor();
		textColor = defaultTextColor;
		textShadow = false;
		super.drawScreen(par1, par2, par3);
	}

	/**
	 * Close the interface.
	 */
	protected void close() {
		if (activeTab != null)
			activeTab.onTabClosed(this);
		tabVboBuffer.delete();
		mc.thePlayer.closeScreen();
	}

	/**
	 * Draw a set of framed symbols
	 *
	 * @param x
	 *            The x-coordinate
	 * @param y
	 *            The y-coordinate
	 * @param type
	 *            The frame set type
	 * @param address
	 *            The address string
	 */
	public void drawFramedSymbols(int x, int y, StargateType type, char[] address) {
		int scale = 2;
		ResourceLocation tex = null;
		if (type.getSuffix().length() != 0)
			tex = ResourceAccess.getNamedResource("textures/gui/symbols/symbol_frame_" + type.getSuffix() + ".png");
		else
			tex = ResourceAccess.getNamedResource("textures/gui/symbols/symbol_frame.png");
		bindTexture(tex, 512 / scale, 128 / scale);
		drawTexturedRect(x - 472 / scale / 2, y, 472 / scale, 88 / scale, 0, 0);
		StargateGlyphRenderer.drawAddress(mc, type, address, x - 472 / scale / 2, y, 8, scale, zLevel);
	}

	/**
	 * Draw an address string
	 *
	 * @param x
	 *            The x-coordinate
	 * @param y
	 *            The y-coordinate
	 * @param address
	 *            The address string
	 * @param len
	 *            The length of the string
	 * @param padding
	 *            The padding to apply
	 * @param caret
	 *            The caret to display when the address does not match the
	 *            length
	 */
	public void drawAddressString(int x, int y, String address, int len, String padding, String caret) {
		StringBuilder result = new StringBuilder();
		result.append(address);
		if (len != result.length() && caret != null)
			result.append(caret);
		while (len > result.length())
			result.append(padding);
		drawCenteredString(fontRendererObj, result.toString(), x, y, 0xffffff);
	}

	/**
	 * Bind a texture
	 *
	 * @param rsrc
	 *            The texture path
	 */
	public void bindTexture(ResourceLocation rsrc) {
		bindTexture(rsrc, 1, 1);
	}

	/**
	 * Bind a texture
	 *
	 * @param rsrc
	 *            The texture path
	 * @param usize
	 *            The u-scale size
	 * @param vsize
	 *            The v-scale size
	 */
	public void bindTexture(ResourceLocation rsrc, int usize, int vsize) {
		mc.getTextureManager().bindTexture(rsrc);
		uscale = 1.0 / usize;
		vscale = 1.0 / vsize;
	}

	/**
	 * Draw a textured rectangle
	 *
	 * @param x
	 *            The x-coordinate
	 * @param y
	 *            The y-coordinate
	 * @param w
	 *            The width
	 * @param h
	 *            The height
	 */
	public void drawTexturedRect(double x, double y, double w, double h) {
		drawTexturedRectUV(x, y, w, h, 0, 0, 1, 1);
	}

	/**
	 * Draw a textured rectangle
	 *
	 * @param x
	 *            The x-coordinate
	 * @param y
	 *            The y-coordinate
	 * @param w
	 *            The width
	 * @param h
	 *            The height
	 * @param u
	 *            The u-coordinate
	 * @param v
	 *            The v-coordinate
	 */
	public void drawTexturedRect(double x, double y, double w, double h, double u, double v) {
		drawTexturedRect(x, y, w, h, u, v, w, h);
	}

	/**
	 * Draw a textured rectangle
	 *
	 * @param x
	 *            The x-coordinate
	 * @param y
	 *            The y-coordinate
	 * @param w
	 *            The width
	 * @param h
	 *            The height
	 * @param u
	 *            The u-coordinate
	 * @param v
	 *            The v-coordinate
	 * @param us
	 *            The u2-coordinate
	 * @param vs
	 *            The v2-coordinate
	 */
	public void drawTexturedRect(double x, double y, double w, double h, double u, double v, double us, double vs) {
		drawTexturedRectUV(x, y, w, h, u * uscale, v * vscale, us * uscale, vs * vscale);
	}

	/**
	 * Draw an unscaled textured rectangle
	 *
	 * @param x
	 *            The x-coordinate
	 * @param y
	 *            The y-coordinate
	 * @param w
	 *            The width
	 * @param h
	 *            The height
	 * @param u
	 *            The u-coordinate
	 * @param v
	 *            The v-coordinate
	 * @param us
	 *            The u2-coordinate
	 * @param vs
	 *            The v2-coordinate
	 */
	public void drawTexturedRectUV(double x, double y, double w, double h, double u, double v, double us, double vs) {
		Tessellator tess = Tessellator.instance;
		tess.startDrawingQuads();
		tess.setColorOpaque_F(red, green, blue);
		tess.addVertexWithUV(x, y + h, zLevel, u, v + vs);
		tess.addVertexWithUV(x + w, y + h, zLevel, u + us, v + vs);
		tess.addVertexWithUV(x + w, y, zLevel, u + us, v);
		tess.addVertexWithUV(x, y, zLevel, u, v);
		tess.draw();
	}

	public void drawGradientRectUV(int x, int y, int x1, int y1, int c0, int c1) {
		float f = (float) (c0 >> 24 & 255) / 255.0F;
		float f1 = (float) (c0 >> 16 & 255) / 255.0F;
		float f2 = (float) (c0 >> 8 & 255) / 255.0F;
		float f3 = (float) (c0 & 255) / 255.0F;
		float f4 = (float) (c1 >> 24 & 255) / 255.0F;
		float f5 = (float) (c1 >> 16 & 255) / 255.0F;
		float f6 = (float) (c1 >> 8 & 255) / 255.0F;
		float f7 = (float) (c1 & 255) / 255.0F;
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		OpenGlHelper.glBlendFunc(770, 771, 1, 0);
		GL11.glShadeModel(GL11.GL_SMOOTH);
		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();
		tessellator.setColorRGBA_F(f1, f2, f3, f);
		tessellator.addVertex((double) x1, (double) y, (double) this.zLevel);
		tessellator.addVertex((double) x, (double) y, (double) this.zLevel);
		tessellator.setColorRGBA_F(f5, f6, f7, f4);
		tessellator.addVertex((double) x, (double) y1, (double) this.zLevel);
		tessellator.addVertex((double) x1, (double) y1, (double) this.zLevel);
		tessellator.draw();
		GL11.glShadeModel(GL11.GL_FLAT);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}

	/**
	 * Set the drawing color
	 *
	 * @param hex
	 *            A hex color
	 */
	public void setColor(int hex) {
		setColor((hex >> 16) / 255.0, (hex >> 8 & 0xff) / 255.0, (hex & 0xff) / 255.0);
	}

	/**
	 * Set the drawing color
	 *
	 * @param r
	 *            The red component
	 * @param g
	 *            The green component
	 * @param b
	 *            The blue component
	 */
	public void setColor(double r, double g, double b) {
		red = (float) r;
		green = (float) g;
		blue = (float) b;
	}

	public void resetTextColor() {
		textColor = defaultTextColor;
	}

	public void setTextColor(double r, double g, double b) {
		setTextColor(Math.max(0, r) * 255, Math.max(0, g) * 255, Math.max(0, b) * 255);
	}

	public void setTextColor(int r, int g, int b) {
		setTextColor((int) ((r << 16) | (g << 8) | b) & 0xFFFFFF);
	}

	public void setTextColor(int rgb) {
		textColor = (rgb & 0xFFFFFF);
	}

	/**
	 * Reset the drawing color
	 */
	public void resetColor() {
		setColor(1, 1, 1);
	}

	/**
	 * Draw a string
	 *
	 * @param s
	 *            The string
	 * @param x
	 *            The x-coordinate
	 * @param y
	 *            The y-coordinate
	 */
	public void drawString(String s, int x, int y) {
		fontRendererObj.drawString(s, x, y, textColor, textShadow);
	}

	/**
	 * Draw a centered string
	 *
	 * @param s
	 *            The string
	 * @param x
	 *            The x-coordinate
	 * @param y
	 *            The y-coordinate
	 */
	public void drawCenteredString(String s, int x, int y) {
		fontRendererObj.drawString(s, x - fontRendererObj.getStringWidth(s) / 2, y, textColor, textShadow);
	}

	/**
	 * Draw an inventory name
	 *
	 * @param inv
	 *            The inventory
	 * @param x
	 *            The x-coordinate
	 * @param y
	 *            The y-coordinate
	 */
	public void drawInventoryName(IInventory inv, int x, int y) {
		drawString(inventoryName(inv), x, y);
	}

	/**
	 * Draw the player's inventory name
	 */
	public void drawPlayerInventoryName() {
		drawString(playerInventoryName(), 8, ySize - 96 + 2);
	}

	/**
	 * Translate an inventory name
	 *
	 * @param inv
	 *            The inventory
	 * @return The inventory name
	 */
	public static String inventoryName(IInventory inv) {
		return StatCollector.translateToLocal(inv.getInventoryName());
	}

	/**
	 * Translate the player's inventory name
	 *
	 * @return The player's inventory name
	 */
	public static String playerInventoryName() {
		return StatCollector.translateToLocal("container.inventory");
	}

	public boolean isMouseDown() {
		return mouseDown;
	}

	public boolean putTextOnClipboard(String what) {
		try {
			Clipboard destination = Toolkit.getDefaultToolkit().getSystemClipboard();
			destination.setContents(new StringSelection(what), null);
			return true;
		} catch (Throwable t) {
			LCLog.warn("Can't write to clipboard.", t);
			return false;
		}
	}

	public String getTextFromClipboard() {
		try {
			Clipboard source = Toolkit.getDefaultToolkit().getSystemClipboard();
			return (String) source.getData(DataFlavor.stringFlavor);
		} catch (Throwable t) {
			LCLog.warn("Can't read from clipboard.", t);
			return null;
		}
	}

	public void pushPopover(Popover over) {
		popovers.add(over);
	}

	public TileEntity getTile() {
		return tile;
	}

	public void setTile(TileEntity tile) {
		this.tile = tile;
	}
}
