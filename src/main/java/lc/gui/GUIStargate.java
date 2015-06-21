package lc.gui;

import java.awt.Dimension;
import java.util.HashMap;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import lc.api.stargate.IStargateAccess;
import lc.common.base.ux.IconButton;
import lc.common.base.ux.LCContainerGUI;
import lc.common.base.ux.LCContainerTab;
import lc.common.base.ux.LCTabbedSlot;
import lc.common.base.ux.Popover;
import lc.common.resource.ResourceAccess;
import lc.container.ContainerStargate;
import lc.tiles.TileStargateBase;

/**
 * Stargate GUI implementation
 * 
 * @author AfterLifeLochie
 *
 */
public class GUIStargate extends LCContainerGUI {

	/** Stargate default tab implementation */
	public static class StargateDefaultTab extends LCContainerTab {
		@Override
		protected void onTabOpened(LCContainerGUI container) {
			for (Object o : container.inventorySlots.inventorySlots)
				if (o instanceof LCTabbedSlot)
					((LCTabbedSlot) o).showSlot();
		}

		@Override
		protected void onTabClosed(LCContainerGUI container) {
			// TODO Auto-generated method stub
		}

		@Override
		protected String getTabName() {
			return I18n.format("lc.interface.stargate.name");
		}

		@Override
		protected ResourceLocation getTabIcon() {
			return ResourceAccess.getNamedResource(ResourceAccess
					.formatResourceName("textures/gui/icons/stargate_${TEX_QUALITY}.png"));
		}

		@Override
		protected Dimension getTabDimensions() {
			return new Dimension(256, 208);
		}

		@Override
		protected void drawBackgroundLayer(LCContainerGUI container, float partialTickCount, int mouseX, int mouseY) {
			container.bindTexture(ResourceAccess.getNamedResource(ResourceAccess
					.formatResourceName("textures/gui/prefabs/sg_gui_${TEX_QUALITY}.png")), 256, 256);
			container.drawTexturedRect(0, 0, container.getXSize(), container.getYSize(), 0, 0);
		}

		@Override
		protected void drawForegroundLayer(LCContainerGUI container, int mouseX, int mouseY) {
			IStargateAccess stargate = (IStargateAccess) container.getTile();
			String address = stargate.getStargateAddress().getAddressString();
			container.drawAddressString(128, 56, address, 9, "-", "-");
			container.drawFramedSymbols(128, 8, stargate.getStargateType(), address.toCharArray());
			IconButton.drawButton(Minecraft.getMinecraft(), "copy", 240, 54, mouseX - container.offsetLeft(), mouseY
					- container.offsetTop(), container.isMouseDown(), 0.75, 1.0f);
			if (IconButton.buttonHovered(240, 54, mouseX - container.offsetLeft(), mouseY - container.offsetTop(), 1.0))
				container.drawTooltip(I18n.format("lc.interface.clipboard.write"), mouseX - container.offsetLeft(),
						mouseY - container.offsetTop());
		}

		@Override
		protected void mouseClicked(LCContainerGUI container, int x, int y, int mouseButton) {
			// TODO Auto-generated method stub
		}

		@Override
		protected void mouseMovedOrUp(LCContainerGUI container, int x, int y, int mouseButton) {
			if (mouseButton == 0 || mouseButton == 1) {
				if (IconButton.buttonDepressed(240, 54, x - container.offsetLeft(), y - container.offsetTop(),
						container.isMouseDown(), 1.0)) {
					IStargateAccess stargate = (IStargateAccess) container.getTile();
					String address = stargate.getStargateAddress().getAddressString();
					boolean result = container.putTextOnClipboard(address);
					if (result)
						container.pushPopover(new Popover(I18n.format("lc.interface.clipboard.write_ok"), 120));
					else
						container.pushPopover(new Popover(I18n.format("lc.interface.clipboard.write_fail"), 120));
				}
			}
		}

		@Override
		protected void keyTyped(LCContainerGUI container, char c, int key) {
			// TODO Auto-generated method stub
		}

		@Override
		protected void update(LCContainerGUI container) {

		}
	}

	/** Stargate options tab implementation */
	public static class StargateRedstoneTab extends LCContainerTab {

		@Override
		protected void onTabOpened(LCContainerGUI container) {
			for (Object o : container.inventorySlots.inventorySlots)
				if (o instanceof LCTabbedSlot)
					((LCTabbedSlot) o).hideSlot();
		}

		@Override
		protected void onTabClosed(LCContainerGUI container) {
			// TODO Auto-generated method stub

		}

		@Override
		protected String getTabName() {
			return I18n.format("lc.interface.redstone.name");
		}

		@Override
		protected ResourceLocation getTabIcon() {
			return ResourceAccess.getNamedResource(ResourceAccess
					.formatResourceName("textures/gui/icons/icon_redstone_${TEX_QUALITY}.png"));
		}

		@Override
		protected Dimension getTabDimensions() {
			return new Dimension(220, 50);
		}

		@Override
		protected void drawBackgroundLayer(LCContainerGUI container, float partialTickCount, int mouseX, int mouseY) {
			container.bindTexture(ResourceAccess.getNamedResource(ResourceAccess
					.formatResourceName("textures/gui/prefabs/blank_gui_128.png")));
			container.drawTexturedRect(0, 0, container.getXSize(), container.getYSize());
		}

		@Override
		protected void drawForegroundLayer(LCContainerGUI container, int mouseX, int mouseY) {
			// IconButton.drawIcon(Minecraft.getMinecraft(), "cross", 3, 5,
			// 0.5f, 1.0f);
			// container.setTextColor(0xFFFFFF);
			// container.drawString("You don't have permission to configure.",
			// 15, 7);
			// container.drawString("Owner: Player1", 15, 22);
			IconButton.drawIcon(Minecraft.getMinecraft(), "icon_iris", 0, 5, 0.5f, 1.0f);
			container.setTextColor(0xFFFFFF);
			container.drawString(I18n.format("lc.interface.options.iris_mode"), 15, 7);
			IconButton.drawButton(Minecraft.getMinecraft(), "icon_ethernet", 85, 5, mouseX - container.offsetLeft(),
					mouseY - container.offsetTop(), container.isMouseDown(), 0.5, 1.0f);
			if (IconButton.buttonHovered(85, 5, mouseX - container.offsetLeft(), mouseY - container.offsetTop(), 0.5))
				container.drawTooltip(I18n.format("lc.interface.redstone.active_ext"), mouseX - container.offsetLeft(),
						mouseY - container.offsetTop());

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

		@Override
		protected void update(LCContainerGUI container) {

		}

	}

	private static final HashMap<Integer, LCContainerTab> tabs = new HashMap<Integer, LCContainerTab>();
	static {
		tabs.put(0, new StargateDefaultTab());
		tabs.put(1, new StargateRedstoneTab());
	}

	/**
	 * Default constructor
	 * 
	 * @param tile
	 *            The base tile
	 * @param player
	 *            The local player
	 */
	public GUIStargate(TileStargateBase tile, EntityPlayer player) {
		super(tile, new ContainerStargate(tile, player));
		switchTab(0);
	}

	@Override
	protected HashMap<Integer, LCContainerTab> getTabs() {
		return GUIStargate.tabs;
	}

}
