package lc.gui;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;

import lc.common.LCLog;
import lc.common.resource.ResourceAccess;
import lc.common.stargate.StargateCharsetHelper;
import lc.common.util.ScanningHelper;
import lc.tiles.TileStargateBase;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ResourceLocation;

public class GUIPortableDHD extends GuiScreen {

	final int dhdWidth = 260, dhdHeight = 180;
	final double dhdRadius1 = dhdWidth * 0.1;
	final double dhdRadius2 = dhdWidth * 0.275;
	final double dhdRadius3 = dhdWidth * 0.45;
	private int dhdTop, dhdCentreX, dhdCentreY, ticks = 0;
	private ResourceLocation dhdLayer, dhdButtonLayer;

	private double offX, offY;

	double uscale, vscale;
	float red = 1.0F, green = 1.0F, blue = 1.0F;

	private final EntityPlayer player;
	private TileStargateBase foundTile;

	public GUIPortableDHD(EntityPlayer player) {
		this.player = player;
		dhdLayer = ResourceAccess.getNamedResource("textures/gui/dhd/dhd_gui.png");
		dhdButtonLayer = ResourceAccess.getNamedResource("textures/gui/dhd/dhd_centre.png");
		dhdCentreX = dhdWidth / 2;
		dhdCentreY = dhdHeight / 2;
		dhdTop = 48;
	}

	public TileStargateBase getTile() {
		if (foundTile != null)
			return foundTile;
		TileEntity tile = ScanningHelper.findNearestTileEntityOf(player.worldObj, TileStargateBase.class,
				(int) Math.round(player.posX), (int) Math.round(player.posY), (int) Math.round(player.posZ),
				AxisAlignedBB.getBoundingBox(-5, -5, -5, 5, 5, 5));
		if (tile != null && (tile instanceof TileStargateBase)) {
			foundTile = (TileStargateBase) tile;
			return foundTile;
		}
		return null;
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTickCount) {
		TileStargateBase near = getTile();
		if (near == null)
			mc.thePlayer.closeScreen();
		offX = (width / 2) - dhdCentreX;
		offY = (height / 2) - dhdCentreY;
		if (near != null) {
			drawBackgroundLayer(partialTickCount, mouseX, mouseY);
			drawForegroundLayer(mouseX, mouseY);
		}
	}

	protected void drawBackgroundLayer(float partialTickCount, int mouseX, int mouseY) {
		bindTexture(dhdLayer);
		drawTexturedRect(offX, offY + dhdTop, dhdWidth, dhdHeight);

		bindTexture(dhdButtonLayer, 128, 64);
		GL11.glEnable(GL11.GL_BLEND);

		TileStargateBase stargate = getTile();
		if (stargate.hasConnectionState())
			setColor(1.0, 0.5, 0.0);
		else
			setColor(0.5, 0.25, 0.0);
		double rx = dhdWidth * 48 / 512.0;
		double ry = dhdHeight * 48 / (90.0 + 256.0);
		Tessellator.instance.disableColor();
		drawTexturedRect(offX + dhdCentreX - rx, offY + dhdTop + dhdCentreY - ry + 8, 2 * rx, 1.5 * ry, 64, 0, 64, 48);
		resetColor();
		if (stargate.hasConnectionState()) {
			GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);
			double d = 5;
			drawTexturedRect(offX + dhdCentreX - rx - d, offY + dhdTop + dhdCentreY - ry - d + 8.0d, 2 * (rx + d), ry
					+ d, 0, 0, 64, 32);
			drawTexturedRect(offX + dhdCentreX - rx - d, offY + dhdTop + dhdCentreY + 8.0d, 2 * (rx + d), 0.5 * ry + d,
					0, 32, 64, 32);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		}
	}

	protected void drawForegroundLayer(int mouseX, int mouseY) {
		TileStargateBase dhd = getTile();
		drawAddressString((int) (offX + dhdCentreX), (int) (offY + 48), dhd.getActivatedGlyphs(), 9, " ",
				(ticks > 10) ? "_" : " ");
	}

	@Override
	protected void mouseClicked(int x, int y, int mouseButton) {
		if (mouseButton == 0) {
			int i = findDHDButton((int) (x - offX), (int) (y - offY));
			if (i >= 0) {
				LCLog.debug("dhdClickGet: %s", i);
				dhdButtonPressed(getTile(), i);
				return;
			}
		}
	}

	@Override
	protected void keyTyped(char c, int key) {
		if (key == Keyboard.KEY_ESCAPE) {
			mc.displayGuiScreen((GuiScreen) null);
			mc.setIngameFocus();
		} else if (key == Keyboard.KEY_BACK || key == Keyboard.KEY_DELETE)
			backspace(getTile());
		else if (key == Keyboard.KEY_RETURN || key == Keyboard.KEY_NUMPADENTER)
			orangeButtonPressed(getTile());
		else {
			String C = String.valueOf(c).toUpperCase();
			if (StargateCharsetHelper.singleton().isLegal(C))
				write(getTile(), C.charAt(0));
		}

		if (Keyboard.isKeyDown(29) || Keyboard.isKeyDown(157))
			if (key == Keyboard.KEY_V)
				try {
					String data = getTextFromClipboard();
					for (char c1 : data.toCharArray())
						if (StargateCharsetHelper.singleton().isLegal(c1))
							write(getTile(), c1);
				} catch (Exception e) {
					LCLog.warn("Can't read from clipboard.", e);
				}
	}

	@Override
	public void updateScreen() {
		ticks = (ticks + 1) % 20;
	}

	private int findDHDButton(int mx, int my) {

		double x = -(mx - dhdCentreX);
		double y = -(my - dhdCentreY - dhdTop) * dhdWidth / dhdHeight;
		double r = Math.hypot(x, y);
		LCLog.debug("dhdClick: " + x + ", " + y);
		if (r > dhdRadius3)
			return -1;
		if (r <= dhdRadius1)
			return 0;
		double a = Math.toDegrees(Math.atan2(y, x));
		if (a < 0)
			a += 360;
		int i0 = (r <= dhdRadius2) ? 20 : 1;
		int i = i0 + (int) Math.floor(a * 19.0d / 360.0d);
		return i;
	}

	private void dhdButtonPressed(TileStargateBase tile, int i) {
		if (i == 0)
			orangeButtonPressed(tile);
		else if (i > 38)
			backspace(tile);
		else
			write(tile, StargateCharsetHelper.singleton().index(i - 1));

	}

	private void orangeButtonPressed(TileStargateBase tile) {
		if (!tile.hasConnectionState())
			tile.engageStargate();
		else
			tile.disengageStargate();
	}

	private void backspace(TileStargateBase tile) {
		tile.deactivateChevron();
	}

	private void write(TileStargateBase tile, char c) {
		tile.selectGlyph(c);
		tile.activateChevron();
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

	public void drawAddressString(int x, int y, String address, int len, String padding, String caret) {
		StringBuilder result = new StringBuilder();
		result.append(address);
		if (len != result.length() && caret != null)
			result.append(caret);
		while (len > result.length())
			result.append(padding);
		drawCenteredString(fontRendererObj, result.toString(), x, y, 0xffffff);
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

}
