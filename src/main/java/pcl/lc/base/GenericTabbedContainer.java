package pcl.lc.base;

import java.util.HashMap;

import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.util.Rectangle;

public abstract class GenericTabbedContainer extends GenericContainerGUI {

	/**
	 * Container tab stub
	 * 
	 * @author AfterLifeLochie
	 */
	public abstract class ContainerTab {

		/**
		 * Called by the GenericTabbedContainer parent when the tab is selected.
		 * 
		 * @param container
		 *            The GenericTabbedContainer parent.
		 */
		protected abstract void onTabOpened(GenericTabbedContainer container);

		/**
		 * Called by the GenericTabbedContainer when the tab is deselected.
		 * 
		 * @param container
		 *            The GenericTabbedContainer parent.
		 */
		protected abstract void onTabClosed(GenericTabbedContainer container);

		/**
		 * Get an un-translated tab name.
		 * 
		 * @return An un-translated tab name.
		 */
		protected abstract String getTabName();

		/**
		 * Get the tab's icon location.
		 * 
		 * @return An absolute resourcelocation to the tab's icon.
		 */
		protected abstract ResourceLocation getTabIcon();

		/**
		 * Get the dimensions of the internal panel area of this tab.
		 * 
		 * @return The internal panel area of this tab.
		 */
		protected abstract Rectangle getTabDimensions();

		/**
		 * Draw the background layer of the tab.
		 * 
		 * @param partialTickCount
		 *            Partial render ticks
		 * @param mouseX
		 *            Mouse x-coord
		 * @param mouseY
		 *            Mouse y-coord
		 */
		protected abstract void drawBackgroundLayer(float partialTickCount, int mouseX, int mouseY);

		/**
		 * Draw the foreground layer of the tab.
		 * 
		 * @param mouseX
		 *            Mouse x-coord
		 * @param mouseY
		 *            Mouse y-coord
		 */
		protected abstract void drawForegroundLayer(int mouseX, int mouseY);

		/**
		 * Called when the mouse is clicked
		 * 
		 * @param x
		 *            The x-coordinate of the click
		 * @param y
		 *            The y-coordinate of the click
		 * @param mouseButton
		 *            Which button was clicked
		 */
		protected abstract void mouseClicked(int x, int y, int mouseButton);

		/**
		 * Called when the mouse is released
		 * 
		 * @param x
		 *            The x-coordinate of the release
		 * @param y
		 *            The y-coordinate of the release
		 * @param mouseButton
		 *            Which button was released
		 */
		protected abstract void mouseMovedOrUp(int x, int y, int mouseButton);

		/**
		 * Called when a key is typed
		 * 
		 * @param c
		 *            The charcode of the key if any
		 * @param key
		 *            The key number
		 */
		protected abstract void keyTyped(char c, int key);

	}

	private HashMap<Integer, ContainerTab> tabList;
	private ContainerTab activeTab;

	public GenericTabbedContainer(Container container, int width, int height) {
		super(container, width, height);
		tabList = getTabs();
		switchTab(0);
	}

	protected abstract HashMap<Integer, ContainerTab> getTabs();

	public void switchTab(int to) {
		ContainerTab nextTab = tabList.get(to);
		if (nextTab == null)
			nextTab = tabList.get(0);

		if (activeTab != null)
			activeTab.onTabClosed(this);
		activeTab = nextTab;
		activeTab.onTabOpened(this);
	}

	@Override
	protected void drawBackgroundLayer(float partialTickCount, int mouseX, int mouseY) {
		// TODO Auto-generated method stub
		if (activeTab != null)
			activeTab.drawBackgroundLayer(partialTickCount, mouseX, mouseY);
	}

	@Override
	protected void drawForegroundLayer(int mouseX, int mouseY) {
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

}
