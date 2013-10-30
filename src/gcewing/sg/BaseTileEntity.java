//------------------------------------------------------------------------------------------------
//
//   Greg's Mod Base - Generic Tile Entity
//
//------------------------------------------------------------------------------------------------

package gcewing.sg;

import net.minecraft.entity.player.*;
import net.minecraft.inventory.*;
import net.minecraft.item.*;
import net.minecraft.network.*;
import net.minecraft.nbt.*;
import net.minecraft.network.packet.*;
import net.minecraft.tileentity.*;

import net.minecraftforge.common.*;

public class BaseTileEntity extends TileEntity
	implements BaseMod.ITileEntity, IInventory, ISidedInventory
{

	@Override
	public Packet getDescriptionPacket() {
		//System.out.printf("BaseTileEntity.getDescriptionPacket for %s\n", this);
		if (syncWithClient()) {
			NBTTagCompound nbt = new NBTTagCompound();
			writeToNBT(nbt);
			return new Packet132TileEntityData(xCoord, yCoord, zCoord, 0, nbt);
		}
		else
			return null;
	}

	@Override
	public void onDataPacket(INetworkManager net, Packet132TileEntityData pkt) {
		//System.out.printf("BaseTileEntity.onDataPacket for %s\n", this);
		readFromNBT(pkt.data);
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}
	
	boolean syncWithClient() {
		return true;
	}
	
	public void markBlockForUpdate() {
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		//herp this.worldObj.notifyBlockChange(xCoord, yCoord, zCoord, this.blockType.blockID);
	}
	
	public void playSoundEffect(String name, float volume, float pitch) {
		worldObj.playSoundEffect(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5, name, volume, pitch);
	}
	
	@Override
	public void onAddedToWorld() {
	}
	
	IInventory getInventory() {
		return null;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		IInventory inventory = getInventory();
		if (inventory != null) {
			NBTTagList list = nbt.getTagList("inventory");
			int n = list.tagCount();
			for (int i = 0; i < n; i++) {
				NBTTagCompound item = (NBTTagCompound)list.tagAt(i);
				int slot = item.getInteger("slot");
				ItemStack stack = ItemStack.loadItemStackFromNBT(item);
				inventory.setInventorySlotContents(slot, stack);
			}
		}
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		IInventory inventory = getInventory();
		if (inventory != null) {
			NBTTagList list = new NBTTagList();
			int n = inventory.getSizeInventory();
			for (int i = 0; i < n; i++) {
				ItemStack stack = inventory.getStackInSlot(i);
				if (stack != null) {
					NBTTagCompound item = new NBTTagCompound();
					item.setInteger("slot", i);
					stack.writeToNBT(item);
					list.appendTag(item);
				}
			}
			nbt.setTag("inventory", list);
		}
	}
	
//------------------------------------- IInventory -----------------------------------------

	/**
	 * Returns the number of slots in the inventory.
	 */
	public int getSizeInventory() {
		IInventory inventory = getInventory();
		return (inventory != null) ? inventory.getSizeInventory() : 0;
	}	

	/**
	 * Returns the stack in slot i
	 */
	public ItemStack getStackInSlot(int slot) {
		IInventory inventory = getInventory();
		return (inventory != null) ? inventory.getStackInSlot(slot) : null;
	}

	/**
	 * Removes from an inventory slot (first arg) up to a specified number (second arg) of items and returns them in a
	 * new stack.
	 */
	public ItemStack decrStackSize(int slot, int amount) {
		IInventory inventory = getInventory();
		if (inventory != null) {
			ItemStack result = inventory.decrStackSize(slot, amount);
			onInventoryChanged();
			return result;
		}
		else
			return null;
	}

	/**
	 * When some containers are closed they call this on each slot, then drop whatever it returns as an EntityItem -
	 * like when you close a workbench GUI.
	 */
	public ItemStack getStackInSlotOnClosing(int slot) {
		IInventory inventory = getInventory();
		if (inventory != null) {
			ItemStack result = inventory.getStackInSlotOnClosing(slot);
			onInventoryChanged();
			return result;
		}
		else
			return null;
	}

	/**
	 * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
	 */
	public void setInventorySlotContents(int slot, ItemStack stack) {
		IInventory inventory = getInventory();
		if (inventory != null) {
			inventory.setInventorySlotContents(slot, stack);
			onInventoryChanged();
		}
	}

	/**
	 * Returns the name of the inventory.
	 */
	public String getInvName() {
		IInventory inventory = getInventory();
		return (inventory != null) ? inventory.getInvName() : "";
	}

	/**
	 * Returns the maximum stack size for a inventory slot. Seems to always be 64, possibly will be extended. *Isn't
	 * this more of a set than a get?*
	 */
	public int getInventoryStackLimit() {
		IInventory inventory = getInventory();
		return (inventory != null) ? inventory.getInventoryStackLimit() : 0;
	}

	/**
	 * Called when an the contents of an Inventory change, usually
	 */
	//void onInventoryChanged();

	/**
	 * Do not make give this method the name canInteractWith because it clashes with Container
	 */
	public boolean isUseableByPlayer(EntityPlayer player) {
		IInventory inventory = getInventory();
		return (inventory != null) ? inventory.isUseableByPlayer(player) : true;
	}

	public void openChest() {
		IInventory inventory = getInventory();
		if (inventory != null)
			inventory.openChest();
	}

	public void closeChest() {
		IInventory inventory = getInventory();
		if (inventory != null)
			inventory.closeChest();
	}
	
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		IInventory inventory = getInventory();
		if (inventory != null)
			return inventory.isItemValidForSlot(slot, stack);
		else
			return false;
	}
	
	public boolean isInvNameLocalized() {
		IInventory inventory = getInventory();
		if (inventory != null)
			return inventory.isInvNameLocalized();
		else
			return false;
	}

//------------------------------------- ISidedInventory -----------------------------------------

	/**
	 * Returns an array containing the indices of the slots that can be accessed by automation on the given side of this
	 * block.
	 */
	public int[] getAccessibleSlotsFromSide(int side) {
		IInventory inventory = getInventory();
		if (inventory instanceof ISidedInventory)
			return ((ISidedInventory)inventory).getAccessibleSlotsFromSide(side);
		else
			return null;
	}

	/**
	 * Returns true if automation can insert the given item in the given slot from the given side. Args: Slot, item,
	 * side
	 */
	public boolean canInsertItem(int slot, ItemStack stack, int side) {
		IInventory inventory = getInventory();
		if (inventory instanceof ISidedInventory)
			return ((ISidedInventory)inventory).canInsertItem(slot, stack, side);
		else
			return true;
	}

	/**
	 * Returns true if automation can extract the given item in the given slot from the given side. Args: Slot, item,
	 * side
	 */
	public boolean canExtractItem(int slot, ItemStack stack, int side) {
		IInventory inventory = getInventory();
		if (inventory instanceof ISidedInventory)
			return ((ISidedInventory)inventory).canExtractItem(slot, stack, side);
		else
			return true;
	}

//--------------------------------- Old ISidedInventory -----------------------------------------

//	/**
//	 * Get the start of the side inventory.
//	 * @param side The global side to get the start of range.
//	 */
//	public int getStartInventorySide(ForgeDirection side) {
//		IInventory inventory = getInventory();
//		if (inventory instanceof ISidedInventory)
//			return ((ISidedInventory)inventory).getStartInventorySide(side);
//		else
//			return 0;
//	}
//
//	/**
//	 * Get the size of the side inventory.
//	 * @param side The global side.
//	 */
//	public int getSizeInventorySide(ForgeDirection side) {
//		IInventory inventory = getInventory();
//		if (inventory instanceof ISidedInventory)
//			return ((ISidedInventory)inventory).getSizeInventorySide(side);
//		else if (inventory != null)
//			return inventory.getSizeInventory();
//		else
//			return 0;
//	}

}
