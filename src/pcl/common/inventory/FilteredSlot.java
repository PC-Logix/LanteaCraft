package pcl.common.inventory;

import java.util.logging.Level;

import pcl.lc.LanteaCraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class FilteredSlot extends Slot {

	private final boolean readonly;
	private final int slotIndex;

	public FilteredSlot(IInventory host, int slotIndex, int xDisplayPosition, int yDisplayPosition, boolean readonly) {
		super(host, slotIndex, xDisplayPosition, yDisplayPosition);
		this.readonly = readonly;
		this.slotIndex = slotIndex;
	}

	@Override
	public void putStack(ItemStack par1ItemStack) {
		if (inventory instanceof FilteredInventory) {
			FilteredInventory fint = (FilteredInventory) inventory;
			FilterRule rule = fint.getFilterRule(slotIndex);
			if (rule.test(par1ItemStack)) {
				LanteaCraft.getLogger().log(Level.INFO, "Slot rule passed!");
				super.putStack(par1ItemStack);
			} else
				LanteaCraft.getLogger().log(Level.INFO, "Slot rule failed!");
		} else
			super.putStack(par1ItemStack);
	}

	@Override
	public boolean canTakeStack(EntityPlayer par1EntityPlayer) {
		return !readonly || super.canTakeStack(par1EntityPlayer);
	}

}
