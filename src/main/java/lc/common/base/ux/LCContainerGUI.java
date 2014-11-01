package lc.common.base.ux;

import java.util.HashMap;

import lc.common.base.LCContainer;
import lc.core.ResourceAccess;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
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

	private HashMap<Integer, LCContainerTab> tabList;
	private LCContainerTab activeTab;

	double uscale, vscale;
	float red = 1.0F, green = 1.0F, blue = 1.0F;
	/** Texture color */
	protected int textColor = defaultTextColor;
	boolean textShadow = false;

	/**
	 * Create a new container GUI
	 * 
	 * @param container
	 *            The parent container
	 * @param width
	 *            The width
	 * @param height
	 *            The height
	 */
	public LCContainerGUI(Container container, int width, int height) {
		super(container);
		xSize = width;
		ySize = height;
	}

	/**
	 * Create a new container GUI
	 * 
	 * @param container
	 *            The parent container
	 */
	public LCContainerGUI(LCContainer container) {
		this(container, container.xSize, container.ySize);
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
		LCContainerTab nextTab = tabList.get(to);
		if (nextTab == null)
			nextTab = tabList.get(0);
		if (activeTab != null)
			activeTab.onTabClosed(this);
		activeTab = nextTab;
		activeTab.onTabOpened(this);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTickCount, int mouseX, int mouseY) {
		// TODO Auto-generated method stub
		if (activeTab != null)
			activeTab.drawBackgroundLayer(partialTickCount, mouseX, mouseY);

	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		// TODO Auto-generated method stub
		if (activeTab != null)
			activeTab.drawForegroundLayer(mouseX, mouseY);
	}

	@Override
	protected void keyTyped(char c, int key) {
		if (activeTab != null)
			activeTab.keyTyped(c, key);
	}

	@Override
	protected void mouseClicked(int x, int y, int mouseButton) {
		if (activeTab != null)
			activeTab.mouseClicked(x, y, mouseButton);
	}

	@Override
	protected void mouseMovedOrUp(int x, int y, int mouseButton) {
		if (activeTab != null)
			activeTab.mouseMovedOrUp(x, y, mouseButton);
	}

	@Override
	public void drawScreen(int par1, int par2, float par3) {
		resetColor();
		textColor = defaultTextColor;
		textShadow = false;
		// FIXME: Some clever overriding is required here in order to change the
		// way
		// slots are rendered (visibility, ghost items, etc)
		super.drawScreen(par1, par2, par3);
	}

	/**
	 * Close the interface.
	 */
	protected void close() {
		if (activeTab != null)
			activeTab.onTabClosed(this);
		mc.thePlayer.closeScreen();
	}

	public void drawFramedSymbols(int x, int y, String address) {
		int scale = 2;
		bindTexture(ResourceAccess.getNamedResource("textures/gui/symbol_frame.png"), 512 / scale, 128 / scale);
		drawTexturedRect(x - (472 / scale) / 2, y, 472 / scale, 88 / scale, 0, 0);
		StargateGlyphRenderer.drawAddress(mc, address, x - (472 / scale) / 2, y, 9, scale, zLevel);
	}

	public void drawAddressString(int x, int y, String address, int len, String padding, String caret) {
		StringBuilder result = new StringBuilder();
		result.append(address);
		if (len != result.length() && caret != null)
			result.append(caret);
		while (len > result.length())
			result.append(padding);
		drawCenteredString(fontRendererObj, result.toString(), x, y, 0xffffff);
	}

	public void bindTexture(ResourceLocation rsrc) {
		bindTexture(rsrc, 1, 1);
	}

	public void bindTexture(ResourceLocation rsrc, int usize, int vsize) {
		mc.getTextureManager().bindTexture(rsrc);
		uscale = 1.0 / usize;
		vscale = 1.0 / vsize;
	}

	public void drawTexturedRect(double x, double y, double w, double h) {
		drawTexturedRectUV(x, y, w, h, 0, 0, 1, 1);
	}

	public void drawTexturedRect(double x, double y, double w, double h, double u, double v) {
		drawTexturedRect(x, y, w, h, u, v, w, h);
	}

	public void drawTexturedRect(double x, double y, double w, double h, double u, double v, double us, double vs) {
		drawTexturedRectUV(x, y, w, h, u * uscale, v * vscale, us * uscale, vs * vscale);
	}

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

	public void setColor(int hex) {
		setColor((hex >> 16) / 255.0, (hex >> 8 & 0xff) / 255.0, (hex & 0xff) / 255.0);
	}

	public void setColor(double r, double g, double b) {
		red = (float) r;
		green = (float) g;
		blue = (float) b;
	}

	public void resetColor() {
		setColor(1, 1, 1);
	}

	public void drawString(String s, int x, int y) {
		fontRendererObj.drawString(s, x, y, textColor, textShadow);
	}

	public void drawCenteredString(String s, int x, int y) {
		fontRendererObj.drawString(s, x - fontRendererObj.getStringWidth(s) / 2, y, textColor, textShadow);
	}

	public void drawInventoryName(IInventory inv, int x, int y) {
		drawString(inventoryName(inv), x, y);
	}

	public void drawPlayerInventoryName() {
		drawString(playerInventoryName(), 8, ySize - 96 + 2);
	}

	public static String inventoryName(IInventory inv) {
		return StatCollector.translateToLocal(inv.getInventoryName());
	}

	public static String playerInventoryName() {
		return StatCollector.translateToLocal("container.inventory");
	}
}
