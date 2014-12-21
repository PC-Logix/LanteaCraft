package lc.gui;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.HashMap;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import lc.common.LCLog;
import lc.common.base.ux.LCContainerGUI;
import lc.common.base.ux.LCContainerTab;
import lc.container.ContainerStargate;
import lc.tiles.TileStargateBase;

public class GUIStargate extends LCContainerGUI {

	public static class StargateDefaultTab extends LCContainerTab {
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
			return "Stargate";
		}

		@Override
		protected ResourceLocation getTabIcon() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		protected Dimension getTabDimensions() {
			return new Dimension(800, 600);
		}

		@Override
		protected void drawBackgroundLayer(LCContainerGUI container, float partialTickCount, int mouseX, int mouseY) {
			// TODO Auto-generated method stub
		}

		@Override
		protected void drawForegroundLayer(LCContainerGUI container, int mouseX, int mouseY) {
			// TODO Auto-generated method stub
			container.drawString("Test!", 0, 0);
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
		tabs.put(0, new StargateDefaultTab());
	}

	public GUIStargate(TileStargateBase tile, EntityPlayer player) {
		super(new ContainerStargate(tile, player));
		switchTab(0);
	}

	@Override
	protected HashMap<Integer, LCContainerTab> getTabs() {
		return GUIStargate.tabs;
	}

}
