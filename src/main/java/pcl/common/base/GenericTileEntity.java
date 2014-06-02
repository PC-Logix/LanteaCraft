package pcl.common.base;

import java.util.logging.Level;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import pcl.lc.LanteaCraft;

public class GenericTileEntity extends TileEntity implements IInventory, ISidedInventory {

	@Override
	public Packet getDescriptionPacket() {
		if (syncWithClient()) {
			NBTTagCompound nbt = new NBTTagCompound();
			writeToNBT(nbt);
			return new Packet132TileEntityData(xCoord, yCoord, zCoord, 0, nbt);
		} else
			return null;
	}

	@Override
	public void onDataPacket(INetworkManager net, Packet132TileEntityData pkt) {
		readFromNBT(pkt.data);
		markBlockForUpdate();
	}

	boolean syncWithClient() {
		return true;
	}

	public void markBlockForUpdate() {
		if (worldObj != null)
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	public void playSoundEffect(String name, float volume, float pitch) {
		if (name.contains(":"))
			LanteaCraft.getLogger().log(Level.WARNING, "Old SoundSystem label detected, can't play label: " + name);
		else {
			String label = new StringBuilder().append(LanteaCraft.getAssetKey()).append(":").append(name).toString();
			try {
				ResourceLocation location = new ResourceLocation(label);
				if (Minecraft.getMinecraft().getResourceManager().getResource(location).getInputStream() == null)
					return;
				worldObj.playSoundEffect(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5, label, volume, pitch);
			} catch (Throwable t) {
				LanteaCraft.getLogger().log(Level.FINE, "Couldn't play sound, doesn't exist: " + label, t);
			}
		}
	}

	public IInventory getInventory() {
		return null;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		IInventory inventory = getInventory();
		if (inventory != null) {
			NBTTagList list = (NBTTagList) nbt.getTag("inventory");
			int n = list.tagCount();
			for (int i = 0; i < n; i++) {
				NBTTagCompound item = (NBTTagCompound) list.getCompoundTagAt(i);
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

	/**
	 * Returns the number of slots in the inventory.
	 */
	@Override
	public int getSizeInventory() {
		IInventory inventory = getInventory();
		return inventory != null ? inventory.getSizeInventory() : 0;
	}

	/**
	 * Returns the stack in slot i
	 */
	@Override
	public ItemStack getStackInSlot(int slot) {
		IInventory inventory = getInventory();
		return inventory != null ? inventory.getStackInSlot(slot) : null;
	}

	/**
	 * Removes from an inventory slot (first arg) up to a specified number
	 * (second arg) of items and returns them in a new stack.
	 */
	@Override
	public ItemStack decrStackSize(int slot, int amount) {
		IInventory inventory = getInventory();
		if (inventory != null) {
			ItemStack result = inventory.decrStackSize(slot, amount);
			onInventoryChanged();
			return result;
		} else
			return null;
	}

	/**
	 * When some containers are closed they call this on each slot, then drop
	 * whatever it returns as an EntityItem - like when you close a workbench
	 * GUI.
	 */
	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		IInventory inventory = getInventory();
		if (inventory != null) {
			ItemStack result = inventory.getStackInSlotOnClosing(slot);
			onInventoryChanged();
			return result;
		} else
			return null;
	}

	/**
	 * Sets the given item stack to the specified slot in the inventory (can be
	 * crafting or armor sections).
	 */
	@Override
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
	@Override
	public String getInventoryName() {
		IInventory inventory = getInventory();
		return inventory != null ? inventory.getInventoryName() : "";
	}

	/**
	 * Returns the maximum stack size for a inventory slot. Seems to always be
	 * 64, possibly will be extended. *Isn't this more of a set than a get?*
	 */
	@Override
	public int getInventoryStackLimit() {
		IInventory inventory = getInventory();
		return inventory != null ? inventory.getInventoryStackLimit() : 0;
	}

	/**
	 * Do not make give this method the name canInteractWith because it clashes
	 * with Container
	 */
	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		IInventory inventory = getInventory();
		return inventory != null ? inventory.isUseableByPlayer(player) : true;
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		IInventory inventory = getInventory();
		if (inventory != null)
			return inventory.isItemValidForSlot(slot, stack);
		else
			return false;
	}

	/**
	 * Returns an array containing the indices of the slots that can be accessed
	 * by automation on the given side of this block.
	 */
	@Override
	public int[] getAccessibleSlotsFromSide(int side) {
		IInventory inventory = getInventory();
		if (inventory instanceof ISidedInventory)
			return ((ISidedInventory) inventory).getAccessibleSlotsFromSide(side);
		else
			return new int[0];
	}

	/**
	 * Returns true if automation can insert the given item in the given slot
	 * from the given side. Args: Slot, item, side
	 */
	@Override
	public boolean canInsertItem(int slot, ItemStack stack, int side) {
		IInventory inventory = getInventory();
		if (inventory instanceof ISidedInventory)
			return ((ISidedInventory) inventory).canInsertItem(slot, stack, side);
		else
			return true;
	}

	/**
	 * Returns true if automation can extract the given item in the given slot
	 * from the given side. Args: Slot, item, side
	 */
	@Override
	public boolean canExtractItem(int slot, ItemStack stack, int side) {
		IInventory inventory = getInventory();
		if (inventory instanceof ISidedInventory)
			return ((ISidedInventory) inventory).canExtractItem(slot, stack, side);
		else
			return true;
	}

	@Override
	public boolean hasCustomInventoryName() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void openInventory() {
		// TODO Auto-generated method stub

	}

	@Override
	public void closeInventory() {
		// TODO Auto-generated method stub

	}

}
