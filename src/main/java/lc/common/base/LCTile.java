package lc.common.base;

import lc.common.network.IPacketHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public abstract class LCTile extends TileEntity implements IInventory, IPacketHandler {

	public abstract IInventory getInventory();

	public abstract void thinkClient();

	public abstract void thinkServer();

	@Override
	public int getSizeInventory() {
		if (getInventory() == null)
			return 0;
		return getInventory().getSizeInventory();
	}

	@Override
	public ItemStack getStackInSlot(int p_70301_1_) {
		if (getInventory() == null)
			return null;
		return getInventory().getStackInSlot(p_70301_1_);
	}

	@Override
	public ItemStack decrStackSize(int p_70298_1_, int p_70298_2_) {
		if (getInventory() == null)
			return null;
		return getInventory().decrStackSize(p_70298_1_, p_70298_2_);
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int p_70304_1_) {
		if (getInventory() == null)
			return null;
		return getInventory().getStackInSlotOnClosing(p_70304_1_);
	}

	@Override
	public void setInventorySlotContents(int p_70299_1_, ItemStack p_70299_2_) {
		if (getInventory() == null)
			return;
		getInventory().setInventorySlotContents(p_70299_1_, p_70299_2_);
	}

	@Override
	public String getInventoryName() {
		if (getInventory() == null)
			return null;
		return getInventory().getInventoryName();
	}

	@Override
	public boolean hasCustomInventoryName() {
		if (getInventory() == null)
			return false;
		return getInventory().hasCustomInventoryName();
	}

	@Override
	public int getInventoryStackLimit() {
		if (getInventory() == null)
			return 0;
		return getInventory().getInventoryStackLimit();
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer p_70300_1_) {
		if (getInventory() == null)
			return false;
		return getInventory().isUseableByPlayer(p_70300_1_);
	}

	@Override
	public void openInventory() {
		if (getInventory() == null)
			return;
		getInventory().openInventory();
	}

	@Override
	public void closeInventory() {
		if (getInventory() == null)
			return;
		getInventory().closeInventory();
	}

	@Override
	public boolean isItemValidForSlot(int p_94041_1_, ItemStack p_94041_2_) {
		if (getInventory() == null)
			return false;
		return getInventory().isItemValidForSlot(p_94041_1_, p_94041_2_);
	}

}
