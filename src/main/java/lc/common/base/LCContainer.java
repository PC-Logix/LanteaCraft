package lc.common.base;

import lc.common.LCLog;
import lc.common.base.ux.LCTabbedSlot;
import lc.common.configuration.IConfigure;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

/**
 * Internal base container class.
 *
 * @author AfterLifeLochie
 *
 */
public abstract class LCContainer extends Container implements IConfigure {
	/** Container width */
	public int xSize;
	/** Container height */
	public int ySize;

	/**
	 * Create a new container
	 *
	 * @param width
	 *            The width
	 * @param height
	 *            The height
	 */
	public LCContainer(int width, int height) {
		super();
		xSize = width;
		ySize = height;
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par2) {
		return null;
	}

	/**
	 * Add the player slots to the container in the default location
	 *
	 * @param player
	 *            The player slots
	 */
	public void addPlayerSlots(EntityPlayer player) {
		addPlayerSlots(player, (xSize - 160) / 2, ySize - 82);
	}

	/**
	 * Add the player slots to the container at the specified location
	 *
	 * @param player
	 *            The player slots
	 * @param x
	 *            The x-coordinate
	 * @param y
	 *            The y-coordinate
	 */
	public void addPlayerSlots(EntityPlayer player, int x, int y) {
		InventoryPlayer inventory = player.inventory;
		for (int i = 0; i < 3; ++i)
			for (int j = 0; j < 9; ++j)
				addSlotToContainer(new LCTabbedSlot(inventory, j + i * 9 + 9, x + j * 18, y + i * 18));

		for (int i = 0; i < 9; ++i)
			addSlotToContainer(new LCTabbedSlot(inventory, i, x + i * 18, y + 58));
	}

	@Override
	public Slot addSlotToContainer(Slot slot) {
		if (0 > slot.xDisplayPosition || 0 > slot.yDisplayPosition || slot.xDisplayPosition + 16 > xSize
				|| slot.yDisplayPosition + 16 > ySize)
			LCLog.warn("Slot index %s in inventory %s is offscreen. Problems may occur!", slot.getSlotIndex(),
					slot.inventory.getClass().getName());
		return super.addSlotToContainer(slot);
	}

	@Override
	public boolean canInteractWith(EntityPlayer var1) {
		return true;
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		for (int i = 0; i < crafters.size(); i++) {
			ICrafting crafter = (ICrafting) crafters.get(i);
			sendStateTo(crafter);
		}
	}

	/**
	 * Send an update state to a crafter
	 *
	 * @param crafter
	 *            The crafter person
	 */
	public abstract void sendStateTo(ICrafting crafter);

	@Override
	public void updateProgressBar(int i, int value) {
		// TODO: Handle update events
	}
}
