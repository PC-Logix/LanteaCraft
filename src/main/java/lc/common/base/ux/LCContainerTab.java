package lc.common.base.ux;

import java.awt.Rectangle;

import net.minecraft.util.ResourceLocation;

/**
 * Container tab stub
 *
 * @author AfterLifeLochie
 */
public abstract class LCContainerTab {
	/**
	 * Called by the LCContainerGUI parent when the tab is selected.
	 *
	 * @param container
	 *            The LCContainerGUI parent.
	 */
	protected abstract void onTabOpened(LCContainerGUI container);

	/**
	 * Called by the LCContainerGUI when the tab is deselected.
	 *
	 * @param container
	 *            The LCContainerGUI parent.
	 */
	protected abstract void onTabClosed(LCContainerGUI container);

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
