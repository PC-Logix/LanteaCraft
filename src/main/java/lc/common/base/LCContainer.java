package lc.common.base;

import lc.common.LCLog;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public abstract class LCContainer extends Container {
	public int xSize;
	public int ySize;

	public LCContainer(int width, int height) {
		super();
		xSize = width;
		ySize = height;
	}

	public void addPlayerSlots(EntityPlayer player) {
		addPlayerSlots(player, (xSize - 160) / 2, ySize - 82);
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par2) {
		return null;
	}

	public void addPlayerSlots(EntityPlayer player, int x, int y) {
		InventoryPlayer inventory = player.inventory;
		for (int i = 0; i < 3; ++i)
			for (int j = 0; j < 9; ++j)
				addSlotToContainer(new Slot(inventory, j + i * 9 + 9, x + j * 18, y + i * 18));

		for (int i = 0; i < 9; ++i)
			addSlotToContainer(new Slot(inventory, i, x + i * 18, y + 58));
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

	public abstract void sendStateTo(ICrafting crafter);

	@Override
	public void updateProgressBar(int i, int value) {
		// TODO: Handle update events
	}
}
