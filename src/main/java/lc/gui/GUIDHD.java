package lc.gui;

import java.awt.Dimension;
import java.util.HashMap;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import lc.common.LCLog;
import lc.common.base.ux.LCContainerGUI;
import lc.common.base.ux.LCContainerTab;
import lc.common.resource.ResourceAccess;
import lc.common.stargate.StargateCharsetHelper;
import lc.common.util.data.PrimitiveHelper;
import lc.container.ContainerDHD;
import lc.tiles.TileDHD;

/**
 * DHD GUI implementation
 * 
 * @author AfterLifeLochie
 *
 */
public class GUIDHD extends LCContainerGUI {

	private static class DHDDefaultTab extends LCContainerTab {

		final int dhdWidth = 260, dhdHeight = 180;
		final double dhdRadius1 = dhdWidth * 0.1;
		final double dhdRadius2 = dhdWidth * 0.275;
		final double dhdRadius3 = dhdWidth * 0.45;
		private int dhdTop, dhdCentreX, dhdCentreY, ticks = 0;
		private ResourceLocation dhdLayer, dhdButtonLayer;

		public DHDDefaultTab() {
			super();
			dhdLayer = ResourceAccess.getNamedResource("textures/gui/dhd/dhd_gui.png");
			dhdButtonLayer = ResourceAccess.getNamedResource("textures/gui/dhd/dhd_centre.png");
		}

		@Override
		protected void onTabOpened(LCContainerGUI container) {
			dhdCentreX = dhdWidth / 2;
			dhdCentreY = dhdHeight / 2;
			dhdTop = 48;
		}

		@Override
		protected void onTabClosed(LCContainerGUI container) {
			// TODO Auto-generated method stub

		}

		@Override
		protected String getTabName() {
			return I18n.format("lc.interface.dhd.name");
		}

		@Override
		protected ResourceLocation getTabIcon() {
			return ResourceAccess.getNamedResource(ResourceAccess
					.formatResourceName("textures/gui/icons/stargate_${TEX_QUALITY}.png"));
		}

		@Override
		protected Dimension getTabDimensions() {
			return new Dimension(dhdWidth, dhdHeight + dhdTop);
		}

		@Override
		protected void drawBackgroundLayer(LCContainerGUI container, float partialTickCount, int mouseX, int mouseY) {
			container.bindTexture(dhdLayer);
			container.drawTexturedRect(0, dhdTop, dhdWidth, dhdHeight);

			container.bindTexture(dhdButtonLayer, 128, 64);
			GL11.glEnable(GL11.GL_BLEND);
			TileDHD dhd = (TileDHD) container.getTile();
			if (dhd.clientAskConnectionOpen())
				container.setColor(1.0, 0.5, 0.0);
			else
				container.setColor(0.5, 0.25, 0.0);
			double rx = dhdWidth * 48 / 512.0;
			double ry = dhdHeight * 48 / (90.0 + 256.0);
			Tessellator.instance.disableColor();
			container.drawTexturedRect(dhdCentreX - rx, dhdTop + dhdCentreY - ry + 8, 2 * rx, 1.5 * ry, 64, 0, 64, 48);
			container.resetColor();
			if (dhd.clientAskConnectionOpen()) {
				GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);
				double d = 5;
				container.drawTexturedRect(dhdCentreX - rx - d, dhdTop + dhdCentreY - ry - d + 8.0d, 2 * (rx + d), ry
						+ d, 0, 0, 64, 32);
				container.drawTexturedRect(dhdCentreX - rx - d, dhdTop + dhdCentreY + 8.0d, 2 * (rx + d), 0.5 * ry + d,
						0, 32, 64, 32);
				GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			}
		}

		@Override
		protected void drawForegroundLayer(LCContainerGUI container, int mouseX, int mouseY) {
			TileDHD dhd = (TileDHD) container.getTile();
			char[] glyphs = PrimitiveHelper.unbox(dhd.clientAskEngagedGlpyhs());
			container.drawFramedSymbols(dhdCentreX, 0, dhd.getDHDType(), glyphs);
			container.drawAddressString(dhdCentreX, 48, PrimitiveHelper.flatten(glyphs), 9, " ", (ticks > 10) ? "_"
					: " ");
		}

		@Override
		protected void mouseClicked(LCContainerGUI container, int x, int y, int mouseButton) {
			if (mouseButton == 0) {
				int i = findDHDButton(x - container.offsetLeft(), y - container.offsetTop());
				if (i >= 0) {
					dhdButtonPressed((TileDHD) container.getTile(), i);
					return;
				}
			}
		}

		@Override
		protected void mouseMovedOrUp(LCContainerGUI container, int x, int y, int mouseButton) {
			// TODO Auto-generated method stub

		}

		@Override
		protected void keyTyped(LCContainerGUI container, char c, int key) {
			if (key == Keyboard.KEY_ESCAPE) {
				container.mc.displayGuiScreen((GuiScreen) null);
				container.mc.setIngameFocus();
			} else if (key == Keyboard.KEY_BACK || key == Keyboard.KEY_DELETE)
				backspace((TileDHD) container.getTile());
			else if (key == Keyboard.KEY_RETURN || key == Keyboard.KEY_NUMPADENTER)
				orangeButtonPressed((TileDHD) container.getTile());
			else {
				String C = String.valueOf(c).toUpperCase();
				if (StargateCharsetHelper.singleton().isLegal(C))
					write((TileDHD) container.getTile(), C.charAt(0));
			}

			if (Keyboard.isKeyDown(29) || Keyboard.isKeyDown(157))
				if (key == Keyboard.KEY_V)
					try {
						String data = container.getTextFromClipboard();
						for (char c1 : data.toCharArray())
							if (StargateCharsetHelper.singleton().isLegal(c1))
								write((TileDHD) container.getTile(), c1);
					} catch (Exception e) {
						LCLog.warn("Can't read from clipboard.", e);
					}
		}

		@Override
		protected void update(LCContainerGUI container) {
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

		private void dhdButtonPressed(TileDHD tile, int i) {
			if (i == 0)
				orangeButtonPressed(tile);
			else if (i > 38)
				backspace(tile);
			else
				write(tile, StargateCharsetHelper.singleton().index(i - 1));

		}

		private void orangeButtonPressed(TileDHD tile) {
			tile.clientDoPressedButton(2, (char) 0);

		}

		private void backspace(TileDHD tile) {
			tile.clientDoPressedButton(1, (char) 0);
		}

		private void write(TileDHD tile, char c) {
			tile.clientDoPressedButton(0, c);
		}
	}

	private static final HashMap<Integer, LCContainerTab> tabs = new HashMap<Integer, LCContainerTab>();
	static {
		tabs.put(0, new DHDDefaultTab());
	}

	/**
	 * Default constructor
	 * 
	 * @param tile
	 *            The base tile
	 * @param player
	 *            The local player
	 */
	public GUIDHD(TileDHD tile, EntityPlayer player) {
		super(tile, new ContainerDHD(tile, player));
		switchTab(0);
	}

	@Override
	protected HashMap<Integer, LCContainerTab> getTabs() {
		return GUIDHD.tabs;
	}

}
