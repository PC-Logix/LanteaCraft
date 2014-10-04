package lc.common.base;

import lc.common.util.game.SlotFilter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class LCInventorySlot extends Slot {

	private final boolean readonly;
	private final int slotIndex;

	public LCInventorySlot(IInventory host, int slotIndex, int xDisplayPosition, int yDisplayPosition, boolean readonly) {
		super(host, slotIndex, xDisplayPosition, yDisplayPosition);
		this.readonly = readonly;
		this.slotIndex = slotIndex;
	}

	@Override
	public void putStack(ItemStack par1ItemStack) {
		if (inventory instanceof LCInventory) {
			LCInventory fint = (LCInventory) inventory;
			SlotFilter rule = fint.getFilterRule(slotIndex);
			if (rule == null || rule.test(par1ItemStack))
				super.putStack(par1ItemStack);
		} else
			super.putStack(par1ItemStack);
	}

	@Override
	public boolean isItemValid(ItemStack par1ItemStack) {
		if (inventory instanceof LCInventory) {
			LCInventory fint = (LCInventory) inventory;
			SlotFilter rule = fint.getFilterRule(slotIndex);
			return rule == null || rule.test(par1ItemStack);
		} else
			return super.isItemValid(par1ItemStack);
	}

	@Override
	public boolean canTakeStack(EntityPlayer par1EntityPlayer) {
		return !readonly || super.canTakeStack(par1EntityPlayer);
	}

}