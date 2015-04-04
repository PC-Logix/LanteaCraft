package lc.gui;

import java.awt.Dimension;
import java.util.HashMap;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import lc.common.base.ux.LCContainerGUI;
import lc.common.base.ux.LCContainerTab;
import lc.container.ContainerDHD;
import lc.container.ContainerStargate;
import lc.tiles.TileDHD;
import lc.tiles.TileStargateBase;

public class GUIDHD extends LCContainerGUI {

	private static class DHDDefaultTab extends LCContainerTab {

		@Override
		protected void onTabOpened(LCContainerGUI container) {
			// TODO Auto-generated method stub

		}

		@Override
		protected void onTabClosed(LCContainerGUI container) {
			// TODO Auto-generated method stub

		}

		@Override
		protected String getTabName() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		protected ResourceLocation getTabIcon() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		protected Dimension getTabDimensions() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		protected void drawBackgroundLayer(LCContainerGUI container, float partialTickCount, int mouseX, int mouseY) {
			// TODO Auto-generated method stub

		}

		@Override
		protected void drawForegroundLayer(LCContainerGUI container, int mouseX, int mouseY) {
			// TODO Auto-generated method stub

		}

		@Override
		protected void mouseClicked(LCContainerGUI container, int x, int y, int mouseButton) {
			// TODO Auto-generated method stub

		}

		@Override
		protected void mouseMovedOrUp(LCContainerGUI container, int x, int y, int mouseButton) {
			// TODO Auto-generated method stub

		}

		@Override
		protected void keyTyped(LCContainerGUI container, char c, int key) {
			// TODO Auto-generated method stub

		}
	}

	private static final HashMap<Integer, LCContainerTab> tabs = new HashMap<Integer, LCContainerTab>();
	static {
		tabs.put(0, new DHDDefaultTab());
	}

	public GUIDHD(TileDHD tile, EntityPlayer player) {
		super(tile, new ContainerDHD(tile, player));
		switchTab(0);
	}

	@Override
	protected HashMap<Integer, LCContainerTab> getTabs() {
		return GUIDHD.tabs;
	}

}
