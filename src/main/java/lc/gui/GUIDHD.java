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
import lc.api.stargate.StargateType;
import lc.common.LCLog;
import lc.common.base.ux.LCContainerGUI;
import lc.common.base.ux.LCContainerTab;
import lc.common.resource.ResourceAccess;
import lc.common.stargate.StargateCharsetHelper;
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
		private String enteredAddress = "";

		public DHDDefaultTab() {
			super();
			dhdLayer = ResourceAccess.getNamedResource("textures/gui/dhd_gui.png");
			dhdButtonLayer = ResourceAccess.getNamedResource("textures/gui/dhd_centre.png");
		}

		@Override
		protected void onTabOpened(LCContainerGUI container) {
			dhdTop = container.height - dhdHeight;
			dhdCentreX = container.width / 2;
			dhdCentreY = dhdTop + dhdHeight / 2;
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
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		protected void drawBackgroundLayer(LCContainerGUI container, float partialTickCount, int mouseX, int mouseY) {
			container.drawTexturedRect((container.width - dhdWidth) / 2, container.height - dhdHeight, dhdWidth,
					dhdHeight);

			container.bindTexture(dhdButtonLayer, 128, 64);
			GL11.glEnable(GL11.GL_BLEND);
			TileDHD dhd = (TileDHD) container.getTile();
			if (dhd.ownsConnection())
				container.setColor(1.0, 0.5, 0.0);
			else
				container.setColor(0.5, 0.25, 0.0);
			double rx = dhdWidth * 48 / 512.0;
			double ry = dhdHeight * 48 / (96.0 + 256.0);
			Tessellator.instance.disableColor();
			container.drawTexturedRect(dhdCentreX - rx, dhdCentreY - ry + 8.0d, 2 * rx, 1.5 * ry, 64, 0, 64, 48);
			container.resetColor();
			if (dhd.ownsConnection()) {
				GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);
				double d = 5;
				container.drawTexturedRect(dhdCentreX - rx - d, dhdCentreY - ry - d + 8.0d, 2 * (rx + d), ry + d, 0, 0,
						64, 32);
				container.drawTexturedRect(dhdCentreX - rx - d, dhdCentreY + 8.0d, 2 * (rx + d), 0.5 * ry + d, 0, 32,
						64, 32);
				GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			}
		}

		@Override
		protected void drawForegroundLayer(LCContainerGUI container, int mouseX, int mouseY) {
			TileDHD dhd = (TileDHD) container.getTile();
			container.drawFramedSymbols(container.width / 2, dhdTop - 60, dhd.getDHDType(), enteredAddress);
			container.drawAddressString(container.width / 2, dhdTop - 12, enteredAddress, 9, " ", (ticks > 10) ? "_"
					: " ");
		}

		@Override
		protected void mouseClicked(LCContainerGUI container, int x, int y, int mouseButton) {
			if (mouseButton == 0) {
				int i = findDHDButton(x, y);
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
				backspace();
			else if (key == Keyboard.KEY_RETURN || key == Keyboard.KEY_NUMPADENTER)
				orangeButtonPressed((TileDHD) container.getTile());
			else {
				String C = String.valueOf(c).toUpperCase();
				if (StargateCharsetHelper.singleton().isLegal(C))
					write(C.charAt(0));
			}

			if (Keyboard.isKeyDown(29) || Keyboard.isKeyDown(157))
				if (key == Keyboard.KEY_V)
					try {
						String data = container.getTextFromClipboard();
						for (char c1 : data.toCharArray())
							if (StargateCharsetHelper.singleton().isLegal(c1))
								write(c1);
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
			double y = -(my - dhdCentreY) * dhdWidth / dhdHeight;
			double r = Math.hypot(x, y);
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
				backspace();
			else
				write(StargateCharsetHelper.singleton().index(i - 1));

		}

		private void orangeButtonPressed(TileDHD tile) {
			if (tile.ownsConnection())
				tile.clientDoHangUp();
			else
				tile.clientDoOpenConnection(enteredAddress);

		}

		private void backspace() {
			if (enteredAddress.length() > 0)
				enteredAddress = enteredAddress.substring(0, enteredAddress.length() - 1);
		}

		private void write(char c) {
			if (enteredAddress.length() < 9)
				enteredAddress = enteredAddress + c;
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
